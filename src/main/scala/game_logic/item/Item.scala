package game_logic.item

trait Item {
  val name: String
  val description: String

  val addedToInventoryEffects: Set[Effect]
  val brokenEffects: Set[Effect]
  val droppedEffects: Set[Effect]
  val usedEffects: Set[Effect]
}

trait Effect {}