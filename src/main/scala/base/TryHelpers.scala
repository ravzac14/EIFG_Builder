package base

import scala.util.{ Failure, Success, Try }

object TryHelpers {

  def sequence[T](vs: Seq[Try[T]]): Try[Seq[T]] =
    vs.foldLeft(Try(Seq.empty[T])) {
      case (Failure(ex), _)                    => Failure(ex)
      case (Success(resultsSoFar), Success(v)) => Success(resultsSoFar :+ v)
      case (Success(_), Failure(ex))           => Failure(ex)
    }
}
