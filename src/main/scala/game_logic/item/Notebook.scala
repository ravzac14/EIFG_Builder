package game_logic.item

import game_logic.item.Notebook.NoteDoesNotExistException
import system.logger.Logger
import system.logger.Logger.log

import scala.util.{ Failure, Success, Try }

/** NOTE: Important to note an entryNum is the number a note appears
  *   as to the user, index is the number a note appears in the Seq.
  *   entryNum = index + 1
  */
case class Notebook(notes: Seq[String]) {

  def indexToEntryNum(index: Int): Int = index + 1
  def entryNumToIndex(entryNum: Int): Int = entryNum - 1

  def size: Int = notes.length

  def addNotes(newNotes: String*): Notebook = {
    this.copy(notes = notes ++ newNotes)
  }

  //TODO: test
  def removeNotes(entryNumsToRemove: Int*): Try[Notebook] = {
    log(
      s"in Notebook.removeNotes with ${entryNumsToRemove} and before - $this",
      Logger.DEBUG)
    val (outcome: Try[Notebook], _) = {
      entryNumsToRemove.foldLeft((Try(this), 0)) {
        case ((Success(current), loopNum), entryNum) =>
          log(s"in notebook loop with $current and $entryNum")
          (current.removeNote(entryNum - loopNum), loopNum + 1)
        case (failedUpdate, entryNum) =>
          failedUpdate
      }
    }
    log(
      s"in Notebook.removeNotes with ${entryNumsToRemove} and after - $outcome",
      Logger.DEBUG)
    outcome
  }

  def removeNote(entryNumToRemove: Int): Try[Notebook] = {
    log(s"in notebook remove note with $entryNumToRemove", Logger.DEBUG)
    if (notes.isDefinedAt(entryNumToIndex(entryNumToRemove))) {
      val removed = notes.patch(entryNumToIndex(entryNumToRemove), Nil, 1)
      Success(this.copy(notes = removed))
    } else {
      Failure(new NoteDoesNotExistException(entryNumToRemove))
    }
  }

  def forOutput(): String = {
    val notebookEntries =
      notes.zipWithIndex.map { case (note, index) =>
        s"  ${indexToEntryNum(index)}.  $note"
      }

    s""" --- Notebook ---
       |${notebookEntries.mkString("\n")}""".stripMargin
  }
}

object Notebook {

  class NoteDoesNotExistException(entryNumToRemove: Int)
      extends Exception(s"Note at entry [$entryNumToRemove] does not exist.")

  def empty: Notebook = Notebook(Seq.empty)
}
