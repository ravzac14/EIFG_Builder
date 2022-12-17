package game_logic.global

import base.DateTime
import game_logic.character.CharacterState

case class GameState(
    // Turn only go up, time can move in either direction
    turnNum: Long,
    time: DateTime)
    extends Serializable {

  def updateTime(newTime: DateTime): GameState = this.copy(time = newTime)

  def updateTurn(newTurnNum: Long): GameState = this.copy(turnNum = newTurnNum)
}

object GameState {

  def empty(startingDateTime: DateTime): GameState = {
    GameState(turnNum = 0, time = startingDateTime)
  }
}
