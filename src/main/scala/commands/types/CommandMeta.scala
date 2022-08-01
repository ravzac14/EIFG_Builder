package commands.types

import commands.CommandHelpers.{ formatSeqAsCommand, formatWord }

trait CommandMeta {
  def name: String
  // These should all be lower case for readability but will be
  // formatted before comparing to input.
  def tooltip: String

  def values: Seq[String]

  private def formattedValues: Seq[String] = values.map(formatWord)

  def matches(input: String): Boolean = {
    println(
      s"DEBUG: In `${this.getClass.getName}.matches(String)` with ${input}")
    formattedValues.contains(formatWord(input))
  }

  def matches(input: Seq[String]): Boolean = {
    println(
      s"DEBUG: In `${this.getClass.getName}.matches(Seq[String])` with ${input}")
    formattedValues.contains(formatSeqAsCommand(input))
  }
}
