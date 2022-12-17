package game_logic.global

import base.DateTime

/** @eifgb_doc: The [[GameConfig]] changes how the game world behaves for your player.
  *
  *             The [[GameConfig.gameTurnsPerMinute]] field determines how many commands
  *             your player will enter before a minute passes in game time. Another way
  *             of thinking about this is how long do you want a player command to take
  *             in your world? IE. if you set this to `0.5` each of your player's commands
  *             take 30 seconds of in-game time to perform. The default is `1.0`.
  *
  *             The [[GameConfig.startingGameTime]] field is determines when the calendar
  *             and clock start in your game, at turn 0 for the player. The default is
  *             `Jan 1st 1990, 00:00:00:000000`
  */
case class GameConfig(gameTurnsPerMinute: Float, startingGameTime: DateTime) {
  def millisPerGameTurn: Int =
    Math.round(DateTime.Constants.MAX_MILLIS_PER_MIN / gameTurnsPerMinute)
}

object GameConfig {

  def empty(gameTurnsPerMinute: Float, startingGameTime: DateTime): GameConfig =
    GameConfig(gameTurnsPerMinute, startingGameTime)
}
