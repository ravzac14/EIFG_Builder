package commands

import commands.types.{ BaseCommand, Command }
import commands.DefaultCommands._
import commands.Exceptions.{
  MissingFreeformInputException,
  PartialCommandMatchException,
  UnknownInputException
}
import game_logic.global.GameState
import game_logic.global.game_loop.{ BaseGameLoop, MainGameLoopParams }
import game_logic.location.Direction
import system.logger.Logger
import system.logger.Logger.log
import ui.console.Console

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration
import scala.math.Integral.Implicits.infixIntegralOps
import scala.util.{ Failure, Success, Try }

object CommandHelpers {

  val CommandWordDelim = " "

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

  def centerInColumn(i: String, prefixExtra: Boolean = false): String =
    if (i.nonEmpty) {
      val len = i.length
      val trimmedLen = i.trim.length

      if (len != trimmedLen) {
        val (numToPad, extra) = (len - trimmedLen) /% 2
        val extraPrefix = if (prefixExtra && extra > 0) " " else ""
        val extraPostfix = if (!prefixExtra && extra > 0) " " else ""
        extraPrefix +
          "".padTo(numToPad, ' ') +
          i.trim.padTo(trimmedLen + numToPad, ' ') +
          extraPostfix
      } else {
        i
      }
    } else {
      i
    }

  def pad2TuplesToMaxColumnString(
      input: Seq[(String, String)],
      delim: String,
      centerFirstColumn: Boolean,
      centerSecondColumn: Boolean): Seq[String] = {
    val maxByTuple: (Int, Int) = (
      input.map(_._1).maxBy(_.length).length,
      input.map(_._2).maxBy(_.length).length)
    input.map { case (first, second) =>
      val firstColumn = first.padTo(maxByTuple._1 + 1, " ").mkString
      val secondColumn = second.padTo(maxByTuple._2 + 1, " ").mkString
      Seq(
        if (centerFirstColumn) centerInColumn(firstColumn) else firstColumn,
        if (centerSecondColumn) centerInColumn(secondColumn) else secondColumn)
        .mkString(delim)
    }
  }

  def pad3TuplesToMaxColumnString(
      input: Seq[(String, String, String)],
      delim: String,
      centerFirstColumn: Boolean,
      centerSecondColumn: Boolean,
      centerThirdColumn: Boolean): Seq[String] = {
    val maxByTuple: (Int, Int, Int) = (
      input.map(_._1).maxBy(_.length).length,
      input.map(_._2).maxBy(_.length).length,
      input.map(_._3).maxBy(_.length).length)
    input.map { case (first, second, third) =>
      val firstColumn = first.padTo(maxByTuple._1 + 1, " ").mkString
      val secondColumn = second.padTo(maxByTuple._2 + 1, " ").mkString
      val thirdColumn = third.padTo(maxByTuple._3 + 1, " ").mkString
      Seq(
        if (centerFirstColumn) centerInColumn(firstColumn) else firstColumn,
        if (centerSecondColumn) centerInColumn(secondColumn) else secondColumn,
        if (centerThirdColumn) centerInColumn(thirdColumn) else thirdColumn
      )
        .mkString(delim)
    }
  }

  // TODO: Parse number words as Int and validate here
  def isNumber(s: String): Boolean =
    s.forall(_.isDigit)

  // TODO: Read synonyms as any of the synonyms

