package base

import java.util.UUID

import scala.io._
import scala.sys.process._
import scala.util.Random

object Utils {
  val Random = new Random()

  def generateUntypedId: String = UUID.randomUUID().toString
    .replaceAll("-", Random.nextInt(10).toString)
    .replaceAll(" ", Random.nextInt(10).toString)

  // TODO: These might take/go in the console class if I make it
  object Console {
    // NOTE: These must match the options in processDefaultCommand below
    val defaultCommands = Set("exit", "clear")
    def processDefaultCommand(input: String) = {
      val s = input.trim.toLowerCase
      require(defaultCommands.contains(s))

      // NOTE: These must match default commands above
      s match {
        case "exit" => exit()
        case "clear" => clear()
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
}