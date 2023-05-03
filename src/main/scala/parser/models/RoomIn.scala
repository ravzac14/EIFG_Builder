package parser.models

/** @eifgb_doc: [Description]
  *             A [[RoomIn]] can be any size and doesn't necessarily mean an indoor space. Any new
  *             space that your player may enter with a movement command can be a [[RoomIn]].
  *             IE. "You move into the clearing and see X, Y, and Z."
  *
  *             [Required Fields]
  *             A [[RoomIn]] has a [[RoomIn.name]] field that should include a unique room name.
  *
  *             A [[RoomIn]] has a [[RoomIn.simpleDescription]] field that will be the first thing a
  *             Player reads when entering the space.
  *
  *             [Optional Fields]
  *             A [[RoomIn]] has an optional [[RoomIn.detailedDescription]] field that will be displayed
  *             to the Player when they choose to look around the room. If this field isn't included
  *             the [[RoomIn.simpleDescription]] will be re-used.
  *
  *             A [[RoomIn]] may have any number of [[PortalIn]]s (in the [[RoomIn.portals]] array field)
  *             which each lead to another [[RoomIn]].
  *
  *             A [[RoomIn]] can optionally include a [[Size]] (in the [[RoomIn.size]] field), this will make
  *             the game engine's distance and time based calculations accurate, but is not needed
  *             especially if you choose not to make those statistics available to the Player.
  */
case class RoomIn(
    name: String,
    simpleDescription: String,
    detailedDescription: Option[String] = None,
    // TODO: decide if size is gonna be parsed directly from my sealed trait or some other
    // enum, perhaps can use a number value too?
//    size: Size = Room.Medium(),
    portals: Seq[PortalIn] = Seq.empty)
