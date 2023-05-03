package game_logic.global.managers

import base.{ DateTime, TryHelpers }
import commands.types.Command
import game_logic.global.managers.GameWorldManager.GameWorldUpdate
import game_logic.global.managers.PlayerManager.PlayerUpdate
import game_logic.global.{ GameConfig, GameState }
import game_logic.location.{ Direction, PlayerMove, PortalEntrance, Room }

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

  // TODO
  def moveAllowed(direction: Direction): Boolean = true

  def movesFromCurrent(direction: Direction): Try[Seq[PlayerMove]] = {
    gameWorldManager
      .portalsFrom(
        current = playerManager.position.meta.id,
        direction = direction)
      .flatMap { portalEntrancesFound =>
        TryHelpers.sequence {
          portalEntrancesFound.map { portalEntrance =>
            for {
              portal <- gameWorldManager.world.getPortal(portalEntrance.parent)
              current = playerManager.position
              destination <- gameWorldManager.world.getRoom(
                portal.getOther(current.meta.id))
            } yield PlayerMove(
              from = current,
              via = portalEntrance,
              through = portal,
              to = destination)
          }
        }
      }
  }

  //TODO
  def getDescriptionLevel(): DescriptionLevel = GameManager.Detailed

  /** TODO remove
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
    *     * marshals the global game config to the other managers as needed
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

  sealed trait DescriptionLevel
  case object Simple extends DescriptionLevel
  case object Detailed extends DescriptionLevel
}
