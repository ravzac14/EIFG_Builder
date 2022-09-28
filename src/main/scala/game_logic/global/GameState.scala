package game_logic.global

import base.DateTime
import game_logic.character.CharacterState

case class GameState(
    // Turn and only go up, time can move in either direction
    turnNum: Long,
    time: DateTime,
    characterState: CharacterState)
    extends Serializable {

  def updateTime(newTime: DateTime): GameState = this.copy(time = newTime)

  def incrementTurn(): GameState = this.copy(turnNum = turnNum + 1)
}

object GameState {

  def empty(startingDateTime: DateTime): GameState = {
    GameState(
      turnNum = 0,
      time = startingDateTime,
      characterState = CharacterState.empty)
  }
}
