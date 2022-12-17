package parser.models

/** @eifgb_doc: [Description]
  *             As is with the [[RoomIn]] the [[PortalIn]] doesn't necessarily mean a door. Any
  *             way a player can move between rooms can be a [[PortalIn]].
  *
  *             [Required Fields]
  *             A [[PortalIn]] has a [[PortalIn.name]] field that should include a unique door name for
  *             any given [[RoomIn]]. IE. it may be a "brown door" and another from the same room may be a
  *             "red door", or if all the doors in a room are brown, perhaps one door is named "northern
  *             brown door", and another "southern brown door".
  *
  *             A [[PortalIn]] has a [[PortalIn.simpleDescription]] field that will be the first thing a
  *             Player reads when entering the space, if the [[PortalIn]] is not hidden of course.
  *
  *             A [[PortalIn]] has a [[PortalIn.leadsTo]] field which holds the name of a [[RoomIn]]. This
  *             must exactly match the name of another [[RoomIn]] that you have defined.
  *
  *             [Optional Fields]
  *             A [[PortalIn]] has an optional [[PortalIn.detailedDescription]] field that will be displayed
  *             to the Player when they choose to further investigate the [[PortalIn]]. If this field isn't
  *             included the [[PortalIn.simpleDescription]] will be re-used.
  *
  *             A [[PortalIn]] can be hidden if you set the [[PortalIn.isHidden]] field to `true`, and if so,
  *             you should leave it out of your [[RoomIn]] description. If you do set a [[PortalIn]] as
  *             hidden, be sure to include a [[PlayerActionIn]] or a [[PlayerActionChainIn]] that will
  *             mark [[PortalIn.isHidden]] as `false`.
  *
  *             A [[PortalIn]] can be locked if you set the [[PortalIn.isLocked]] field to `true`, and if so,
  *             be sure to include a [[PlayerActionIn]] or a [[PlayerActionChainIn]] that will mark
  *             [[PortalIn.isLocked]] as `false`. Once a [[PortalIn]] is unlocked (or if it started that way),
  *             a Player will be able to move through it.
  */

case class PortalIn(
    name: String,
    simpleDescription: String,
    leadsTo: String,
    detailedDescription: Option[String] = None,
    isHidden: Boolean = false,
    isLocked: Boolean = false)
