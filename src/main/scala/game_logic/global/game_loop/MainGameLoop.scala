package game_logic.global.game_loop

import base.DateTime
import commands.CommandHelpers
import commands.outcomes.{
  CommandOutcome,
  ConsoleReadMoreInfoOutcome,
  ConsoleSpecialOutcome,
  ConsoleWriteOutcome,
  RedoCommandOutcome,
  UndoCommandOutcome,
  UnitOutcome,
  UpdateGameStateOutcome
}
import commands.types.Command
import game_logic.global
import game_logic.global.PlayerGameAction
import system.logger.Logger
import system.logger.Logger.log

import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.util.{ Success, Try }

case class MainGameLoop(state: MainGameLoopState)(implicit ec: ExecutionContext)
    extends BaseGameLoop[MainGameLoopState] {
  import state._

  override def setState(
      newState: MainGameLoopState): BaseGameLoop[MainGameLoopState] =
    this.copy(state = newState)

  override def getState: MainGameLoopState = state

  def processCommand(
      outcome: CommandOutcome,
      command: Command): Future[BaseGameLoop[MainGameLoopState]] = {
    val currentGameTime: DateTime = gameState.time
    val newGameTimeAfterCommand: DateTime =
      if (!command.typed.meta.isGameTimeExempt)
        currentGameTime.addMillis(state.gameConfig.millisPerGameTurn)
      else
        currentGameTime
    val action: PlayerGameAction =
      global.PlayerGameAction(
        command,
        turnNum = -1, // Unset, will get updated later
        atSystemTime = outcome.atSystemTime,
        atGameTime = newGameTimeAfterCommand)

    outcome match {
      case UnitOutcome(_, _) =>
        Future.successful {
          MainGameLoop {
            state
              .updateTurn(action)
              .updateGameTime(newGameTimeAfterCommand)
          }
        }

      case ConsoleReadMoreInfoOutcome(prompt, _, _) =>
        Future.successful(prompt)

      case ConsoleWriteOutcome(message, _, _) =>
        Future
          .fromTry(console.writeUntyped(message))
          .map { _ =>
            MainGameLoop {
              state
                .updateTurn(action)
                .updateGameTime(newGameTimeAfterCommand)
            }
          }

      case ConsoleSpecialOutcome(f, _, _) =>
        Future
          .fromTry(f(console))
          .map { _ =>
            MainGameLoop {
              state
                .updateTurn(action)
                .updateGameTime(newGameTimeAfterCommand)
            }
          }

      case UpdateGameStateOutcome(maybeMessage, newGameState, _, _) =>
        maybeMessage.foreach(console.writeUntyped)
        Future
          .fromTry(
            maybeMessage.map(console.writeUntyped).getOrElse(Success(())))
          .map { _ =>
            MainGameLoop {
              state
                .updateTurn(action, newGameState)
                .updateGameTime(newGameTimeAfterCommand)
            }
          }

      case UndoCommandOutcome(newGameState, _, _) =>
        val (stateWithUndone, maybeUndone) = state.undoLastCommand()
        val oldTime =
          maybeUndone.map(_.atGameTime).getOrElse(newGameTimeAfterCommand)
        Future.successful {
          MainGameLoop {
            stateWithUndone
              .updateTurn(action, newGameState)
              .updateGameTime(oldTime)
          }
        }

      case RedoCommandOutcome(newGameState, _, _) =>
        val (stateWithRedone, maybeRedone) = state.redoLastUndo()
        val oldTime =
          maybeRedone.map(_.atGameTime).getOrElse(newGameTimeAfterCommand)
        Future.successful {
          MainGameLoop {
            stateWithRedone
              .updateTurn(action, newGameState)
              .updateGameTime(oldTime)
          }
        }
    }
  }

  def run: BaseGameLoop[MainGameLoopState] = {
    // TODO: Remove this debug line
    lastCommand() match {
      case Some(PlayerGameAction(lastCommand, turnNum, atSystemTime, atGame)) =>
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
        CommandHelpers.parseLineAsCommand(_, state, this, console, runTimeout))
    val futureLoopWithNewState: Future[BaseGameLoop[MainGameLoopState]] =
      for {
        command <- Future.fromTry(parsedCommand)
        action <- command.typed.action
        newLoop <- processCommand(action, command)
      } yield newLoop
    Await.result(futureLoopWithNewState, runTimeout)
  }
}
