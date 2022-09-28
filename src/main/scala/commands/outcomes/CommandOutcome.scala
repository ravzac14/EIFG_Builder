package commands.outcomes

import commands.types.BaseCommand
import game_logic.global.GameState
import game_logic.global.game_loop.{ GameLoopParams, MainGameLoopState }
import game_logic.global.game_loop.prompts.BasePromptLoop
import ui.console.Console

import scala.util.Try

sealed trait CommandOutcome {
  val atSystemTime: Long
  val commander: BaseCommand
}

case class UnitOutcome(commander: BaseCommand, atSystemTime: Long)
    extends CommandOutcome

case class ConsoleWriteOutcome(
    message: String,
    commander: BaseCommand,
    atSystemTime: Long)
    extends CommandOutcome

case class ConsoleSpecialOutcome(
    f: Console => Try[Unit],
    commander: BaseCommand,
    atSystemTime: Long)
    extends CommandOutcome

case class ConsoleReadMoreInfoOutcome(
    promptLoop: BasePromptLoop[MainGameLoopState],
    commander: BaseCommand,
    atSystemTime: Long)
    extends CommandOutcome

case class UpdateGameStateOutcome(
    maybeMessage: Option[String],
    newGameState: GameState,
    commander: BaseCommand,
    atSystemTime: Long
) extends CommandOutcome

case class UndoCommandOutcome(
    newGameState: GameState,
    commander: BaseCommand,
    atSystemTime: Long
) extends CommandOutcome

case class RedoCommandOutcome(
    newGameState: GameState,
    commander: BaseCommand,
    atSystemTime: Long
) extends CommandOutcome
