package ui.console

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.Duration
import scala.util.Try

trait Console {

  def writeUntyped(out: String): Try[Unit]

  def writeTyped[T](out: T)(implicit conversion: T => String): Try[Unit] =
    writeUntyped(conversion(out))

  def readUntyped(): Try[String]

  def readTyped[T](implicit conversion: String => T): Try[T] =
    readUntyped().flatMap(in => Try(conversion(in)))

  def clear(): Try[Unit]

  def waitForInterrupt(n: Duration)(implicit
      ec: ExecutionContext): Try[Option[String]]
}
