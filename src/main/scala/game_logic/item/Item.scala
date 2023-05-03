package game_logic.item

import base.data_structures.Meta
import game_logic.character.Character.EquipmentZone
import game_logic.item.Item.ItemId

sealed trait Item {
  val meta: Meta[ItemId]
  val name: String
  val description: String

  def id: ItemId = meta.id

  // TODO
  // Need to consider "this happens each time X happens" vs
  // this happens the 1st time, 2nd time, etc
  // addedToInventoryEffects: Seq[Effect],
  // brokenEffects: Seq[Effect],
  // droppedEffects: Seq[Effect],
  // usedEffects: Seq[Effect])
}

// ex. Flashlight, RubberBoots, Weapons, NightVisionGoggles
case class EquipItem(
    meta: Meta[ItemId],
    name: String,
    description: String,
    zone: EquipmentZone)
    extends Item

// ex. BrownDoorKey, AncientRelic, SeveredOrcHead
case class KeyItem(meta: Meta[ItemId], name: String, description: String)
    extends Item

// ex. HealthPotion, Milk, GranolaBar
case class ConsumableItem(meta: Meta[ItemId], name: String, description: String)
    extends Item

object Item {

  type ItemId = String
}
