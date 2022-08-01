package commands

import commands.types.BaseCommand
import commands.DefaultCommands._
import commands.Exceptions.{
  MissingFreeformInputException,
  PartialCommandMatchException,
  UnknownInputException
}
import game_logic.global.GameState

import scala.util.{ Failure, Success, Try }

object CommandHelpers {

  val CommandWordDelim = " "

  case class FormattedWord(word: String)

  case class FormattedLine(words: Seq[String]) {
    def asSingleCommand(): Option[FormattedWord] =
      words.headOption.filter(a => words.length == 1).map(FormattedWord)
  }

  // TODO(zack): This should automatically format out plurals
  def formatWord(s: String): String =
    s.trim.toLowerCase

  // TODO: Consider how this limits the input
  def formatLine(s: String): Seq[String] =
    formatWord(s)
      .replaceAll(",", " ")
      .replaceAll("-+", " ")
      .replaceAll("_+", " ")
      .replaceAll("'", " ")
      .replaceAll("\"", " ")
      .replaceAll("\\|+", " ")
      .replaceAll(" the ", " ")
      .replaceAll(" my ", " ")
      .replaceAll(" a ", " ")
      .replaceAll(" +", " ")
      .split(' ')
      .toSeq
      .map(formatWord)

  def formatSeqAsCommand(s: Seq[String]): String =
    s.map(formatWord).mkString(CommandWordDelim)

  // TODO: Parse number words as Int and validate here
  def isNumber(s: String): Boolean =
    s.forall(_.isDigit)

  // TODO: Read synonyms as any of the synonyms

  // TODO(zack): I should parse these default commands in the same way I expect the other commands
  // to be parsed. That will lead into the best way to be generic here, and as the added benefit of
  // being able to generate helper scala files and stuff, for parse method for example.
  def parseLineAsCommand(
      line: String,
      gameState: GameState,
      customCommands: Seq[BaseCommand] = Seq.empty): Try[BaseCommand] = {
    val formatted = formatLine(line)
    println(s"DEBUG: formatted - ${formatted}")
    formatted match {
      // Clear
      case Seq(singleInput) if Clear.matches(singleInput) => Success(Clear())
      case whole if Clear.matches(whole) =>
        Failure(new PartialCommandMatchException(whole, Clear))

      // Exit
      case Seq(singleInput) if Exit.matches(singleInput) => Success(Exit())
      case whole if Exit.matches(whole) =>
        Failure(new PartialCommandMatchException(whole, Exit))

      // Add Note
      case Seq(singleInput, rest @ _*)
          if rest.nonEmpty &&
            AddNote.matches(singleInput) &&
            !AddNote.matches(Seq(singleInput, rest.head)) &&
            AddNote.validateFreeformInput(rest).isSuccess =>
        Success(AddNote(rest.mkString(CommandWordDelim), gameState))
      case Seq(firstInput, secondInput, rest @ _*)
          if AddNote.matches(Seq(firstInput, secondInput)) &&
            AddNote.validateFreeformInput(rest).isSuccess =>
        Success(AddNote(rest.mkString(CommandWordDelim), gameState))
      case whole if AddNote.matches(formatSeqAsCommand(whole)) =>
        Failure(new MissingFreeformInputException(whole, AddNote))

      // Remove Note
      case Seq(firstInput, secondInput, rest @ _*)
          if RemoveNote.matches(Seq(firstInput, secondInput)) &&
            RemoveNote
              .validateFreeformInput(rest)
              .isSuccess =>
        Success(RemoveNote(rest.map(_.toInt), gameState))
      case whole if RemoveNote.matches(formatSeqAsCommand(whole)) =>
        Failure(new MissingFreeformInputException(whole, RemoveNote))

      // Read Notebook
      case whole
          if ReadNotebook.matches(whole) &&
            (whole.length == 1 || whole.length == 2) =>
        Success(ReadNotebook(gameState))
      case whole if ReadNotebook.matches(whole) =>
        Failure(new PartialCommandMatchException(whole, ReadNotebook))

      // List Commands
      case whole if ListCommands.matches(whole) =>
        Success(ListCommands(customCommands))

      // Default - Error
      case whole => Failure(new UnknownInputException(whole))
    }
  }
}
