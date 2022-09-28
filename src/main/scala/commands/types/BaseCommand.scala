package commands.types

import commands.outcomes.CommandOutcome

import scala.concurrent.Future

trait BaseCommand {
  def meta: CommandMeta
  def name: String = meta.name

  // Should the command be displayed when asked for all commands
  def hiddenByDefault: Boolean = false

  def action: Future[CommandOutcome]
}
