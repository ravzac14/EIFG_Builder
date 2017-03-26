package base

import java.util.UUID
import scala.util.Random

object Utils {
  lazy val Random = new Random()

  def generateUntypedId: String = UUID.randomUUID().toString
    .replaceAll("-", Random.nextInt(10).toString)
    .replaceAll(" ", Random.nextInt(10).toString)
}