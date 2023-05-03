package game_logic.global.managers

import game_logic.character.{ Character, Inventory }
import game_logic.global.managers.PlayerManager.PlayerUpdate
import game_logic.item.{ Item, Notebook }
import game_logic.location.Room

import scala.util.{ Failure, Success, Try }

case class PlayerManager(
    player: Character,
    notebook: Notebook,
    inventory: Inventory,
    position: Room,
    previousPositions: Seq[Room])
    extends BaseManager[PlayerUpdate] {
  import PlayerManager._

  protected def handleUpdate(update: PlayerUpdate): Try[PlayerManager] = {
    def updateCharacterTemplate(f: Character => Character): Try[PlayerManager] =
      Success(this.copy(player = f(player)))

    def updateNotebookTemplate(
        f: Notebook => Try[Notebook]): Try[PlayerManager] =
      f(notebook).map(result => this.copy(notebook = result))

    def updateInventoryTemplate(
        f: Inventory => Try[Inventory]): Try[PlayerManager] =
      f(inventory).map(result => this.copy(inventory = result))

    update match {
      case UpdatePlayerName(newName) =>
        updateCharacterTemplate(_.copy(name = newName))

      case UpdatePlayerDescription(newDesc) =>
        updateCharacterTemplate(_.copy(description = newDesc))

      case UpdatePlayerAge(newAge) =>
        updateCharacterTemplate(_.copy(age = newAge))

      case AddNote(newNote) =>
        updateNotebookTemplate(n => Success(n.addNotes(newNote)))

      case RemoveNote(entryNums) =>
        updateNotebookTemplate(_.removeNotes(entryNums))

      case EquipHeadFromGround(item) =>
        updateInventoryTemplate(_.equipHeadFromGround(item))

      case EquipEyesFromGround(item) =>
        updateInventoryTemplate(_.equipEyesFromGround(item))

      case EquipLeftHandFromGround(item) =>
        updateInventoryTemplate(_.equipLeftHandFromGround(item))

      case EquipRightHandFromGround(item) =>
        updateInventoryTemplate(_.equipRightHandFromGround(item))

      case EquipFeetFromGround(item) =>
        updateInventoryTemplate(_.equipFeetFromGround(item))

      case UnequipHeadToGround() =>
        updateInventoryTemplate(_.unequipHeadToGround())

      case UnequipEyesToGround() =>
        updateInventoryTemplate(_.unequipEyesToGround())

      case UnequipLeftHandToGround() =>
        updateInventoryTemplate(_.unequipLeftHandToGround())

      case UnequipRightHandToGround() =>
        updateInventoryTemplate(_.unequipRightHandToGround())

      case UnequipFeetToGround() =>
        updateInventoryTemplate(_.unequipFeetToGround())

      case EquipHeadFromBackpack(itemName) =>
        updateInventoryTemplate(_.equipHeadFromBackpack(itemName))

      case EquipEyesFromBackpack(itemName) =>
        updateInventoryTemplate(_.equipEyesFromBackpack(itemName))

      case EquipLeftHandFromBackpack(itemName) =>
        updateInventoryTemplate(_.equipLeftHandFromBackpack(itemName))

      case EquipRightHandFromBackpack(itemName) =>
        updateInventoryTemplate(_.equipRightHandFromBackpack(itemName))

      case EquipFeetFromBackpack(itemName) =>
        updateInventoryTemplate(_.equipFeetFromBackpack(itemName))

      case UnequipHeadToBackpack() =>
        updateInventoryTemplate(_.unequipHeadToBackpack())

      case UnequipEyesToBackpack() =>
        updateInventoryTemplate(_.unequipEyesToBackpack())

      case UnequipLeftHandToBackpack() =>
        updateInventoryTemplate(_.unequipLeftHandToBackpack())

      case UnequipRightHandToBackpack() =>
        updateInventoryTemplate(_.unequipRightHandToBackpack())

      case UnequipFeetToBackpack() =>
        updateInventoryTemplate(_.unequipFeetToBackpack())

      case AddToBackpack(item) =>
        updateInventoryTemplate(_.addToBackpack(item))

      case RemoveFromBackpack(itemName, numberToRemove) =>
        updateInventoryTemplate(_.removeFromBackpack(itemName, numberToRemove))

      case ChangeRoom(newRoom) =>
        Success(this.copy(position = newRoom))

      case Backtrack() =>
        val previousPosResult: Try[Room] =
          previousPositions.lastOption
            .map(Success(_))
            .getOrElse(Failure(new NothingToBacktrackToException()))

        previousPosResult.map { prev =>
          this.copy(position = prev, previousPositions = previousPositions.init)
        }
    }
  }
}

object PlayerManager {

  class NothingToBacktrackToException()
      extends Exception("No previous position to return to.")

  sealed trait PlayerUpdate
  /* Character Updates */
  case class UpdatePlayerName(newName: String) extends PlayerUpdate
  case class UpdatePlayerDescription(newDesc: String) extends PlayerUpdate
  case class UpdatePlayerAge(newAge: Int) extends PlayerUpdate
  /* Notebook Updates */
  case class AddNote(newNote: String) extends PlayerUpdate
  case class RemoveNote(entryNum: Int*) extends PlayerUpdate
  /* Inventory Updates */
  case class EquipHeadFromGround(item: Item) extends PlayerUpdate
  case class EquipEyesFromGround(item: Item) extends PlayerUpdate
  case class EquipLeftHandFromGround(item: Item) extends PlayerUpdate
  case class EquipRightHandFromGround(item: Item) extends PlayerUpdate
  case class EquipFeetFromGround(item: Item) extends PlayerUpdate
  case class UnequipHeadToGround() extends PlayerUpdate
  case class UnequipEyesToGround() extends PlayerUpdate
  case class UnequipLeftHandToGround() extends PlayerUpdate
  case class UnequipRightHandToGround() extends PlayerUpdate
  case class UnequipFeetToGround() extends PlayerUpdate
  case class EquipHeadFromBackpack(itemName: String) extends PlayerUpdate
  case class EquipEyesFromBackpack(itemName: String) extends PlayerUpdate
  case class EquipLeftHandFromBackpack(itemName: String) extends PlayerUpdate
  case class EquipRightHandFromBackpack(itemName: String) extends PlayerUpdate
  case class EquipFeetFromBackpack(itemName: String) extends PlayerUpdate
  case class UnequipHeadToBackpack() extends PlayerUpdate
  case class UnequipEyesToBackpack() extends PlayerUpdate
  case class UnequipLeftHandToBackpack() extends PlayerUpdate
  case class UnequipRightHandToBackpack() extends PlayerUpdate
  case class UnequipFeetToBackpack() extends PlayerUpdate
  case class AddToBackpack(item: Item) extends PlayerUpdate
  case class RemoveFromBackpack(itemName: String, numberToRemove: Int)
      extends PlayerUpdate
  /* Position Updates */
  case class ChangeRoom(room: Room) extends PlayerUpdate
  case class Backtrack() extends PlayerUpdate
}