  // TODO(zack): I should parse these default commands in the same way I expect the other commands
  // to be parsed. That will lead into the best way to be generic here, and as the added benefit of
  // being able to generate helper scala files and stuff, for parse method for example.
  def parseLineAsCommand(
      line: String,
      previousGameLoop: BaseGameLoop[MainGameLoopParams],
      console: Console,
      timeout: Duration,
      customCommands: Seq[BaseCommand] = Seq.empty,
      atSystemTime: Long = System.currentTimeMillis())(implicit
      ec: ExecutionContext): Try[Command] = {
    val gameManager = previousGameLoop.getParams.gameManager

    def asCommand(typed: BaseCommand): Success[Command] =
      Success(Command(line, typed))

    val formatted = formatLine(line)
    log(s"formatted - ${formatted}", Logger.DEBUG)
    formatted match {
      // Empty
      case Seq("") => asCommand(Empty(atSystemTime))

      // Clear
      case Seq(singleInput) if Clear.matches(singleInput) =>
        asCommand(Clear(atSystemTime))
      case whole if Clear.matches(whole) =>
        Failure(new PartialCommandMatchException(whole, Clear))

      // Exit
      case Seq(singleInput) if Exit.matches(singleInput) =>
        asCommand(Exit(previousGameLoop, console, timeout, atSystemTime))
      case whole if Exit.matches(whole) =>
        Failure(new PartialCommandMatchException(whole, Exit))

      // Add Note
      case Seq(singleInput, rest @ _*)
          if rest.nonEmpty &&
            AddNote.matches(singleInput) &&
            !AddNote.matches(Seq(singleInput, rest.head)) &&
            AddNote.validateInput(rest).isSuccess =>
        asCommand(
          AddNote(
            rest.mkString(CommandWordDelim),
            gameManager.playerManager.notebook,
            atSystemTime))
      case Seq(firstInput, secondInput, rest @ _*)
          if AddNote.matches(Seq(firstInput, secondInput)) &&
            AddNote.validateInput(rest).isSuccess =>
        asCommand(
          AddNote(
            rest.mkString(CommandWordDelim),
            gameManager.playerManager.notebook,
            atSystemTime))
      case whole if AddNote.matches(formatSeqAsCommand(whole)) =>
        Failure(new MissingFreeformInputException(whole, AddNote))

      // Remove Note
      case Seq(firstInput, secondInput, rest @ _*)
          if RemoveNote.matches(Seq(firstInput, secondInput)) &&
            RemoveNote
              .validateInput(rest)
              .isSuccess =>
        asCommand(RemoveNote(rest.map(_.toInt), atSystemTime))
      case whole if RemoveNote.matches(formatSeqAsCommand(whole)) =>
        Failure(new MissingFreeformInputException(whole, RemoveNote))

      // Read Notebook
      case whole
          if ReadNotebook.matches(whole) &&
            (whole.length == 1 || whole.length == 2) =>
        asCommand(
          ReadNotebook(gameManager.playerManager.notebook, atSystemTime))
      case whole if ReadNotebook.matches(whole) =>
        Failure(new PartialCommandMatchException(whole, ReadNotebook))

      // Command History
      case Seq(singleInput) if CommandHistory.matches(singleInput) =>
        asCommand(CommandHistory(previousGameLoop.getParams, atSystemTime))
      case Seq(singleInput, rest @ _*)
          if rest.nonEmpty &&
            CommandHistory.matches(singleInput) &&
            !CommandHistory.matches(Seq(singleInput, rest.head)) &&
            CommandHistory.validateInput(rest).isSuccess =>
        asCommand(
          CommandHistory(
            previousGameLoop.getParams,
            atSystemTime,
            Some(rest.head)))
      case Seq(firstInput, secondInput)
          if CommandHistory.matches(Seq(firstInput, secondInput)) =>
        asCommand(CommandHistory(previousGameLoop.getParams, atSystemTime))
      case Seq(firstInput, secondInput, rest @ _*)
          if CommandHistory.matches(Seq(firstInput, secondInput)) &&
            CommandHistory.validateInput(rest).isSuccess =>
        asCommand(
          CommandHistory(
            previousGameLoop.getParams,
            atSystemTime,
            Some(rest.head)))
      case whole if CommandHistory.matches(formatSeqAsCommand(whole)) =>
        Failure(new MissingFreeformInputException(whole, CommandHistory))

      // List Commands
      case whole if ListCommands.matches(whole) =>
        asCommand(ListCommands(customCommands, atSystemTime))

      // Move
      case whole
          if Move.matches(whole) &&
            Move.validateInput(whole).isSuccess &&
            whole.lastOption.nonEmpty &&
            Direction.findByAnyMeans(whole.last).isSuccess =>
        asCommand(
          Move(
            gameManager = gameManager,
            direction = Direction.findByAnyMeans(whole.last).get,
            atSystemTime = atSystemTime))

      // Default - Error
      case whole => Failure(new UnknownInputException(whole))
    }
  }
}
