package commands

import commands.InputValidators.{ isAllNumber, isNonEmpty }
import commands.outcomes.{
  CommandOutcome,
  ConsoleReadMoreInfoOutcome,
  ConsoleSpecialOutcome,
  ConsoleWriteOutcome,
  UnitOutcome,
  UpdateGameOutcome
}
import commands.types.{
  BaseCommand,
  CommandMeta,
  CompoundCommandMeta,
  FlexibleCommandMeta,
  HasInput,
  HasOptionalInput
}
import game_logic.global.GameState
import game_logic.global.game_loop.{
  BaseGameLoop,
  GameLoopParams,
  MainGameLoopParams
}
import game_logic.global.game_loop.prompts.ExitToMenuLoop
import game_logic.global.managers.PlayerManager
import game_logic.item.Notebook
import system.logger.Logger
import system.logger.Logger.log
import ui.console.Console

import scala.concurrent.duration.Duration
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }

/** Default Commands - When adding, you must:
  * 1) Add to ListCommands.action
  * 2) Add to Command.parseLineAsCommand
  */
object DefaultCommands {

  sealed trait DefaultCommand extends BaseCommand

  case class Clear(atSystemTime: Long) extends DefaultCommand {
    def meta: CommandMeta = Clear

    def action: Future[CommandOutcome] = fSucc {
      val f = { c: Console =>
        c.clear()
      }
      ConsoleSpecialOutcome(f, this, atSystemTime)
    }
  }
  object Clear extends CommandMeta {
    override val isGameTimeExempt: Boolean = true
    override val isTurnExempt: Boolean = true
    override val isQueueExempt: Boolean = true

    def name: String = "clear"
    def values: Seq[String] = Seq("clear", "clr", "cls")
    def tooltip: Option[String] = Some(
      "Clears the text up to the top of the console.")
  }

  case class Empty(atSystemTime: Long) extends DefaultCommand {
    def meta: CommandMeta = Empty

    def action: Future[CommandOutcome] = fSucc(UnitOutcome(this, atSystemTime))
  }
  object Empty extends CommandMeta {
    override val isGameTimeExempt: Boolean = true
    override val isTurnExempt: Boolean = true
    override val isQueueExempt: Boolean = true

    def name: String = "empty"
    def tooltip: Option[String] = None
    def values: Seq[String] = Seq("")
  }

  case class Exit(
      previousGameLoop: BaseGameLoop[MainGameLoopParams],
      console: Console,
      timeout: Duration,
      atSystemTime: Long)(implicit ec: ExecutionContext)
      extends DefaultCommand {
    def meta: CommandMeta = Exit

    // TODO: This should return a "CloseGame" outcome, for serialization etc
    def action: Future[CommandOutcome] = fSucc {
      ConsoleReadMoreInfoOutcome(
        promptLoop = new ExitToMenuLoop(previousGameLoop, console, timeout),
        commander = this,
        atSystemTime = atSystemTime)
    }
  }
  object Exit extends CommandMeta {
    override val isGameTimeExempt: Boolean = true
    override val isTurnExempt: Boolean = true
    override val isQueueExempt: Boolean = true

    def name: String = "exit"
    def values: Seq[String] = Seq("exit", "ex", "quit", "q", ":q")
    def tooltip: Option[String] = Some("Exits the program.")
  }

  case class AddNote(
      newNote: String,
      playerNotebook: Notebook,
      atSystemTime: Long)
      extends DefaultCommand {
    def meta: CommandMeta = AddNote

    def action: Future[CommandOutcome] = fSucc {
      UpdateGameOutcome(
        maybeMessage =
          Some(s"Note added as entry number [${playerNotebook.size + 1}]."),
        playerUpdates = Seq(PlayerManager.AddNote(newNote)),
        worldUpdates = Seq.empty, // TODO
        commander = this,
        atSystemTime = atSystemTime
      )
    }
  }
  object AddNote extends FlexibleCommandMeta with HasInput {
    override val isGameTimeExempt: Boolean = true

    def name: String = "add_note"
    def singleValues: Seq[String] = Seq("note", "notes", "memo", "memos")
    def primaryCommandValues: Seq[String] = Seq("add", "new")
    def secondaryCommandValues: Seq[String] = singleValues
    def tooltip: Option[String] =
      Some("Use like '> note \"Your note here.\"' to add to your notebook.")

    def validateInput(inputWords: Seq[String]): Try[Unit] = {
      log(s"In validateFreeformInput with ${inputWords}", Logger.DEBUG)
      for {
        _ <- isNonEmpty(inputWords)(name)
      } yield ()
    }
  }

  // TODO: Add prompt to confirm removal
  case class RemoveNote(entryNum: Seq[Int], atSystemTime: Long)
      extends DefaultCommand {
    def meta: CommandMeta = RemoveNote

    def action: Future[CommandOutcome] = fSucc {
      UpdateGameOutcome(
        maybeMessage = Some(s"Removed entries ${entryNum.mkString(" and ")}"),
        playerUpdates = Seq(PlayerManager.RemoveNote(entryNum: _*)),
        worldUpdates = Seq.empty, // TODO
        commander = this,
        atSystemTime = atSystemTime
      )
    }
  }
  object RemoveNote extends CompoundCommandMeta with HasInput {
    override val isGameTimeExempt: Boolean = true
    def name: String = "remove_note"
    def primaryCommandValues: Seq[String] = Seq("remove", "delete")
    def secondaryCommandValues: Seq[String] = Seq("note", "notes")
    def tooltip: Option[String] =
      Some(
        "Use like '> remove notes 1,4,5' to remove entries 1, 4, and 5 from your notebook.")

