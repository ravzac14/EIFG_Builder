package ui.console

import scala.concurrent.duration.Duration
import scala.io.StdIn
import scala.util.{ Success, Try }

class StdOutConsole(config: ConsoleConfig) extends Console {
  override def writeUntyped(out: String): Try[Unit] = {
    val outForConsole =
      config.writePrefix.getOrElse("") + out + config.writePostfix.getOrElse("")
    Success(println(outForConsole))
  }

  override def readUntyped(
      overridePrefix: Option[String] = None): Try[String] = {
    def read: String =
      overridePrefix
        .orElse(config.readPrefix)
        .map(StdIn.readLine(_))
        .getOrElse(StdIn.readLine())
    Try(read)
  }

  override def clear(): Try[Unit] = {
    Success {
      print("\u001b[2J")
      print("\u001b[H")
    }
  }

  override def waitForInterrupt(n: Duration): Option[String] =
    throw new UnsupportedOperationException(
      "StdOutConsole does not support time-based input reads")
}
