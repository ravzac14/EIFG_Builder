package commands

import commands.Exceptions.InvalidFreeformInputException
import commands.FreeformInputValidators.{ isAllNumber, isNonEmpty }
import commands.outcomes.{
  CommandOutcome,
  ConsoleWriteOutcome,
  UnitOutcome,
  UpdateGameStateOutcome
}
import commands.types.{
  BaseCommand,
  CommandMeta,
  CompoundCommandMeta,
  FlexibleCommandMeta,
  HasFreeformInput
}
import game_logic.global.GameState

import scala.concurrent.Future
import scala.util.{ Failure, Success, Try }

/** Default Commands - When adding, you must:
  * 1) Add to ListCommands.action
  * 2) Add to Command.parseLineAsCommand
  */
object DefaultCommands {

  sealed trait DefaultCommand extends BaseCommand

  case class Clear() extends DefaultCommand {
    def meta: CommandMeta = Clear

    def action: Future[CommandOutcome] = fSucc {
      print("\u001b[2J")
      UnitOutcome(this)
    }
  }
  object Clear extends CommandMeta {
    def name: String = "clear"
    def values: Seq[String] = Seq("clear", "clr", "cls")
    def tooltip: String = "Clears the text up to the top of the console."
  }

  case class Exit() extends DefaultCommand {
    def meta: CommandMeta = Exit

    // TODO: This should return a "CloseGame" outcome, for serialization etc
    def action: Future[CommandOutcome] = fSucc {
      System.exit(0)
      UnitOutcome(this)
    }
  }
  object Exit extends CommandMeta {
    def name: String = "exit"
    def values: Seq[String] = Seq("exit", "ex", "quit", "q", ":q")
    def tooltip: String = "Exits the program."
  }

  case class AddNote(newNote: String, gameState: GameState)
      extends DefaultCommand {
    def meta: CommandMeta = AddNote

    def action: Future[CommandOutcome] = fSucc {
      val updatedState =
        gameState.copy(
          characterState = gameState.characterState.copy(
            notebook = gameState.characterState.notebook.addNotes(newNote)
          )
        )
      UpdateGameStateOutcome(
        maybeMessage = Some(
          s"Note added as entry number [${gameState.characterState.notebook.size + 1}]."),
        newGameState = updatedState,
        commander = this)
    }
  }
  object AddNote extends FlexibleCommandMeta with HasFreeformInput {
    def name: String = "add_note"
    def singleValues: Seq[String] = Seq("note", "notes")
    def primaryCommandValues: Seq[String] = Seq("add", "new")
    def secondaryCommandValues: Seq[String] = singleValues
    def tooltip: String =
      "Use like '> note \"Your note here.\"' to add to your notebook."

    def validateFreeformInput(inputWords: Seq[String]): Try[Unit] = {
      println(s"DEBUG: In validateFreeformInput with ${inputWords}")
      for {
        _ <- isNonEmpty(inputWords)(name)
      } yield ()
    }
  }

  // TODO: Add prompt to confirm removal
  case class RemoveNote(entryNum: Seq[Int], gameState: GameState)
      extends DefaultCommand {
    def meta: CommandMeta = RemoveNote

    def action: Future[CommandOutcome] = fSucc {
      val updatedState =
        gameState.copy(
          characterState = gameState.characterState.copy(
            notebook =
              gameState.characterState.notebook.removeNotes(entryNum: _*)
          )
        )
      UpdateGameStateOutcome(
        maybeMessage = Some(s"Removed entries ${entryNum.mkString(" and ")}"),
        newGameState = updatedState,
        commander = this
      )
    }
  }
  object RemoveNote extends CompoundCommandMeta with HasFreeformInput {
    def name: String = "remove_note"
    def primaryCommandValues: Seq[String] = Seq("remove", "delete")
    def secondaryCommandValues: Seq[String] = Seq("note", "notes")
    def tooltip: String =
      "Use like '> remove notes 1,4,5' to remove entries 1, 4, and 5 from your notebook."

    def validateFreeformInput(inputWords: Seq[String]): Try[Unit] =
      for {
        _ <- isNonEmpty(inputWords)(name)
        _ <- isAllNumber(inputWords)(name)
      } yield ()
  }

  case class ReadNotebook(gameState: GameState) extends DefaultCommand {
    def meta: CommandMeta = ReadNotebook

    def action: Future[CommandOutcome] = fSucc {
      val output = gameState.characterState.notebook.forOutput()
      ConsoleWriteOutcome(output, this)
    }
  }
  object ReadNotebook extends FlexibleCommandMeta {
    def name: String = "read_notebook"
    def tooltip: String = "Review all the notes you've put in your notebook."

    def singleValues: Seq[String] =
      Seq("notebook", "notes")
    def primaryCommandValues: Seq[String] =
      Seq("read", "review", "peruse")
    def secondaryCommandValues: Seq[String] = singleValues ++ Seq("note")
  }

  case class ListCommands(customCommands: Seq[BaseCommand] = Seq.empty)
      extends DefaultCommand {
    def meta: CommandMeta = ListCommands

    def action: Future[CommandOutcome] = {
      def tooltipLineTuple(
          commandMeta: CommandMeta): (String, String, String) = {
        val valuesTrimmed =
          if (commandMeta.values.length > 4)
            commandMeta.values.tail.take(3).mkString(", ") + "..."
          else if (commandMeta.values.length > 1)
            commandMeta.values.tail.mkString(", ")
          else
            commandMeta.values.mkString(", ")

        (commandMeta.values.head, valuesTrimmed, commandMeta.tooltip)
      }

      val allTooltipTuples: Seq[(String, String, String)] = Seq(
        ("Main Command", "Aliases", "Tooltip"),
        tooltipLineTuple(Clear),
        tooltipLineTuple(AddNote),
        tooltipLineTuple(RemoveNote),
        tooltipLineTuple(ReadNotebook),
        tooltipLineTuple(Exit),
        tooltipLineTuple(this.meta) // ListCommands
      )
      val maxByTuple: (Int, Int, Int) = (
        allTooltipTuples.map(_._1).maxBy(_.length).length,
        allTooltipTuples.map(_._2).maxBy(_.length).length,
        allTooltipTuples.map(_._3).maxBy(_.length).length
      )
      val allTooltipLines: Seq[String] =
        allTooltipTuples.map { case (first, second, third) =>
          Seq(
            first.padTo(maxByTuple._1 + 1, " ").mkString,
            second.padTo(maxByTuple._2 + 1, " ").mkString,
            third.padTo(maxByTuple._3 + 1, " ").mkString).mkString(" - ")
        }

      val output =
        s"""The following commands may not be a complete list. There may be others to find,
           |but these are the most common commands you will be using. Additionally, most commands
           |have a short-hand that you can use.
           |
           |
           |${allTooltipLines.mkString("\n")}""".stripMargin

      fSucc(ConsoleWriteOutcome(output, this))
    }
  }
  object ListCommands extends CompoundCommandMeta {
    def name: String = "list_commands"
    def primaryCommandValues: Seq[String] = Seq("list", "l")
    def secondaryCommandValues: Seq[String] =
      Seq("commands", "command", "actions", "action")
    def tooltip: String = "Display a list of all non-hidden commands"
  }

  def fSucc[T]: T => Future[T] = Future.successful _
}
