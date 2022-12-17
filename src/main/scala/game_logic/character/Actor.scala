package game_logic.character

import game_logic.character.Disposition.Disposition
import game_logic.item.Item
import game_logic.stats.Buff

class Actor(
    override val name: String = "Playable Character",
    override val description: String = "",
    override val age: Int = 1,
    override val disposition: Disposition = Disposition.Neutral,
    override val buffs: Set[Buff] = Set(),
    override val inventory: Set[Item] = Set())
    extends Character(name, description, age, disposition, buffs, inventory)
