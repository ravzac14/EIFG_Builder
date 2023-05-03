package parser.models

import base.DateTime

/** @eifgb_doc: The [[GameConfigIn]] changes how the game world behaves for your player.
  *
  *             The [[GameConfigIn.gameTurnsPerMinute]] field determines how many commands
  *             your player will enter before a minute passes in game time. Another way
  *             of thinking about this is how long do you want a player command to take
  *             in your world? IE. if you set this to `0.5` each of your player's commands
  *             take 30 seconds of in-game time to perform. The default is `1.0`.
  *
  *             The [[GameConfigIn.startingGameTime]] field is determines when the calendar
  *             and clock start in your game, at turn 0 for the player. The default is
  *             `Jan 1st 1990, 00:00:00:000000`
  */
case class GameConfigIn(
    gameTurnsPerMinute: Float,
    startingGameTime: DateTime,
    startingPosition: PlayerPositionIn)
