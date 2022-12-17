package game_logic.global.managers

import scala.util.{ Failure, Success, Try }

trait BaseManager[T] {

  def update(updates: Seq[T]): Try[BaseManager[T]] =
    updates.foldLeft(Try(this)) {
      case (Failure(ex), _) => Failure(ex)
      case (Success(previousManager), newUpdate) =>
        previousManager.handleUpdate(newUpdate)
    }

  protected def handleUpdate(update: T): Try[BaseManager[T]]
}
