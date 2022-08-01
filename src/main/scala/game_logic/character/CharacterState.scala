package game_logic.character

import game_logic.item.Notebook

// TODO: flesh this out
case class CharacterState(notebook: Notebook, inventory: Unit)
    extends Serializable

object CharacterState {

  def empty: CharacterState =
    CharacterState(Notebook.empty, ())
}
