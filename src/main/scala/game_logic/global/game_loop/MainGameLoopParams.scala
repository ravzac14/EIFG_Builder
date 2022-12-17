package game_logic.global.game_loop

import base.DateTime
import base.data_structures.DoQueue
import game_logic.global.game_loop.MainGameLoopParams.{
  AddToQueue,
  ParamUpdate
}
import game_logic.global.managers.GameManager
import game_logic.global.managers.GameManager.{
  StateUpdate,
  UpdateGameTime,
  UpdateTurn
}
import game_logic.global.managers.GameWorldManager.GameWorldUpdate
import game_logic.global.managers.PlayerManager.PlayerUpdate
import game_logic.global.{ GameConfig, GameState, PlayerCommand }
import ui.console.Console

import scala.concurrent.duration.Duration
import scala.util.{ Failure, Success, Try }

case class MainGameLoopParams(
    gameManager: GameManager,
    private val commandQueue: DoQueue[PlayerCommand],
    console: Console,
    runTimeout: Duration)
    extends GameLoopParams {

  def lastCommand(): Option[PlayerCommand] = commandQueue.peak()

  def lastUndoneCommand(): Option[PlayerCommand] = commandQueue.peakUndo()

  def commandHistory(maybeDepth: Option[Int] = None): Seq[PlayerCommand] =
    maybeDepth
      .map(i => commandQueue.queue.take(i))
      .getOrElse(commandQueue.queue)

  // WHAT WAS I DOING
  // removing the copy pasta, and making this update the internal fields
  // and then calling it in the main game loop
  def update(
      paramUpdates: Seq[ParamUpdate],
      stateUpdates: Seq[StateUpdate],
      worldUpdates: Seq[GameWorldUpdate],
      playerUpdates: Seq[PlayerUpdate]): Try[MainGameLoopParams] =
    for {
      paramsWithNewQueue <- updateParams(paramUpdates)
      newGameManager <- gameManager.update(
        stateUpdates,
        worldUpdates,
        playerUpdates)
    } yield {
      paramsWithNewQueue.copy(gameManager = newGameManager)
    }

  private def updateParams(updates: Seq[ParamUpdate]): Try[MainGameLoopParams] =
    updates.foldLeft(Try(this)) {
      case (Success(previousVersion), nextOp) =>
        previousVersion.handleUpdate(nextOp)
      case (Failure(ex), _) =>
        Failure(ex)
    }

  private def handleUpdate(update: ParamUpdate): Try[MainGameLoopParams] =
    update match {
      case AddToQueue(playerCommand) =>
        Success(copy(commandQueue = commandQueue.enqueue(playerCommand)))
    }

  def undoLastCommand(): (DoQueue[PlayerCommand], Option[PlayerCommand]) = {
    val (maybeUndone: Option[PlayerCommand], newQueue: DoQueue[PlayerCommand]) =
      commandQueue.undo

    if (maybeUndone.isEmpty) {
      console.writeUntyped("Nothing to undo...")
    }

    val result: Option[DoQueue[PlayerCommand]] =
      for {
        undoneAction <- maybeUndone
        undoneCommand = undoneAction.command
        _ <- console
          .writeUntyped(s"Undone command [${undoneCommand.untyped}]")
          .toOption
      } yield newQueue

    (result.getOrElse(this.commandQueue), maybeUndone)
  }

  def redoLastUndo(): (MainGameLoopParams, Option[PlayerCommand]) = {
    val (maybeRedone: Option[PlayerCommand], newQueue: DoQueue[PlayerCommand]) =
      this.commandQueue.redo

    if (maybeRedone.isEmpty) {
      console.writeUntyped("Nothing to redo...")
    }

    val result: Option[MainGameLoopParams] =
      for {
        redoneAction <- maybeRedone
        redoneCommand = redoneAction.command
        _ <- console
          .writeUntyped(s"Redone command [${redoneCommand.untyped}]")
          .toOption
      } yield this.copy(commandQueue = newQueue)
    (result.getOrElse(this), maybeRedone)
  }
}

object MainGameLoopParams {

  sealed trait ParamUpdate
  case class AddToQueue(playerCommand: PlayerCommand) extends ParamUpdate

  def empty(
      console: Console,
      timeout: Duration,
      gameManager: GameManager): MainGameLoopParams =
    MainGameLoopParams(
      gameManager = gameManager,
      commandQueue = DoQueue.empty,
      console = console,
      runTimeout = timeout)
}
