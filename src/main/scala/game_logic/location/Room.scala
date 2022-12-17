package game_logic.location

import base.data_structures.Meta
import game_logic.location.Room.{ RoomId, Size }

case class Room(
    meta: Meta[RoomId],
    name: String,
    simpleDescription: String,
    detailedDescription: Option[String] = None,
    size: Size = Room.Medium(),
    portals: Seq[PortalEntrance] = Seq.empty,
    zones: Seq[Zone] = Seq.empty,
    hasBeenVisited: Boolean = false) {

  def isValid: Boolean =
    meta.isActive &&
      zones.forall(z => z.isValid && z.parent == meta.id) &&
      portals.forall(p => p.isValid && p.from == meta.id)

  // TODO: Some smart way of doing this with,
  // hasBeenFound/hasBeenOpened changing what the
  // doors looked like
//  def printRoom = size match {
//    case Size.HUGE       =>
//    case Size.VERY_LARGE =>
//    case Size.LARGE      =>
//    case Size.MEDIUM     =>
//    case Size.SMALL      =>
//    case Size.VERY_SMALL =>
//    case Size.TINY       =>
//  }
}

object Room {

  type RoomId = String

  case class RoomIdentifiers(id: RoomId, name: String)

  def roomNameCompare(one: String, two: String): Boolean = {
    def format(s: String): String = s.trim.toLowerCase
    format(one) == format(two)
  }

  /** @eifgb_doc: [[Size]] is a way for the game engine to calculate things like
    *             distance and time travelled. It is optional to make these statistics
    *             available to the Player, and if you decline to do so, [[Size]] doesn't
    *             really matter.
    */
  // TODO: there will need to be concrete `Game*` versions of these Sizes that have been
  // built when compiling there game. Anything that references these base sizes should
  // actually use those (or possibly just multiply by some config value)
  trait Size {
    val min: Int
    val max: Int

    def range(scale: Int): Range =
      Range.inclusive(min * scale, max * scale)

    def average(scale: Int): Int =
      Math.round((range(scale).end - range(scale).start) / 2)
  }

  case class Huge(min: Int = 51, max: Int = 1000) extends Size {
    override def toString: String = "huge"
  }

  case class Large(min: Int = 11, max: Int = 50) extends Size {
    override def toString: String = "large"
  }

  case class Medium(min: Int = 5, max: Int = 10) extends Size {
    override def toString: String = "medium"
  }

  case class Small(min: Int = 1, max: Int = 4) extends Size {
    override def toString: String = "huge"
  }
}
