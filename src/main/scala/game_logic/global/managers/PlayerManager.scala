package game_logic.global.managers

import game_logic.character.{ Actor, CharacterState }
import game_logic.global.managers.PlayerManager.PlayerUpdate

import scala.util.{ Success, Try }

case class PlayerManager(player: Actor, characterState: CharacterState)
    extends BaseManager[PlayerUpdate] {
  import PlayerManager._

  protected def handleUpdate(update: PlayerUpdate): Try[PlayerManager] =
    update match {
      case AddNote(newNote) =>
        Success {
          this.copy(characterState = characterState.copy(
            notebook = characterState.notebook.addNotes(newNote)))
        }

      case RemoveNote(entryNums) =>
        Success {
          this.copy(
            characterState = characterState.copy(
              notebook = characterState.notebook.removeNotes(entryNums)))
        }
    }
}

object PlayerManager {

  sealed trait PlayerUpdate
  case class AddNote(newNote: String) extends PlayerUpdate
  case class RemoveNote(entryNum: Int*) extends PlayerUpdate
}
