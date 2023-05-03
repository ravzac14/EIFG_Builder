package game_logic.character

import commands.CommandHelpers.formatWord
import game_logic.character.Inventory.{
  ConsumableItemNotInBackpackException,
  DuplicateItemFoundException,
  EquipItemNotInBackpackException,
  GivenItemNotEquippableException,
  ItemNotInBackpackException,
  KeyItemNotInBackpackException,
  NotEnoughItemsInBackpackToRemoveException,
  NothingToUnequipException
}
import game_logic.item.Item.ItemId
import game_logic.item.{ ConsumableItem, EquipItem, Item, KeyItem }

import scala.util.{ Failure, Success, Try }

case class Inventory(
    head: Option[EquipItem],
    eyes: Option[EquipItem],
    leftHand: Option[EquipItem],
    rightHand: Option[EquipItem],
    feet: Option[EquipItem],
    backpack: Seq[Item]) {

  def isValid: Boolean =
    backpack.map(_.id).distinct.toSet.equals(backpack.map(_.id).toSet) &&
      backpack.map(_.name).distinct.toSet.equals(backpack.map(_.name).toSet)

  /* Accessor Methods */
  private def findAllItemInBackpack(p: Item => Boolean): Seq[Item] =
    backpack.collect { case i if p(i) => i }

  private def findItemInBackpack(p: Item => Boolean): Try[Item] =
    backpack
      .collectFirst { case i if p(i) => Success(i) }
      .getOrElse(Failure(new ItemNotInBackpackException()))

  private def findKeyItemInBackpack(p: KeyItem => Boolean): Try[KeyItem] =
    keyItems
      .collectFirst { case i if p(i) => Success(i) }
      .getOrElse(Failure(new KeyItemNotInBackpackException()))

  def keyItems: Seq[KeyItem] =
    backpack.collect { case keyItem: KeyItem =>
      keyItem
    }

  private def findEquipItemInBackpack(p: EquipItem => Boolean): Try[EquipItem] =
    equipItems
      .collectFirst { case i if p(i) => Success(i) }
      .getOrElse(Failure(new EquipItemNotInBackpackException()))

  def equipItems: Seq[EquipItem] =
    backpack.collect { case equipItem: EquipItem =>
      equipItem
    }

  private def findConsumableItemInBackpack(
      p: ConsumableItem => Boolean): Try[ConsumableItem] =
    consumables
      .collectFirst { case i if p(i) => Success(i) }
      .getOrElse(Failure(new ConsumableItemNotInBackpackException()))

  def consumables: Seq[ConsumableItem] =
    backpack.collect { case consumableItem: ConsumableItem =>
      consumableItem
    }

  /* Mutator Methods */
  def addToBackpack(item: Item): Try[Inventory] =
    if (findItemInBackpack(_.id == item.id).isFailure) {
      Success(copy(backpack = backpack :+ item))
    } else {
      Failure(
        new DuplicateItemFoundException(s"Item ${item.name} already in bag."))
    }

  private def removeFromBackpack(id: ItemId): Try[Inventory] =
    findItemInBackpack(_.id == id)
      .map(_ => copy(backpack = backpack.filterNot(_.id == id)))

  def removeFromBackpack(
      itemName: String,
      numberToRemove: Int): Try[Inventory] =
    if (
      findAllItemInBackpack(i =>
        formatWord(i.name) == formatWord(itemName)).length >= numberToRemove
    ) {
      findAllItemInBackpack(i => formatWord(i.name) == formatWord(itemName))
        .take(numberToRemove)
        .foldLeft(Try(this)) {
          case (Success(prevInventory), nextItemToRemove) =>
            prevInventory.removeFromBackpack(nextItemToRemove.id)
          case (failureResult, _) =>
            failureResult
        }
    } else {
      Failure(
        new NotEnoughItemsInBackpackToRemoveException(itemName, numberToRemove))
    }

  private def equipFromGround(item: Item)(
      zoneName: String,
      accessF: Inventory => Option[EquipItem],
      updateF: Inventory => EquipItem => Inventory): Try[Inventory] =
    item match {
      case i: EquipItem if i.zone.name == zoneName =>
        accessF(this)
          .map(addToBackpack(_))
          .getOrElse(Success(this))
          .map { withPreviousEquipmentInBP =>
            updateF(withPreviousEquipmentInBP)(i)
          }

      case item =>
        Failure(new GivenItemNotEquippableException(item.name, zoneName))
    }

  def equipHeadFromGround(item: Item): Try[Inventory] =
    equipFromGround(item)(
      Character.Head.name,
      _.head,
      (inv: Inventory) =>
        (equipItem: EquipItem) => inv.copy(head = Some(equipItem)))

  def equipEyesFromGround(item: Item): Try[Inventory] =
    equipFromGround(item)(
      Character.Eyes.name,
      _.eyes,
      (inv: Inventory) =>
        (equipItem: EquipItem) => inv.copy(eyes = Some(equipItem)))

  def equipLeftHandFromGround(item: Item): Try[Inventory] =
    equipFromGround(item)(
      Character.Hand.name,
      _.leftHand,
      (inv: Inventory) =>
        (equipItem: EquipItem) => inv.copy(leftHand = Some(equipItem)))

  def equipRightHandFromGround(item: Item): Try[Inventory] =
    equipFromGround(item)(
      Character.Hand.name,
      _.rightHand,
      (inv: Inventory) =>
        (equipItem: EquipItem) => inv.copy(rightHand = Some(equipItem)))

  def equipFeetFromGround(item: Item): Try[Inventory] =
    equipFromGround(item)(
      Character.Feet.name,
      _.feet,
      (inv: Inventory) =>
        (equipItem: EquipItem) => inv.copy(feet = Some(equipItem)))

  def unequipHeadToGround(): Try[Inventory] =
    head
      .map(Success(_))
      .getOrElse(Failure(new NothingToUnequipException(Character.Head.name)))
      .map(_ => copy(head = None))

  def unequipEyesToGround(): Try[Inventory] =
    eyes
      .map(Success(_))
      .getOrElse(Failure(new NothingToUnequipException(Character.Eyes.name)))
      .map(_ => copy(eyes = None))

  def unequipLeftHandToGround(): Try[Inventory] =
    leftHand
      .map(Success(_))
      .getOrElse(Failure(new NothingToUnequipException(Character.Hand.name)))
      .map(_ => copy(leftHand = None))

  def unequipRightHandToGround(): Try[Inventory] =
    rightHand
      .map(Success(_))
      .getOrElse(Failure(new NothingToUnequipException(Character.Hand.name)))
      .map(_ => copy(rightHand = None))

  def unequipFeetToGround(): Try[Inventory] =
    feet
      .map(Success(_))
      .getOrElse(Failure(new NothingToUnequipException(Character.Feet.name)))
      .map(_ => copy(feet = None))

  private def equipFromBackpack(itemName: String)(
      zoneName: String,
      accessF: Inventory => Option[EquipItem],
      updateF: Inventory => EquipItem => Inventory): Try[Inventory] =
    for {
      foundEquipItemByName <- findEquipItemInBackpack { i =>
        formatWord(i.name) == formatWord(itemName)
      }
      if foundEquipItemByName.zone.name == zoneName
      withRemovedFromBP <- removeFromBackpack(foundEquipItemByName.id)
      withPreviousEquipmentInBP <- accessF(this)
        .map(h => withRemovedFromBP.addToBackpack(h))
        .getOrElse(Success(withRemovedFromBP))
    } yield updateF(withPreviousEquipmentInBP)(foundEquipItemByName)

  def equipHeadFromBackpack(itemName: String): Try[Inventory] =
    equipFromBackpack(itemName)(
      Character.Head.name,
      _.head,
      (inv: Inventory) =>
        (equipItem: EquipItem) => inv.copy(head = Some(equipItem)))

  def equipEyesFromBackpack(itemName: String): Try[Inventory] =
    equipFromBackpack(itemName)(
      Character.Eyes.name,
      _.eyes,
      (inv: Inventory) =>
        (equipItem: EquipItem) => inv.copy(eyes = Some(equipItem)))

  def equipLeftHandFromBackpack(itemName: String): Try[Inventory] =
    equipFromBackpack(itemName)(
      Character.Hand.name,
      _.leftHand,
      (inv: Inventory) =>
        (equipItem: EquipItem) => inv.copy(leftHand = Some(equipItem)))

  def equipRightHandFromBackpack(itemName: String): Try[Inventory] =
    equipFromBackpack(itemName)(
      Character.Hand.name,
      _.rightHand,
      (inv: Inventory) =>
        (equipItem: EquipItem) => inv.copy(rightHand = Some(equipItem)))

  def equipFeetFromBackpack(itemName: String): Try[Inventory] =
    equipFromBackpack(itemName)(
      Character.Feet.name,
      _.feet,
      (inv: Inventory) =>
        (equipItem: EquipItem) => inv.copy(feet = Some(equipItem)))

  private def unequipToBackpack(
      zoneName: String,
      accessF: Inventory => Option[EquipItem],
      updateF: Inventory => Inventory): Try[Inventory] =
    for {
      foundEquipItemInSlot <- accessF(this)
        .map(Success(_))
        .getOrElse(Failure(new NothingToUnequipException(zoneName)))
      withEquipmentInBP <- this.addToBackpack(foundEquipItemInSlot)
    } yield updateF(withEquipmentInBP)

  def unequipHeadToBackpack(): Try[Inventory] =
    unequipToBackpack(Character.Head.name, _.head, _.copy(head = None))

  def unequipEyesToBackpack(): Try[Inventory] =
    unequipToBackpack(Character.Eyes.name, _.eyes, _.copy(eyes = None))

  def unequipLeftHandToBackpack(): Try[Inventory] =
    unequipToBackpack(Character.Hand.name, _.leftHand, _.copy(leftHand = None))

  def unequipRightHandToBackpack(): Try[Inventory] =
    unequipToBackpack(
      Character.Hand.name,
      _.rightHand,
      _.copy(rightHand = None))

  def unequipFeetToBackpack(): Try[Inventory] =
    unequipToBackpack(Character.Feet.name, _.feet, _.copy(feet = None))
}

