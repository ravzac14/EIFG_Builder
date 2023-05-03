package parser.models

/** @eifgb_doc: [Description]
  *             A simple abstraction for the room and zone that a player starts in.
  *
  *             [Required Fields]
  *             A [[roomName]] must exactly match one of the [[RoomIn.name]]s that you've
  *             provided.
  */
case class PlayerPositionIn(roomName: String)
