package ui.console

import scala.io._
import scala.sys.process._

// TODO: These might take/go in the ui.console class if I make it
object Console {
  lazy val defaultCommands = Set(ClearCommand, ExitCommand)

  def commandKey(s: String): String = s.trim.toLowerCase

  def isDefault(s: String): Boolean = defaultCommands.contains(commandKey(s))

  def processDefaultCommand(input: String) = {
    require(isDefault(input))

    commandKey(input) match {
      case ClearCommand => clear()
      case ExitCommand => exit()
      case _ => ()
    }
  }

  def readLine(): String = {
    print("$: ")
    commandKey(StdIn.readLine())
  }

  val ClearCommand = "clear"
  def clear(): Unit = "clear".!

  val ExitCommand = "exit"
  def exit(): Unit = sys.exit()
}
