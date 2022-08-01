package commands.types

import commands.outcomes.CommandOutcome

import scala.concurrent.Future

trait BaseCommand {
  def meta: CommandMeta
  def name: String = meta.name
  // These should all be lower case for readability but will be
  // formatted before comparing to input.
  def tooltip: String = meta.tooltip

  // Should the command be displayed when asked for all commands
  def hiddenByDefault: Boolean = false

  def values: Seq[String] = meta.values

  def action: Future[CommandOutcome]
}
