package ui.console

import scala.io.StdIn
import scala.util.{ Success, Try }

class StdOutConsole(config: ConsoleConfig) extends Console {
  override def writeUntyped(out: String): Try[Unit] = {
    val outForConsole =
      config.writePrefix.getOrElse("") + out + config.writePostfix.getOrElse("")
    Success(println(outForConsole))
  }

  override def readUntyped(): Try[String] = {
    def read: String =
      config.readPrefix.map(StdIn.readLine(_)).getOrElse(StdIn.readLine())
    Try(read)
  }
}
