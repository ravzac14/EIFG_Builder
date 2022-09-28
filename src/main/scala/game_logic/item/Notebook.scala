package game_logic.item

import system.logger.Logger
import system.logger.Logger.log

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
  def removeNotes(entryNumsToRemove: Int*): Notebook = {
    log(
      s"in Notebook.removeNotes with ${entryNumsToRemove} and before - $this",
      Logger.DEBUG)
    val (outcome: Notebook, _) = {
      entryNumsToRemove.foldLeft((this, 0)) {
        case ((current, loopNum), entryNum) =>
          log(s"in notebook loop with $current and $entryNum")
          (current.removeNote(entryNum - loopNum), loopNum + 1)
      }
    }
    log(
      s"in Notebook.removeNotes with ${entryNumsToRemove} and after - $outcome",
      Logger.DEBUG)
    outcome
  }

  def removeNote(entryNumToRemove: Int): Notebook = {
    log(s"in notebook remove note with $entryNumToRemove", Logger.DEBUG)
    val removed = notes.patch(entryNumToIndex(entryNumToRemove), Nil, 1)
    this.copy(notes = removed)
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

  def empty: Notebook = Notebook(Seq.empty)
}
