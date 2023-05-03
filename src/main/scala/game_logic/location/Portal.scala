package game_logic.location

import base.data_structures.Meta
import game_logic.location.Portal.{ Oblivion, PortalId }
import game_logic.location.Room.RoomId

case class Portal(
    meta: Meta[PortalId],
    a: RoomId,
    b: RoomId,
    description: String,
    isOpen: Boolean = false,
    isLocked: Boolean = false,
    hasBeenUsed: Boolean = false) {

  def descriptionForUse: String = {
    val openDesc = if (isOpen) "open" else "closed"
    val openVerb = if (isOpen) "push it open and " else ""
    s"The $description stands before you $openDesc, you ${openVerb}step through."
  }

  def isValid: Boolean = meta.isActive

  def canBeUsed: Boolean =
    (isOpen || !isLocked) && (a != Oblivion && b != Oblivion)

  def setExits(one: RoomId, two: RoomId): Portal =
    this.copy(a = one, b = two)

  def getOther(in: RoomId): RoomId =
    if (in == a) b else a
}

object Portal {

  val Oblivion: RoomId = "-1"

  type PortalId = String

  def stub(isOpen: Boolean, isLocked: Boolean): Portal =
    Portal(
      meta = Meta[PortalId](),
      a = Oblivion,
      b = Oblivion,
      description = "cool door",
      isOpen = isOpen,
      isLocked = isLocked)
}
