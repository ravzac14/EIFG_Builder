package game_logic.global

import game_logic.character.CharacterState

case class GameState(characterState: CharacterState) extends Serializable

object GameState {

  def empty: GameState =
    GameState(CharacterState.empty)
}
