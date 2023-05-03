package game_logic.global.game_loop

import base.DateTime
import commands.CommandHelpers
import commands.outcomes.{
  CommandOutcome,
  ConsoleReadMoreInfoOutcome,
  UnitOutcome,
  UpdateGameOutcome
}
import commands.types.Command
import game_logic.global
import game_logic.global.PlayerCommand
import game_logic.global.game_loop.MainGameLoopParams.{
  AddToQueue,
  ParamUpdate
}
import game_logic.global.managers.GameManager.{
  StateUpdate,
  UpdateGameTime,
  UpdateTurn
}
import game_logic.global.managers.GameWorldManager.GameWorldUpdate
import game_logic.global.managers.PlayerManager.PlayerUpdate
import system.logger.Logger
import system.logger.Logger.log
import ui.console.Console

import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.util.Try

case class GameplayLoop(params: MainGameLoopParams)(implicit
    ec: ExecutionContext)
    extends BaseGameLoop[MainGameLoopParams] {
  import params._

  override def setParams(
      newState: MainGameLoopParams): BaseGameLoop[MainGameLoopParams] =
    this.copy(params = newState)

  override def getParams: MainGameLoopParams = params

  private def getParamUpdates(action: PlayerCommand): Seq[ParamUpdate] = {
    val currentTurn = gameManager.state.turnNum

    if (!action.command.typed.meta.isQueueExempt)
      Seq(AddToQueue(action.copy(turnNum = currentTurn)))
    else
      Seq.empty
  }

  private def getStateUpdates(
      action: PlayerCommand,
      maybeGameTimeForUpdate: Option[DateTime]): Seq[StateUpdate] = {
    val currentTurn = gameManager.state.turnNum
    val turnUpdates =
      if (!action.command.typed.meta.isTurnExempt)
        Seq(UpdateTurn(newTurnNum = currentTurn + 1))
      else
        Seq.empty
    val timeUpdates =
      maybeGameTimeForUpdate.toSeq.map(t => UpdateGameTime(newTime = t))

    turnUpdates ++ timeUpdates
  }

  private case class CommandEffects(
      maybeNewGameLoop: Option[BaseGameLoop[MainGameLoopParams]],
      paramUpdates: Seq[ParamUpdate],
      stateUpdates: Seq[StateUpdate],
      playerUpdates: Seq[PlayerUpdate],
      worldUpdates: Seq[GameWorldUpdate],
      postUpdateConsoleActions: Seq[Console => Try[Unit]])

  private def processCommand(
      outcome: CommandOutcome,
      command: Command): Future[CommandEffects] = {
    val gameTimeAfterCommand: DateTime =
      gameManager.timeAfterCommand(command)
    val maybeTimeForState =
      Some(gameTimeAfterCommand).filterNot(_ == gameManager.currentTime)
    val action: PlayerCommand =
      global.PlayerCommand(
        command,
        turnNum = -1, // Unset, will get updated later
        atSystemTime = outcome.atSystemTime,
        atGameTime = gameTimeAfterCommand)
    val paramUpdates = getParamUpdates(action)
    val stateUpdates = getStateUpdates(action, maybeTimeForState)
    val preUpdateConsoleActionsResult = Future.sequence {
      outcome.preUpdateConsoleActions.map(f => Future.fromTry(f(console)))
    }

    val effects = outcome match {
      case UnitOutcome(_, _, _, postUpdateConsoleActions) =>
        CommandEffects(
          None,
          paramUpdates,
          stateUpdates,
          Seq.empty,
          Seq.empty,
          postUpdateConsoleActions)

      case ConsoleReadMoreInfoOutcome(
            promptLoop,
            _,
            _,
            _,
            postUpdateConsoleActions) =>
        CommandEffects(
          Some(promptLoop),
          paramUpdates,
          stateUpdates,
          Seq.empty,
          Seq.empty,
          postUpdateConsoleActions)

      case UpdateGameOutcome(
            playerUpdates,
            worldUpdates,
            _,
            _,
            _,
            postUpdateConsoleActions) =>
        CommandEffects(
          None,
          paramUpdates,
          stateUpdates,
          playerUpdates,
          worldUpdates,
          postUpdateConsoleActions)
    }

// TODO add or remove undo/redo
//        case UndoCommandOutcome(_, _) =>
//          val (newQueue, maybeUndone) = params.undoLastCommand()
//          val oldTime =
//            maybeUndone.map(_.atGameTime).getOrElse(gameTimeAfterCommand)
//          val paramUpdates = Seq(
//            UpdateQueue(newQueue),
//            UpdateGameTime
//          )
//          Future.successful((None, Seq.empty, Seq.empty, paramUpdates))
//
//        case RedoCommandOutcome(_, _) =>
//          val (stateWithRedone, maybeRedone) = params.redoLastUndo()
//          val oldTime =
//            maybeRedone.map(_.atGameTime).getOrElse(gameTimeAfterCommand)
//          Future.successful {
//            MainGameLoop {
//              stateWithRedone
//                .updateTurn(action, newGameState)
//                .updateGameTime(oldTime)
//            }
//          }

    preUpdateConsoleActionsResult.map(_ => effects)
  }

  def run: BaseGameLoop[MainGameLoopParams] = {
    // TODO: Remove this debug line
    lastCommand() match {
      case Some(PlayerCommand(lastCommand, turnNum, atSystemTime, atGame)) =>
        log(
          s"At turn [$turnNum] you entered [${lastCommand.untyped}] which was " +
            s"parsed as [${lastCommand.typed.name}] at system time [$atSystemTime] and game time [$atGame]",
          Logger.DEBUG
        )
      case _ =>
    }

    val rawCommand: Try[String] = console.readUntyped()
    val parsedCommand: Try[Command] =
      rawCommand.flatMap(
        CommandHelpers.parseLineAsCommand(_, this, console, runTimeout))
    val futureLoopWithNewState: Future[BaseGameLoop[MainGameLoopParams]] =
      for {
        command <- Future.fromTry(parsedCommand)
        action <- command.typed.action
        commandEffects <- processCommand(action, command)
        newParams <- Future.fromTry {
          params.update(
            commandEffects.paramUpdates,
            commandEffects.stateUpdates,
            commandEffects.worldUpdates,
            commandEffects.playerUpdates)
        }
        _ <- Future.sequence {
          commandEffects.postUpdateConsoleActions.map { f =>
            Future.fromTry(f(console))
          }
        }
        newLoop = commandEffects.maybeNewGameLoop.getOrElse(this)
      } yield newLoop.setParams(newParams)
    Await.result(futureLoopWithNewState, runTimeout)
  }
}
