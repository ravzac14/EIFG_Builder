package ui.console

import scala.concurrent.duration.Duration
import scala.util.Try

trait Console {

  def writeUntyped(out: String): Try[Unit]

  def writeTyped[T](out: T)(implicit conversion: T => String): Try[Unit] =
    writeUntyped(conversion(out))

  def readUntyped(overridePrefix: Option[String] = None): Try[String]

  def readTyped[T](overridePrefix: Option[String] = None)(implicit
      conversion: String => T): Try[T] =
    readUntyped(overridePrefix).flatMap(in => Try(conversion(in)))

  def clear(): Try[Unit]

  def waitForInterrupt(n: Duration): Option[String]
}
