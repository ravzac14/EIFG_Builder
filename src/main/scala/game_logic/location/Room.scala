package game_logic.location

import game_logic.action.Action
import game_logic.event.{ Obstacle, Event }
import game_logic.location.Size.Size

trait RoomInstance {
  val description: String
  val size: Size

  // may need to be a map or exit class with directionality info
  val exits: Seq[Portal]

  // TODO: Some smart way of doing this with,
  // hasBeenFound/hasBeenOpened changing what the
  // doors looked like
  def printRoom = size match {
    case Size.HUGE       =>
    case Size.VERY_LARGE =>
    case Size.LARGE      =>
    case Size.MEDIUM     =>
    case Size.SMALL      =>
    case Size.VERY_SMALL =>
    case Size.TINY       =>
  }
}

trait Room {
  val name: String
  val hasBeenVisited: Boolean
  val instances: Seq[RoomInstance]
}

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

trait PortalInstance {
  val obstacles: Seq[Obstacle] // ordinary doors have Seq().empty[Obstacle]
  val hasBeenFound: Boolean
  val hasBeenOpened: Boolean
  val to: Room
  val from: Room
}

trait Portal {
  val instances: Seq[PortalInstance]
}
