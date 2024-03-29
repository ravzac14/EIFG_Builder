package game_logic.global

import base.DateTime
import commands.types.Command

case class PlayerCommand(
    command: Command,
    turnNum: Long,
    atSystemTime: Long,
    atGameTime: DateTime)
