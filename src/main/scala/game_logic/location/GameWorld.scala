package game_logic.location

import game_logic.location.Portal.PortalId
import game_logic.location.Room.RoomId

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

  // TODO: Print each room if visited
  def printVisitedMap: Unit = ???
}

object GameWorld {

  class RoomNotFoundException(id: RoomId)
      extends Exception(s"Could not find room with id [$id]")

  class PortalNotFoundException(id: PortalId)
      extends Exception(s"Could not find portal with id [$id]")
}
