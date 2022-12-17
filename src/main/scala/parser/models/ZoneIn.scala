package parser.models

/** @eifgb_doc: [Description]
  *             A [[ZoneIn]] is a sub-area to a [[RoomIn]], such as a complex [[GameObject]] or maybe just
  *             a dark corner. A player may go to investigate a [[ZoneIn]] within a [[RoomIn]], and it will
  *             change the context of a player's actions. IE. "You're standing in front of the old,
  *             brown bureau.", a player may choose to "> Search." and the game will know they mean to
  *             search the current [[ZoneIn]].
  *
  *             [Required Fields]
  *             A [[ZoneIn]] has a [[ZoneIn.name]] field that should include a unique zone name.
  *
  *             A [[ZoneIn]] has a [[ZoneIn.simpleDescription]] field that will be the first thing a
  *             Player reads when entering the space.
  *
  *             [Optional Fields]
  *             A [[ZoneIn]] has an optional [[ZoneIn.detailedDescription]] field that will be displayed
  *             to the Player when they choose to look around the [[ZoneIn]]. If this field isn't included
  *             the [[ZoneIn.simpleDescription]] will be re-used.
  */
case class ZoneIn(
    name: String,
    simpleDescription: String,
    detailedDescription: Option[String] = None)
