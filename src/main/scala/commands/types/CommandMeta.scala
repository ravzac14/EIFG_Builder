package commands.types

import commands.CommandHelpers.{ formatSeqAsCommand, formatWord }
import system.logger.Logger
import system.logger.Logger.log

trait CommandMeta {
  // The command should not move the game clock. If it was noon, Jan 6 1986 it should still be.
  val isGameTimeExempt: Boolean = false
  // The command should not move the turn tracker. An invisible counter that lets us do math on command queue
  val isTurnExempt: Boolean = false
  // The command should not be enqueued in command queue. Cannot be undone? Should not be tracked
  val isQueueExempt: Boolean = false

  def name: String
  // These should all be lower case for readability but will be
  // formatted before comparing to input.
  // If it is None, it will not appear in the list commands list
  def tooltip: Option[String]

  def values: Seq[String]

  private def formattedValues: Seq[String] = values.map(formatWord)

  def matches(input: String): Boolean = {
    log(
      s"In `${this.getClass.getName}.matches(String)` with ${input}",
      Logger.DEBUG)
    formattedValues.contains(formatWord(input))
  }

  def matches(input: Seq[String]): Boolean = {
    log(
      s"In `${this.getClass.getName}.matches(Seq[String])` with ${input}",
      Logger.DEBUG)
    formattedValues.contains(formatSeqAsCommand(input))
  }
}
