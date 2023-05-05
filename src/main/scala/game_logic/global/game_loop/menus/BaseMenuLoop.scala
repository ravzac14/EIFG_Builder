package game_logic.global.game_loop.menus

import game_logic.global.game_loop.{ BaseGameLoop, GameLoopParams }
import ui.menu.MenuTree

import scala.concurrent.{ Await, ExecutionContext, Future }

trait BaseMenuLoop[T <: GameLoopParams] extends BaseGameLoop[T] {
  val menu: MenuTree[T]
}
