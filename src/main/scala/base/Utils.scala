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
    def readLine(): String = {
      print("$: ")
      StdIn.readLine()
    }

    def clear(): Unit = "clear".!

    def exit(): Unit = sys.exit()
  }
}