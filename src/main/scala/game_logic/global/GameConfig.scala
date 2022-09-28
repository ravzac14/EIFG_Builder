package game_logic.global

import base.DateTime

case class GameConfig(gameTurnsPerMinute: Float, startingGameTime: DateTime) {
  def millisPerGameTurn: Int =
    Math.round(DateTime.Constants.MAX_MILLIS_PER_MIN / gameTurnsPerMinute)
}

object GameConfig {

  def empty(gameTurnsPerMinute: Float, startingGameTime: DateTime): GameConfig =
    GameConfig(gameTurnsPerMinute, startingGameTime)
}
