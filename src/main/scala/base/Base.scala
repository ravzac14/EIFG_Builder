package base

/** Holds the inner life/identifier data for any given object
  * @param id - A unique 32 bit id
  * @param isActive - Describes the life-cycle of an item
  * @tparam T - Should describe the Id type and extend String
  */
class MetaData[T <: String](val id: T, val isActive: Boolean = true)

/** Designed to keep track of "undone" and "redone" queue events
  *   in addition to storing the original elements in the main queue
  * @param queue     - Used as the main store of T's
  * @param undoQueue - Used to keep track of undone actions (and process undos/redos)
  * @tparam T - Inner type of DoQueue
  */
class DoQueue[T](val queue: List[T], val undoQueue: List[T]) {
  def enqueue(elem: T): DoQueue[T] =
    new DoQueue[T](elem :: queue, undoQueue)

  def undo: DoQueue[T] =
    if (queue.nonEmpty)
      new DoQueue[T](queue.tail, queue.head :: undoQueue)
    else this

  def redo: DoQueue[T] =
    if (undoQueue.nonEmpty)
      new DoQueue[T](undoQueue.head :: queue, undoQueue.tail)
    else this
}

/** Safe place for my customer types
  */
package object types {
  // ################### ID type synonyms #####################
  type ActionTakerId = String
}
