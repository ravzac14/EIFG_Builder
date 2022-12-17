package game_logic.location

import base.data_structures.Meta
import game_logic.location.Room.RoomId
import game_logic.location.Zone.ZoneId

case class Zone(
    meta: Meta[ZoneId],
    name: String,
    parent: RoomId,
    simpleDescription: String,
    detailedDescription: Option[String] = None,
    hasBeenVisited: Boolean = false) {

  def isValid: Boolean = meta.isActive
}

object Zone {

  type ZoneId = String
}