    def validateInput(inputWords: Seq[String]): Try[Unit] =
      for {
        _ <- isNonEmpty(inputWords)(name)
        _ <- isAllNumber(inputWords)(name)
      } yield ()
  }

  case class ReadNotebook(playerNotebook: Notebook, atSystemTime: Long)
      extends DefaultCommand {
    def meta: CommandMeta = ReadNotebook

    def action: Future[CommandOutcome] = fSucc {
      val output = playerNotebook.forOutput()
      ConsoleWriteOutcome(output, this, atSystemTime)
    }
  }
  object ReadNotebook extends FlexibleCommandMeta {
    override val isGameTimeExempt: Boolean = true
    def name: String = "read_notebook"
    def tooltip: Option[String] = Some(
      "Review all the notes you've put in your notebook.")

    def singleValues: Seq[String] =
      Seq("notebook", "notes", "memos")
    def primaryCommandValues: Seq[String] =
      Seq("read", "review", "peruse")
    def secondaryCommandValues: Seq[String] = singleValues ++ Seq("note")
  }

  case class CommandHistory(
      params: MainGameLoopParams,
      atSystemTime: Long,
      maybeDepthStr: Option[String] = None)
      extends DefaultCommand {
    val depthNum: Option[Int] = maybeDepthStr.map(_.toInt)

    def meta: CommandMeta = CommandHistory

    def action: Future[CommandOutcome] = fSucc {
      val relevantCommandElements = params.commandHistory(depthNum)
      val asTuples = {
        ("Turn Number", "Command") +:
          relevantCommandElements.map(a =>
            (a.turnNum.toString, a.command.untyped))
      }
      val output = CommandHelpers
        .pad2TuplesToMaxColumnString(
          asTuples,
          delim = " - ",
          centerFirstColumn = true,
          centerSecondColumn = false)
        .mkString("\n")
      ConsoleWriteOutcome(output, this, atSystemTime)
    }
  }
  object CommandHistory extends FlexibleCommandMeta with HasOptionalInput {
    override val isGameTimeExempt: Boolean = true
    def singleValues: Seq[String] = Seq("history")
    def primaryCommandValues: Seq[String] = Seq("command", "commands")
    def secondaryCommandValues: Seq[String] = singleValues
    def name: String = "command_history"
    def tooltip: Option[String] = Some(
      "Display a list of a given amount of entered commands. By default it will be the last 10.")

    def validateInput(inputWords: Seq[String]): Try[Unit] =
      for {
        _ <- InputValidators.isInputSize(inputWords, expected = 1)(name)
        _ <- InputValidators.isAllNumber(inputWords)(name)
      } yield ()
  }

  case class ListCommands(
      customCommands: Seq[BaseCommand] = Seq.empty,
      atSystemTime: Long)
      extends DefaultCommand {
    def meta: CommandMeta = ListCommands

    def action: Future[CommandOutcome] = {
      def tooltipLineTuple(
          commandMeta: CommandMeta): Option[(String, String, String)] = {
        val valuesTrimmed =
          if (commandMeta.values.length > 4)
            commandMeta.values.tail.take(3).mkString(", ") + "..."
          else if (commandMeta.values.length > 1)
            commandMeta.values.tail.mkString(", ")
          else
            commandMeta.values.mkString(", ")

        commandMeta.tooltip.map { tip =>
          (commandMeta.values.head, valuesTrimmed, tip)
        }
      }

      val allTooltipTuples: Seq[(String, String, String)] = Seq(
        Some(("Main Command", "Aliases", "Tooltip")),
        tooltipLineTuple(Clear),
        tooltipLineTuple(Empty),
        tooltipLineTuple(AddNote),
        tooltipLineTuple(RemoveNote),
        tooltipLineTuple(ReadNotebook),
        tooltipLineTuple(Exit),
        tooltipLineTuple(this.meta) // ListCommands
      ).collect { case Some(thing) => thing }
      val allTooltipLines: Seq[String] =
        CommandHelpers.pad3TuplesToMaxColumnString(
          allTooltipTuples,
          delim = " - ",
          centerFirstColumn = false,
          centerSecondColumn = false,
          centerThirdColumn = false)

      val output =
        s"""The following commands may not be a complete list. There may be others to find,
           |but these are the most common commands you will be using. Additionally, most commands
           |have a short-hand that you can use.
           |
           |
           |${allTooltipLines.mkString("\n")}""".stripMargin

      fSucc(ConsoleWriteOutcome(output, this, atSystemTime))
    }
  }
  object ListCommands extends FlexibleCommandMeta {
    override val isGameTimeExempt: Boolean = true
    def name: String = "list_commands"
    def singleValues: Seq[String] = Seq("help")
    def primaryCommandValues: Seq[String] = Seq("list", "l")
    def secondaryCommandValues: Seq[String] =
      Seq("commands", "command", "actions", "action")
    def tooltip: Option[String] = Some(
      "Display a list of all non-hidden commands")
  }

  def fSucc[T]: T => Future[T] = Future.successful _
}
