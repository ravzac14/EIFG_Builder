package game_logic.location

import base.data_structures.Meta
import game_logic.location.Portal.PortalId
import game_logic.location.PortalEntrance.PortalEntranceId

case class PortalEntrance(
    meta: Meta[PortalEntranceId],
    parent: PortalId,
    name: String,
    direction: Direction,
    simpleDescription: String,
    detailedDescription: Option[String] = None,
    isOpen: Boolean = false,
    isHidden: Boolean = false,
    isLocked: Boolean = false) {

  def isValid: Boolean = meta.isActive
}

object PortalEntrance {

  type PortalEntranceId = String

  def init(
      parent: PortalId,
      name: String,
      direction: Direction,
      simpleDescription: String,
      detailedDescription: Option[String] = None,
      isHidden: Boolean = false): PortalEntrance =
    PortalEntrance(
      meta = Meta[PortalEntranceId](),
      parent = parent,
      name = name,
      direction = direction,
      simpleDescription = simpleDescription,
      detailedDescription = detailedDescription,
      isHidden = isHidden
    )
}
