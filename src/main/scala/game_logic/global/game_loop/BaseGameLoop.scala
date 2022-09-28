package game_logic.global.game_loop

import ui.console.Console

import scala.concurrent.duration.Duration

trait GameLoopParams {
  val console: Console
  val runTimeout: Duration
}
trait BaseGameLoop[T <: GameLoopParams] {
  def run: BaseGameLoop[T]
  def setState(newState: T): BaseGameLoop[T]
  def updateState(transform: T => T): BaseGameLoop[T] = setState(
    transform(getState))
  def getState: T
}
