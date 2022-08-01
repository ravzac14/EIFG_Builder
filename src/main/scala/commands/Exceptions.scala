package commands

import commands.CommandHelpers.CommandWordDelim
import commands.types.CommandMeta

object Exceptions {

  class UnknownInputException(strings: Seq[String])
      extends RuntimeException(
        s"Input [${strings.mkString(CommandWordDelim)}] did not match a command")

  class PartialCommandMatchException(strings: Seq[String], command: CommandMeta)
      extends RuntimeException(s"Input [${strings.mkString(
        CommandWordDelim)}] partially matched the command [${command.name}]")

  class MissingFreeformInputException(
      strings: Seq[String],
      commandMeta: CommandMeta)
      extends RuntimeException(
        s"Input [${strings.mkString(CommandWordDelim)}] was missing an expected freeform input component for command [${commandMeta.name}]")

  class InvalidFreeformInputException(
      name: String,
      inputWords: Seq[String],
      reason: String)
      extends RuntimeException(
        s"$name could not parse freeform input words [${inputWords}] for reason [$reason].")
}
