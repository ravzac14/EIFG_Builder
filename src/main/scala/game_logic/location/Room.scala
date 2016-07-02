package game_logic.location

import game_logic.action.Action
import game_logic.event.{Obstacle, Event}
import game_logic.location.Size.Size

trait Room {
  val name: String
  val description: String
  val size: Size
  val hasBeenVisited: Boolean

  val northExit: Option[Portal]
  val southExit: Option[Portal]
  val westExit: Option[Portal]
  val eastExit: Option[Portal]

  // TODO: Some smart way of doing this with,
  // hasBeenFound/hasBeenOpened changing what the
  // doors looked like
  def printRoom = size match {
    case Size.HUGE =>
    case Size.VERY_LARGE =>
    case Size.LARGE =>
    case Size.MEDIUM =>
    case Size.SMALL =>
    case Size.VERY_SMALL =>
    case Size.TINY =>
  }
}

// Keep in mind, regardless of the size they only have one exit each way.
// ie. Huge would be a banquet hall, and tiny would be a cubby with
// room enough for one person and nothing else.
object Size extends Enumeration {
  type Size = Value
  val HUGE = Value("huge")
  val VERY_LARGE = Value("very_large")
  val LARGE = Value("large")
  val MEDIUM = Value("medium")
  val SMALL = Value("small")
  val VERY_SMALL = Value("very_small")
  val TINY = Value("tiny")
}

trait Portal {
  val obstacles: Seq[Obstacle] // ordinary doors have Seq().empty[Obstacle]
  val hasBeenFound: Boolean
  val hasBeenOpened: Boolean
  val to: Room
  val from: Room
}