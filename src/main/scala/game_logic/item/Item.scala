package game_logic.item

trait ItemInstance {
  val name: String
  val description: String

  // Need to consider "this happens each time X happens" vs
  // this happens the 1st time, 2nd time, etc
  val addedToInventoryEffects: Seq[Effect]
  val brokenEffects: Seq[Effect]
  val droppedEffects: Seq[Effect]
  val usedEffects: Seq[Effect]
}

trait Item {
  val instances: Seq[ItemInstance]
}

trait Effect {}
