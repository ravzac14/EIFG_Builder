package commands.outcomes

import commands.types.BaseCommand
import game_logic.global.GameState

sealed trait CommandOutcome {
  val commander: BaseCommand
}

case class UnitOutcome(commander: BaseCommand) extends CommandOutcome

case class ConsoleWriteOutcome(message: String, commander: BaseCommand)
    extends CommandOutcome

case class ConsoleReadMoreInfoOutcome(prompt: String, commander: BaseCommand)
    extends CommandOutcome

case class UpdateGameStateOutcome(
    maybeMessage: Option[String],
    newGameState: GameState,
    commander: BaseCommand
) extends CommandOutcome
