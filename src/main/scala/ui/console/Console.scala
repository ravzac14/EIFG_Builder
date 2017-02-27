package ui.console

import scala.io._
import scala.sys.process._

// TODO: These might take/go in the ui.console class if I make it
object Console {
  val defaultCommands = Map(
    "exit" -> exit(),
    "clear" -> clear()
  )

  def isDefault(s: String): Boolean =
    defaultCommands.keySet.contains(s.trim.toLowerCase)

  def processDefaultCommand(input: String) = {
    require(isDefault(input))

    defaultCommands.get(input.trim.toLowerCase) match {
      case Some(command) => command
      case _ => ()
    }
  }

  def readLine(): String = {
    print("$: ")
    StdIn.readLine().trim.toLowerCase()
  }

  def clear(): Unit = "clear".!

  def exit(): Unit = sys.exit()
}
