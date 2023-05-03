package game_logic.global.managers

import game_logic.global.managers.GameWorldManager.GameWorldUpdate
import game_logic.location.Portal.PortalId
import game_logic.location.PortalEntrance.PortalEntranceId
import game_logic.location.{
  Direction,
  GameWorld,
  Portal,
  PortalEntrance,
  Room,
  Size
}
import game_logic.location.Room.RoomId

import scala.util.Try

case class GameWorldManager(
    world: GameWorld,
    changeLog: Seq[GameWorldUpdate] = Seq.empty)
    extends BaseManager[GameWorldUpdate] {
  import GameWorldManager._

  def portalsFrom(
      current: RoomId,
      direction: Direction): Try[Seq[PortalEntrance]] =
    for {
      currentRoom <- world.getRoom(current)
    } yield currentRoom.portalsInDirection(direction)

  protected def handleUpdate(
      update: GameWorldUpdate): Try[BaseManager[GameWorldUpdate]] = {
    def roomUpdateTemplate(
        id: RoomId,
        currentWorld: GameWorld = world,
        currentChangeLog: Seq[GameWorldUpdate] = changeLog)(
        f: Room => Room): Try[GameWorldManager] =
      for {
        existing <- currentWorld.getRoom(id)
        updated = f(existing)
        newWorld <- currentWorld.setRoom(updated)
      } yield {
        this.copy(world = newWorld, changeLog = currentChangeLog :+ update)
      }

    def portalUpdateTemplate(
        id: PortalId,
        currentWorld: GameWorld = world,
        currentChangeLog: Seq[GameWorldUpdate] = changeLog)(
        f: Portal => Portal): Try[GameWorldManager] =
      for {
        existing <- currentWorld.getPortal(id)
        updated = f(existing)
        newWorld <- currentWorld.setPortal(updated)
      } yield {
        this.copy(world = newWorld, changeLog = currentChangeLog :+ update)
      }

    def updateEntranceTemplate(roomId: RoomId, entranceId: PortalEntranceId)(
        f: PortalEntrance => PortalEntrance) =
      roomUpdateTemplate(roomId) { r =>
        val (found, notFound) = r.portals.partition(_.meta.id == entranceId)
        val updated = f(found.head)
        r.copy(portals = notFound :+ updated)
      }

    update match {
      case UpdateRoomSimpleDescription(id, newDescription) =>
        roomUpdateTemplate(id)(_.copy(simpleDescription = newDescription))

      case UpdateRoomDetailedDescription(id, newDescription) =>
        roomUpdateTemplate(id) {
          _.copy(detailedDescription = Some(newDescription))
        }

      case UpdateRoomName(id, newName) =>
        roomUpdateTemplate(id)(_.copy(name = newName))

      case UpdateRoomSize(id, newSize) =>
        roomUpdateTemplate(id)(_.copy(size = newSize))

      case MarkRoomAsVisited(id) =>
        roomUpdateTemplate(id)(_.copy(hasBeenVisited = true))

      case MarkPortalAsUsed(id) =>
        portalUpdateTemplate(id)(_.copy(hasBeenUsed = true))

      case UpdatePortalEntranceSimpleDescription(
            roomId,
            entranceId,
            newDescription) =>
        updateEntranceTemplate(roomId, entranceId)(
          _.copy(simpleDescription = newDescription))

      case UpdatePortalEntranceDetailedDescription(
            roomId,
            entranceId,
            newDescription) =>
        updateEntranceTemplate(roomId, entranceId)(
          _.copy(detailedDescription = Some(newDescription)))

      case UpdatePortalEntranceName(roomId, entranceId, newName) =>
        updateEntranceTemplate(roomId, entranceId)(_.copy(name = newName))

      case MarkPortalEntranceNotHidden(roomId, entranceId) =>
        updateEntranceTemplate(roomId, entranceId)(_.copy(isHidden = false))

      case MarkPortalEntranceUnlocked(roomId, entranceId) =>
        updateEntranceTemplate(roomId, entranceId)(_.copy(isLocked = false))

      case MarkPortalEntranceLocked(roomId, entranceId) =>
        updateEntranceTemplate(roomId, entranceId)(_.copy(isLocked = true))
    }
  }
}

object GameWorldManager {

  // TODO new portal version, is a door that went somewhere now goes to a different place
  // also, create new room?
  sealed trait GameWorldUpdate
  case class UpdateRoomSimpleDescription(id: RoomId, newDescription: String)
      extends GameWorldUpdate
  case class UpdateRoomDetailedDescription(id: RoomId, newDescription: String)
      extends GameWorldUpdate
  case class UpdateRoomName(id: RoomId, newName: String) extends GameWorldUpdate
  case class UpdateRoomSize(id: RoomId, newSize: Size) extends GameWorldUpdate
  case class MarkRoomAsVisited(id: RoomId) extends GameWorldUpdate
  case class MarkPortalAsUsed(id: PortalId) extends GameWorldUpdate
  case class UpdatePortalEntranceSimpleDescription(
      id: RoomId,
      entranceId: PortalEntranceId,
      newDescription: String)
      extends GameWorldUpdate
  case class UpdatePortalEntranceDetailedDescription(
      id: RoomId,
      entranceId: PortalEntranceId,
      newDescription: String)
      extends GameWorldUpdate
  case class UpdatePortalEntranceName(
      id: RoomId,
      entranceId: PortalEntranceId,
      name: String)
      extends GameWorldUpdate
  case class MarkPortalEntranceNotHidden(
      id: RoomId,
      entranceId: PortalEntranceId)
      extends GameWorldUpdate
  case class MarkPortalEntranceUnlocked(
      id: RoomId,
      entranceId: PortalEntranceId)
      extends GameWorldUpdate
  case class MarkPortalEntranceLocked(id: RoomId, entranceId: PortalEntranceId)
      extends GameWorldUpdate
}
