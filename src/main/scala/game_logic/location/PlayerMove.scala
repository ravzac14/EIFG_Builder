package game_logic.location

case class PlayerMove(
    from: Room,
    via: PortalEntrance,
    through: Portal,
    to: Room)
