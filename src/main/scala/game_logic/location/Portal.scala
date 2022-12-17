package game_logic.location

import base.data_structures.Meta
import game_logic.location.Portal.PortalId
import game_logic.location.PortalEntrance.PortalEntranceId
import game_logic.location.Room.RoomId

case class Portal(
    meta: Meta[PortalId],
    entrances: Seq[PortalEntrance] = Seq.empty,
    hasBeenUsed: Boolean = false) {

  def isValid: Boolean =
    meta.isActive &&
      entrances.count(_.isValid) == 2 &&
      entrances.filter(_.isValid).forall(_.parent == meta.id)

  def entranceNamesByRoomId: Map[RoomId, String] =
    entrances
      .map(e => (e.from, e.name))
      .toMap
}

object Portal {

  type PortalId = String
}

case class PortalEntrance(
    meta: Meta[PortalEntranceId],
    parent: PortalId,
    from: RoomId,
    name: String,
    simpleDescription: String,
    detailedDescription: Option[String] = None,
    isHidden: Boolean = false,
    isLocked: Boolean = false) {

  def isValid: Boolean = meta.isActive
}

object PortalEntrance {

  type PortalEntranceId = String
}
