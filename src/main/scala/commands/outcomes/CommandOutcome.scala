package commands.outcomes

import commands.types.BaseCommand
import game_logic.global.game_loop.MainGameLoopParams
import game_logic.global.game_loop.prompts.BasePromptLoop
import game_logic.global.managers.GameWorldManager.GameWorldUpdate
import game_logic.global.managers.PlayerManager.PlayerUpdate
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
    promptLoop: BasePromptLoop[MainGameLoopParams],
    commander: BaseCommand,
    atSystemTime: Long)
    extends CommandOutcome

case class UpdateGameOutcome(
    maybeMessage: Option[String],
    playerUpdates: Seq[PlayerUpdate],
    worldUpdates: Seq[GameWorldUpdate],
    commander: BaseCommand,
    atSystemTime: Long)
    extends CommandOutcome
//TODO
//case class UndoCommandOutcome(commander: BaseCommand, atSystemTime: Long)
//    extends CommandOutcome
//
//case class RedoCommandOutcome(commander: BaseCommand, atSystemTime: Long)
//    extends CommandOutcome
