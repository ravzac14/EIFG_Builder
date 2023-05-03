package builder

import game_logic.location.{ GameWorld, Portal, Room }
import parser.models.{ PortalIn, RoomIn }

import scala.util.{ Failure, Try }

// TODO
//class WorldBuilder() {}

// TODO
object WorldBuilder {

  class WorldBuildingException(msg: String) extends Exception(msg)
  class DuplicateRoomException(name: String)
      extends WorldBuildingException(s"Given duplicate rooms with name [$name]")

  def portalFromPortalIn(parentRoom: String, in: PortalIn): Portal = ???
//    Portal(name = in.name)

  def roomFromRoomIn(in: RoomIn)(
      worldSoFar: GameWorld): Try[(GameWorld, Room)] = ??? //{
//    val roomAlreadyExists = worldSoFar.rooms.exists { existingRoom =>
//      Room.roomNameCompare(in.name, existingRoom.name)
//    }
//
//    if (!roomAlreadyExists) {

  /** TODO: i was laying out the logic for how to merge the portalIns into the world
    * a lil tricky since we create the Portal, and one PortalEntrance the first time
    * we see a PortalIn we also create an unresolved PortalEntrance at this time for
    * the other Room that will inevitably connect back to the first Room. This unresolved
    * PortalEntrance will be turned into a fully fledged PortalEntrance when we encounter
    * the "other" Room.
    *
    * this has from => to
    * PortalIn.from exists in WORLD_PORTALS with resolved PortalEntrance match by name:
    *   fail with duplicate portal
    *
    * PortalIn.from exists in WORLD_PORTALS with unresolved PortalEntrance match by name:
    *
    * PortalIn.from does not exist in WORLD_PORTALS:
    *
    *   check unresolved entrances, if its exists there then
    *     verify that the `to` already has a resolved entrance, if it does then
    *       add `from` to existing world portals and remove from unresolved entrances
    *       if it doesn't
    *       fail with unexpected error
    *       if it doesn't exist, its a presumably new portal, then
    *     check that `to` doesn't already have a resolved or unresolved entrance, if they dont
    *
    * if it doesn't exist, make a new portal and then do the above.
    */
//      val portalsEntrances = in.portals.map(portalFromPortalIn(in.name, _))
//      val zones = in.zones.map(zoneFromZoneIn)
//      Room(
//        in.name,
//        in.simpleDescription,
//        in.detailedDescription,
//        portalsEntrances,
//        zones)
//    } else {
//      Failure(new DuplicateRoomException(in.name))
//    }
//  }
}
