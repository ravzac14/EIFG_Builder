package game_logic.global

import base.DateTime
import game_logic.location.Room

case class GameConfig(
    gameTurnsPerMinute: Float,
    startingGameTime: DateTime,
    startingPosition: Room) {
  def millisPerGameTurn: Int =
    Math.round(DateTime.Constants.MAX_MILLIS_PER_MIN / gameTurnsPerMinute)
}

object GameConfig {

  def empty(
      gameTurnsPerMinute: Float,
      startingGameTime: DateTime,
      startingPosition: Room): GameConfig =
    GameConfig(gameTurnsPerMinute, startingGameTime, startingPosition)
}
