package game_logic.global.game_loop

import base.DateTime
import base.data_structures.DoQueue
import game_logic.global.{ GameConfig, GameState, PlayerGameAction }
import ui.console.Console

import scala.concurrent.duration.Duration

case class MainGameLoopState(
    gameState: GameState,
    gameConfig: GameConfig,
    private val commandQueue: DoQueue[PlayerGameAction],
    console: Console,
    runTimeout: Duration)
    extends GameLoopParams {

  def lastCommand(): Option[PlayerGameAction] = commandQueue.peak()

  def lastUndoneCommand(): Option[PlayerGameAction] = commandQueue.peakUndo()

  def commandHistory(maybeDepth: Option[Int]): Seq[PlayerGameAction] =
    maybeDepth
      .map(i => commandQueue.queue.take(i))
      .getOrElse(commandQueue.queue)

  def undoLastCommand(): (MainGameLoopState, Option[PlayerGameAction]) = {
    val (
      maybeUndone: Option[PlayerGameAction],
      newQueue: DoQueue[PlayerGameAction]) =
      commandQueue.undo

    if (maybeUndone.isEmpty) {
      console.writeUntyped("Nothing to undo...")
    }

    val result: Option[MainGameLoopState] =
      for {
        undoneAction <- maybeUndone
        undoneCommand = undoneAction.command
        _ <- console
          .writeUntyped(s"Undone command [${undoneCommand.untyped}]")
          .toOption
      } yield this.copy(commandQueue = newQueue)

    (result.getOrElse(this), maybeUndone)
  }

  def redoLastUndo(): (MainGameLoopState, Option[PlayerGameAction]) = {
    val (
      maybeRedone: Option[PlayerGameAction],
      newQueue: DoQueue[PlayerGameAction]) =
      this.commandQueue.redo

    if (maybeRedone.isEmpty) {
      console.writeUntyped("Nothing to redo...")
    }

    val result: Option[MainGameLoopState] =
      for {
        redoneAction <- maybeRedone
        redoneCommand = redoneAction.command
        _ <- console
          .writeUntyped(s"Redone command [${redoneCommand.untyped}]")
          .toOption
      } yield this.copy(commandQueue = newQueue)
    (result.getOrElse(this), maybeRedone)
  }

  def updateGameTime(newGameTime: DateTime): MainGameLoopState =
    this.copy(gameState = gameState.updateTime(newGameTime))

  def updateTurn(
      action: PlayerGameAction,
      updatedGameState: GameState = gameState): MainGameLoopState = {
    val afterQueue =
      if (!action.command.typed.meta.isQueueExempt)
        this.copy(commandQueue =
          commandQueue.enqueue(action.copy(turnNum = updatedGameState.turnNum)))
      else
        this
    val afterTurn =
      if (!action.command.typed.meta.isTurnExempt)
        afterQueue.copy(gameState = updatedGameState.incrementTurn())
      else
        afterQueue

    afterTurn
  }
}

object MainGameLoopState {

  def empty(
      console: Console,
      timeout: Duration,
      gameConfig: GameConfig): MainGameLoopState =
    MainGameLoopState(
      GameState.empty(gameConfig.startingGameTime),
      gameConfig,
      DoQueue.empty,
      console,
      timeout)
}
