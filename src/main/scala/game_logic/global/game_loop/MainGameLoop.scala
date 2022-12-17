package game_logic.global.game_loop

import base.DateTime
import commands.CommandHelpers
import commands.outcomes.{
  CommandOutcome,
  ConsoleReadMoreInfoOutcome,
  ConsoleSpecialOutcome,
  ConsoleWriteOutcome,
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

import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.util.Try

case class MainGameLoop(params: MainGameLoopParams)(implicit
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

  private def getPlayerUpdates(): Seq[PlayerUpdate] = ???

  private def getWorldUpdates(): Seq[GameWorldUpdate] = ???

  /** TODO remove
    * Should delegate the game outcomes of the command to the game manager/state,
    *   ie how the game world changes, how the player changes
    * the built in functions/side effects of the commands to the tools/state,
    *   ie manipulating the command queue, IO, serialization?
    *
    *   TODO WHERE YOU LEFT OFF
    *     REFACTORING THIS TO PASS THE StateUpdates to the GameManager,
    *     and then going on to make PlayerUpdates and WorldUpdates from
    *     the various commands
    */

  case class CommandEffects(
      maybeNewGameLoop: Option[BaseGameLoop[MainGameLoopParams]],
      paramUpdates: Seq[ParamUpdate],
      stateUpdates: Seq[StateUpdate],
      playerUpdates: Seq[PlayerUpdate],
      worldUpdates: Seq[GameWorldUpdate])

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
    val baseParamUpdates = getParamUpdates(action)
    val stateUpdates = getStateUpdates(action, maybeTimeForState)

    val partialResult: Future[(
        Option[BaseGameLoop[MainGameLoopParams]],
        Seq[PlayerUpdate],
        Seq[GameWorldUpdate])] =
      outcome match {
        case UnitOutcome(_, _) =>
          Future.successful((None, Seq.empty, Seq.empty))

        case ConsoleReadMoreInfoOutcome(prompt, _, _) =>
          Future.successful((Some(prompt), Seq.empty, Seq.empty))

        case ConsoleWriteOutcome(message, _, _) =>
          Future
            .fromTry(console.writeUntyped(message))
            .map(_ => (None, Seq.empty, Seq.empty))

        case ConsoleSpecialOutcome(f, _, _) =>
          Future
            .fromTry(f(console))
            .map(_ => (None, Seq.empty, Seq.empty))

        case UpdateGameOutcome(
              maybeMessage,
              playerUpdates,
              worldUpdates,
              _,
              _) =>
          for {
            _ <- Future.sequence {
              maybeMessage.toSeq.map { m =>
                Future.fromTry(console.writeUntyped(m))
              }
            }
          } yield (None, playerUpdates, worldUpdates)

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
      }

    partialResult.map { case (newLoop, playerUpdates, worldUpdates) =>
      CommandEffects(
        maybeNewGameLoop = newLoop,
        paramUpdates = baseParamUpdates,
        stateUpdates = stateUpdates,
        playerUpdates = playerUpdates,
        worldUpdates = worldUpdates)
    }
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
        newLoop = commandEffects.maybeNewGameLoop.getOrElse(this)
      } yield newLoop.setParams(newParams)
    Await.result(futureLoopWithNewState, runTimeout)
  }
}

object MainGameLoop {

  case class LoopUpdate[T <: GameLoopParams](
      newLoop: BaseGameLoop[T],
      current: BaseGameLoop[T])
}