object Inventory {

  // TODO: It may be the case that we occasionally _want_ to secretly remove
  // things from a players backpack
  class ItemNotInBackpackException(
      id: Option[ItemId] = None,
      itemType: Option[String] = None)
      extends Exception(
        s"Requested ${itemType.getOrElse("regular")} item [${id.getOrElse("N/A")}] not found in backpack.")

  class KeyItemNotInBackpackException(id: Option[ItemId] = None)
      extends ItemNotInBackpackException(id, Some("key"))

  class EquipItemNotInBackpackException(id: Option[ItemId] = None)
      extends ItemNotInBackpackException(id, Some("equipment"))

  class ConsumableItemNotInBackpackException(id: Option[ItemId] = None)
      extends ItemNotInBackpackException(id, Some("consumable"))

  class GivenItemNotEquippableException(itemName: String, zoneName: String)
      extends Exception(s"Cannot equip item [$itemName] in [$zoneName] slot")

  class DuplicateItemFoundException(details: String) extends Exception(details)

  class NotEnoughItemsInBackpackToRemoveException(
      itemName: String,
      numberToRemove: Int)
      extends Exception(
        s"Could not remove [$numberToRemove] copies of [$itemName], there aren't enough in inventory.")

  class NothingToUnequipException(zoneName: String)
      extends Exception(
        s"Could not unequip [$zoneName], nothing equipped in slot.")

  def empty: Inventory =
    Inventory(
      leftHand = None,
      rightHand = None,
      head = None,
      eyes = None,
      feet = None,
      backpack = Seq.empty)
}
