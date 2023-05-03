package game_logic.location

import base.data_structures.Meta
import game_logic.global.managers.GameManager
import game_logic.global.managers.GameManager.DescriptionLevel
import game_logic.location.Room.RoomId

case class Room(
    meta: Meta[RoomId],
    name: String,
    simpleDescription: String,
    detailedDescription: Option[String] = None,
    size: Size = Medium(),
    portals: Seq[PortalEntrance] = Seq.empty,
    hasBeenVisited: Boolean = false) {

  def description(level: DescriptionLevel): String = level match {
    case GameManager.Detailed =>
      detailedDescription.getOrElse(simpleDescription)
    case GameManager.Simple =>
      simpleDescription
  }

  def isValid: Boolean =
    meta.isActive &&
      portals.forall(_.isValid)

  def portalsInDirection(givenDirection: Direction): Seq[PortalEntrance] =
    portals.filter(_.direction == givenDirection)

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

  def init(
      name: String,
      simpleDescription: String,
      detailedDescription: Option[String] = None,
      size: Size = Medium(),
      portals: Seq[PortalEntrance] = Seq.empty): Room =
    Room(
      meta = Meta[RoomId](),
      name = name,
      simpleDescription = simpleDescription,
      detailedDescription = detailedDescription,
      size = size,
      portals = portals,
      hasBeenVisited = false
    )

  def roomNameCompare(one: String, two: String): Boolean = {
    def format(s: String): String = s.trim.toLowerCase
    format(one) == format(two)
  }
}
