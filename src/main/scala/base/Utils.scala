package base

import java.util.UUID

import scala.util.Random

object Utils {
  val Random = new Random()

  def makeUntypedId: String = UUID.randomUUID().toString
    .replaceAll("-", Random.nextInt(10).toString)
    .replaceAll(" ", Random.nextInt(10).toString)
}