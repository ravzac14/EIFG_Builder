package game_logic.character

import game_logic.action.ActionTaker
import game_logic.character.Disposition.Disposition
import game_logic.item.Item
import game_logic.stats.Buff

class Actor(name: String = "Playable Character",
            age: Int = 1,
            disposition: Disposition = Disposition.Neutral,
            buffs: Set[Buff] = Set(),
            inventory: Set[Item] = Set(),
            canUndo: Boolean = true)
  extends Character(name, age, disposition, buffs, inventory) with ActionTaker {
  this.canUndoRedo = canUndo
}
