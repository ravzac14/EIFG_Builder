package game_logic.global.managers

import game_logic.global.managers.GameWorldManager.GameWorldUpdate
import game_logic.location.Portal.PortalId
import game_logic.location.PortalEntrance.PortalEntranceId
import game_logic.location.{ GameWorld, Portal, PortalEntrance, Room, Zone }
import game_logic.location.Room.{ RoomId, Size }
import game_logic.location.Zone.ZoneId

import scala.util.Try

case class GameWorldManager(
    world: GameWorld,
    changeLog: Seq[GameWorldUpdate] = Seq.empty)
    extends BaseManager[GameWorldUpdate] {
  import GameWorldManager._

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

    def zoneUpdateTemplate(
        id: ZoneId,
        roomId: RoomId,
        currentWorld: GameWorld = world,
        currentChangeLog: Seq[GameWorldUpdate] = changeLog)(
        f: Zone => Zone): Try[GameWorldManager] =
      for {
        existing <- currentWorld.getZone(id, roomId)
        updated = f(existing)
        newWorld <- currentWorld.setZone(updated, roomId)
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

    def roomAndPortalUpdateTemplate(roomId: RoomId, portalId: PortalId)(
        fRoom: Room => Room)(fPortal: Portal => Portal): Try[GameWorldManager] =
      for {
        managerAfterRoomUpdate <- roomUpdateTemplate(roomId)(fRoom)
        managerAfterPortalUpdate <- portalUpdateTemplate(
          portalId,
          managerAfterRoomUpdate.world,
          managerAfterRoomUpdate.changeLog)(fPortal)
      } yield managerAfterPortalUpdate

    def updateEntranceTemplate(
        roomId: RoomId,
        portalId: PortalId,
        entranceId: PortalEntranceId)(f: PortalEntrance => PortalEntrance) =
      roomAndPortalUpdateTemplate(roomId, portalId) { r =>
        val (found, notFound) = r.portals.partition(_.meta.id == entranceId)
        val updated = f(found.head)
        r.copy(portals = notFound :+ updated)
      } { p =>
        val (found, notFound) = p.entrances.partition(_.meta.id == entranceId)
        val updated = f(found.head)
        p.copy(entrances = notFound :+ updated)
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

      case UpdateZoneSimpleDescription(id, roomId, newDescription) =>
        zoneUpdateTemplate(id, roomId)(
          _.copy(simpleDescription = newDescription))

      case UpdateZoneDetailedDescription(id, roomId, newDescription) =>
        zoneUpdateTemplate(id, roomId) {
          _.copy(detailedDescription = Some(newDescription))
        }

      case UpdateZoneName(id, roomId, newName) =>
        zoneUpdateTemplate(id, roomId)(_.copy(name = newName))

      case MarkZoneAsVisited(id, roomId) =>
        zoneUpdateTemplate(id, roomId)(_.copy(hasBeenVisited = true))

      case MarkPortalAsUsed(id) =>
        portalUpdateTemplate(id)(_.copy(hasBeenUsed = true))

      case UpdatePortalEntranceSimpleDescription(
            roomId,
            portalId,
            entranceId,
            newDescription) =>
        updateEntranceTemplate(roomId, portalId, entranceId)(
          _.copy(simpleDescription = newDescription))

      case UpdatePortalEntranceDetailedDescription(
            roomId,
            portalId,
            entranceId,
            newDescription) =>
        updateEntranceTemplate(roomId, portalId, entranceId)(
          _.copy(detailedDescription = Some(newDescription)))

      case UpdatePortalEntranceName(roomId, portalId, entranceId, newName) =>
        updateEntranceTemplate(roomId, portalId, entranceId)(
          _.copy(name = newName))

      case MarkPortalEntranceNotHidden(roomId, portalId, entranceId) =>
        updateEntranceTemplate(roomId, portalId, entranceId)(
          _.copy(isHidden = false))

      case MarkPortalEntranceUnlocked(roomId, portalId, entranceId) =>
        updateEntranceTemplate(roomId, portalId, entranceId)(
          _.copy(isLocked = false))

      case MarkPortalEntranceLocked(roomId, portalId, entranceId) =>
        updateEntranceTemplate(roomId, portalId, entranceId)(
          _.copy(isLocked = true))
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
  case class UpdateZoneSimpleDescription(
      id: ZoneId,
      roomId: RoomId,
      newDescription: String)
      extends GameWorldUpdate
  case class UpdateZoneDetailedDescription(
      id: ZoneId,
      roomId: RoomId,
      newDescription: String)
      extends GameWorldUpdate
  case class UpdateZoneName(id: ZoneId, roomId: RoomId, newName: String)
      extends GameWorldUpdate
  case class MarkZoneAsVisited(id: ZoneId, roomId: RoomId)
      extends GameWorldUpdate
  case class MarkPortalAsUsed(id: PortalId) extends GameWorldUpdate
  case class UpdatePortalEntranceSimpleDescription(
      id: RoomId,
      portalId: PortalId,
      entranceId: PortalEntranceId,
      newDescription: String)
      extends GameWorldUpdate
  case class UpdatePortalEntranceDetailedDescription(
      id: RoomId,
      portalId: PortalId,
      entranceId: PortalEntranceId,
      newDescription: String)
      extends GameWorldUpdate
  case class UpdatePortalEntranceName(
      id: RoomId,
      portalId: PortalId,
      entranceId: PortalEntranceId,
      name: String)
      extends GameWorldUpdate
  case class MarkPortalEntranceNotHidden(
      id: RoomId,
      portalId: PortalId,
      entranceId: PortalEntranceId)
      extends GameWorldUpdate
  case class MarkPortalEntranceUnlocked(
      id: RoomId,
      portalId: PortalId,
      entranceId: PortalEntranceId)
      extends GameWorldUpdate
  case class MarkPortalEntranceLocked(
      id: RoomId,
      portalId: PortalId,
      entranceId: PortalEntranceId)
      extends GameWorldUpdate
}
