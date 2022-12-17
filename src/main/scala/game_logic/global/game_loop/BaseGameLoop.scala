package game_logic.global.game_loop

import ui.console.Console

import scala.concurrent.duration.Duration

trait GameLoopParams {
  val console: Console
  val runTimeout: Duration
}

trait BaseGameLoop[T <: GameLoopParams] {
  def run: BaseGameLoop[T]
  def setParams(newParams: T): BaseGameLoop[T]
  def updateParams(transform: T => T): BaseGameLoop[T] = setParams(
    transform(getParams))
  def getParams: T
}
