package base

/** Holds the inner life/identifier data for any given object
  * @param id - A unique 32 bit id
  * @param isActive - Describes the life-cycle of an item
  * @tparam T - Should describe the Id type and extend String
  */
case class MetaData[T <: String](val id: T, val isActive: Boolean = true) {
  def deactivate() = copy(isActive = false)
  def activate() = copy(isActive = true)
}

/** Designed to keep track of "undone" and "redone" queue events
  *   in addition to storing the original elements in the main queue
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

trait AppWithJsonFormats extends App {
  implicit val formats = org.json4s.DefaultFormats
}

/** Safe place for my custom types
  */
package object types {
  // ################### ID type synonyms #####################
  type ActionTakerId = String
}
