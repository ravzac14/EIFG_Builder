package base.data_structures

/** Designed to keep track of "undone" and "redone" queue events
  *   in addition to storing the original elements in the main queue
  *
  * @param queue     - Used as the main store of T's
  * @param undoQueue - Used to keep track of undone actions (and process undos/redos)
  * @tparam T - Inner type of DoQueue
  */
case class DoQueue[T](val queue: List[T], val undoQueue: List[T]) {
  def enqueue(elem: T): DoQueue[T] = this.copy(queue = elem :: queue)

  def undo: DoQueue[T] =
    if (queue.nonEmpty) this.copy(queue = queue.tail, undoQueue = queue.head :: undoQueue)
    else this

  def redo: DoQueue[T] =
    if (undoQueue.nonEmpty) this.copy(queue = undoQueue.head :: queue, undoQueue = undoQueue.tail)
    else this
}
