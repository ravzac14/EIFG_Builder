package base.data_structures

/** Designed to keep track of "undone" and "redone" queue events
  *   in addition to storing the original elements in the main queue
  *
  * @param queue     - Used as the main store of T's
  * @param undoQueue - Used to keep track of undone actions (and process undos/redos)
  * @tparam T - Inner type of DoQueue
  */
case class DoQueue[T](queue: Seq[T], undoQueue: Seq[T]) {
  def enqueue(elem: T): DoQueue[T] = this.copy(queue = elem +: queue)

  def peak(): Option[T] = queue.headOption
  def peakUndo(): Option[T] = undoQueue.headOption

  def nonEmpty: Boolean = queue.nonEmpty && undoQueue.nonEmpty
  def isEmpty: Boolean = !nonEmpty

  def undo: (Option[T], DoQueue[T]) =
    if (queue.nonEmpty) {
      val undone = queue.headOption
      (
        undone,
        this.copy(queue = queue.tail, undoQueue = queue.head +: undoQueue))
    } else {
      (None, this)
    }

  def redo: (Option[T], DoQueue[T]) = {
    if (undoQueue.nonEmpty) {
      val redone = undoQueue.headOption
      (
        redone,
        this.copy(queue = undoQueue.head +: queue, undoQueue = undoQueue.tail))
    } else {
      (None, this)
    }
  }
}

object DoQueue {

  def empty[T]: DoQueue[T] = DoQueue(Seq.empty[T], Seq.empty[T])
}
