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
  val preUpdateConsoleActions: Seq[Console => Try[Unit]]
  val postUpdateConsoleActions: Seq[Console => Try[Unit]]
}

case class UnitOutcome(
    commander: BaseCommand,
    atSystemTime: Long,
    preUpdateConsoleActions: Seq[Console => Try[Unit]] = Seq.empty,
    postUpdateConsoleActions: Seq[Console => Try[Unit]] = Seq.empty)
    extends CommandOutcome

case class ConsoleReadMoreInfoOutcome(
    promptLoop: BasePromptLoop[MainGameLoopParams],
    commander: BaseCommand,
    atSystemTime: Long,
    preUpdateConsoleActions: Seq[Console => Try[Unit]] = Seq.empty,
    postUpdateConsoleActions: Seq[Console => Try[Unit]] = Seq.empty)
    extends CommandOutcome

case class UpdateGameOutcome(
    playerUpdates: Seq[PlayerUpdate],
    worldUpdates: Seq[GameWorldUpdate],
    commander: BaseCommand,
    atSystemTime: Long,
    preUpdateConsoleActions: Seq[Console => Try[Unit]] = Seq.empty,
    postUpdateConsoleActions: Seq[Console => Try[Unit]] = Seq.empty)
    extends CommandOutcome
//TODO
//case class UndoCommandOutcome(commander: BaseCommand, atSystemTime: Long)
//    extends CommandOutcome
//
//case class RedoCommandOutcome(commander: BaseCommand, atSystemTime: Long)
//    extends CommandOutcome
