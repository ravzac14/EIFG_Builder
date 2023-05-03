package manual_tests

import game_logic.location.Direction.opposite
import game_logic.location.{
  GameWorld,
  Medium,
  North,
  Northwest,
  Portal,
  PortalEntrance,
  Room,
  Small,
  Southwest
}

object TestWorld {

  def build: (GameWorld, Room) = {

    /* Master Bedroom */
    val masterLivingRoomDoorStub = Portal.stub(isOpen = false, isLocked = true)
    val masterBedBathDoorStub = Portal.stub(isOpen = true, isLocked = false)
    val masterLowerYardDoorStub = Portal.stub(isOpen = false, isLocked = true)
    val masterBedroom = Room.init(
      name = "The Master Bedroom",
      simpleDescription =
        "A large bedroom with a messy, King-sized bed against the center of the northwestern wall.",
      detailedDescription = Some(
        "The walls are decorated with cute hand-made crafts. There is a large wooden door in the northern corner " +
          "and another on the northwestern wall. There is a sliding, glass door on the southwestern wall. There is " +
          "a thin, brown bookcase full to the brim near the northern wooden door. There is a comfy, tropical chair" +
          " in the eastern corner. There is a cabinet with a television atop it against the center of the " +
          "southeastern wall. In the southern corner there sits a bowl-shaped chair."),
      size = Medium(),
      portals = Seq(
        PortalEntrance.init(
          parent = masterLivingRoomDoorStub.meta.id,
          name = "Northern Wooden Door",
          direction = North,
          simpleDescription = "A sturdy wooden door, about 8 feet tall."),
        PortalEntrance.init(
          parent = masterBedBathDoorStub.meta.id,
          name = "Northwestern Wooden Door",
          direction = Northwest,
          simpleDescription = "A sturdy wooden door, about 8 feet tall."),
        PortalEntrance.init(
          parent = masterLowerYardDoorStub.meta.id,
          name = "Sliding Glass Door",
          direction = Southwest,
          simpleDescription = "A standard sliding glass door.",
          detailedDescription = Some(
            "There are dog nose imprints at about knee level. You can see a backyard through the door and it" +
              " seems to be raining.")
        )
      )
    )
    /* Master Bath */
    val masterBathroom = Room.init(
      name = "The Master Bathroom",
      simpleDescription = "Master Bath simple description stub",
      detailedDescription = Some("Master bath detailed description stub"),
      size = Small(),
      portals = Seq(
        PortalEntrance.init(
          parent = masterBedBathDoorStub.meta.id,
          name = "Wooden Door",
          direction = opposite(Northwest),
          simpleDescription = "A sturdy wooden door.")
      )
    )
    /* Lower Backyard */
    /* Upper Backyard */
    /* Great Room */
    /* The Entry Patio */
    /* Main Hallway */
    /* Hall Bathroom */
    /* Junior Suite */
    /* Junior Suite Bathroom */
    /* Patrick's Room */
    /* The Office */
    /* The Dungeon */
    /* The Billiards Room */
    /* The Garage */
    /* The Driveway */

    val world =
      GameWorld(
        "Test World",
        Seq(masterBedroom, masterBathroom),
        Seq(
          masterBedBathDoorStub.setExits(
            masterBedroom.meta.id,
            masterBathroom.meta.id),
          masterLivingRoomDoorStub,
          masterLowerYardDoorStub)
      )
    (world, masterBedroom)
  }
}
