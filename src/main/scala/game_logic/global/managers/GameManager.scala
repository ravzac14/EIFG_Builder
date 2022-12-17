package game_logic.global.managers

import base.DateTime
import commands.types.Command
import game_logic.global.managers.GameWorldManager.GameWorldUpdate
import game_logic.global.managers.PlayerManager.PlayerUpdate
import game_logic.global.{ GameConfig, GameState }

import scala.util.{ Failure, Success, Try }

case class GameManager(
    state: GameState,
    playerManager: PlayerManager,
    gameWorldManager: GameWorldManager,
    config: GameConfig) {
  import game_logic.global.managers.GameManager._

  def currentTime: DateTime = state.time

  def timeAfterCommand(command: Command): DateTime =
    if (!command.typed.meta.isGameTimeExempt)
      currentTime.addMillis(config.millisPerGameTurn)
    else
      currentTime

  /** TODO remove
    * WHO USES THIS: the game loop.run? ie. game loop finds a command that would update
    *   the player or interacts with the world or both, this would ask the player manager
    *   and the world manager to update their respective areas of interest and return back
    *   to this the new version of themselves, and then this returns the new version of itself
    *   to the game loop.
    *
    * WHO SHOULD KNOW WHAT:
    *   player manager:
    *     * inventory
    *     * status effects and durations
    *   game world manager:
    *     * current player location
    *     * current state of each room/portal/zone/InWorldObject
    *   game manager:
    *     * the other managers
    *     * manage global game state
    *     * loading/saving of resources?
    *     * marshalls the global game config to the other managers as needed
    *     * custom commands
    */
  def update(
      stateUpdates: Seq[StateUpdate],
      worldUpdates: Seq[GameWorldUpdate],
      playerUpdates: Seq[PlayerUpdate]): Try[GameManager] =
    for {
      newState <- updateState(stateUpdates)
      newWorldManager <- gameWorldManager.update(worldUpdates)
      newPlayerManager <- playerManager.update(playerUpdates)
    } yield {
      newState.copy(
        playerManager = newPlayerManager.asInstanceOf[PlayerManager],
        gameWorldManager = newWorldManager.asInstanceOf[GameWorldManager])
    }

  private def updateState(updates: Seq[StateUpdate]): Try[GameManager] =
    updates.foldLeft(Try(this)) {
      case (Success(previousManagerVersion), nextOp) =>
        previousManagerVersion.handleUpdate(nextOp)
      case (Failure(ex), _) =>
        Failure(ex)
    }

  private def handleUpdate(update: StateUpdate): Try[GameManager] =
    update match {
      case UpdateGameTime(newTime) =>
        Success(copy(state = state.updateTime(newTime)))
      case UpdateTurn(newTurnNum) =>
        Success(copy(state = state.updateTurn(newTurnNum)))
    }
}

object GameManager {

  sealed trait StateUpdate
  case class UpdateGameTime(newTime: DateTime) extends StateUpdate
  case class UpdateTurn(newTurnNum: Long) extends StateUpdate
}
