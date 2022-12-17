package game_logic.location

import game_logic.location.Portal.PortalId
import game_logic.location.Room.RoomId
import game_logic.location.Zone.ZoneId

import scala.util.{ Failure, Success, Try }

// TODO
case class GameWorld(name: String, rooms: Seq[Room], portals: Seq[Portal]) {
  import game_logic.location.GameWorld._

  def isValid: Boolean = rooms.forall(_.isValid) && portals.forall(_.isValid)

  def roomExists(id: RoomId): Boolean =
    rooms.exists(_.meta.id == id)

  def getRoom(id: RoomId): Try[Room] =
    rooms
      .find(_.meta.id == id)
      .map(Success(_))
      .getOrElse(Failure(new RoomNotFoundException(id)))

  def setRoom(updated: Room): Try[GameWorld] =
    if (roomExists(updated.meta.id)) {
      val existingRoomsWithout = rooms.filterNot(_.meta.id == updated.meta.id)
      Success(this.copy(rooms = existingRoomsWithout :+ updated))
    } else {
      Failure(new RoomNotFoundException(updated.meta.id))
    }

  def portalExists(id: PortalId): Boolean =
    portals.exists(_.meta.id == id)

  def getPortal(id: PortalId): Try[Portal] =
    portals
      .find(_.meta.id == id)
      .map(Success(_))
      .getOrElse(Failure(new PortalNotFoundException(id)))

  def setPortal(updated: Portal): Try[GameWorld] =
    if (portalExists(updated.meta.id)) {
      val existingPortalWithout =
        portals.filterNot(_.meta.id == updated.meta.id)
      Success(this.copy(portals = existingPortalWithout :+ updated))
    } else {
      Failure(new PortalNotFoundException(updated.meta.id))
    }

  def zoneExists(id: ZoneId, parentId: RoomId): Boolean =
    rooms.exists(r => r.meta.id == parentId && r.zones.exists(_.meta.id == id))

  def getZone(id: ZoneId, parentId: RoomId): Try[Zone] =
    rooms
      .find(_.meta.id == parentId)
      .flatMap(_.zones.find(_.meta.id == id))
      .map(Success(_))
      .getOrElse(Failure(new ZoneNotFoundException(id)))

  def setZone(updated: Zone, parentId: RoomId): Try[GameWorld] =
    if (roomExists(parentId)) {
      if (zoneExists(updated.meta.id, parentId)) {
        for {
          existingRoom <- getRoom(parentId)
          zonesWithout = existingRoom.zones.filterNot {
            _.meta.id == updated.meta.id
          }
          updatedRoom = existingRoom.copy(zones = zonesWithout :+ updated)
          existingRoomsWithout = rooms.filterNot {
            _.meta.id == existingRoom.meta.id
          }
        } yield this.copy(rooms = existingRoomsWithout :+ updatedRoom)
      } else {
        Failure(new ZoneNotFoundException(updated.meta.id))
      }
    } else {
      Failure(new RoomNotFoundException(parentId))
    }

  // TODO: Print each room if visited
  def printVisitedMap: Unit = ???
}

object GameWorld {

  class RoomNotFoundException(id: RoomId)
      extends Exception(s"Could not find room with id [$id]")

  class PortalNotFoundException(id: PortalId)
      extends Exception(s"Could not find portal with id [$id]")

  class ZoneNotFoundException(id: ZoneId)
      extends Exception(s"Could not find zone with id [$id]")
}
