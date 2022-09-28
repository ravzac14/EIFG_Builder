package game_logic.global.game_loop.menus

import game_logic.global.game_loop.{ BaseGameLoop, GameLoopParams }
import ui.menu.MenuTree

import scala.concurrent.{ Await, ExecutionContext, Future }

case class BaseMenuLoop[T <: GameLoopParams](menuParams: T, menu: MenuTree[T])(
    implicit ec: ExecutionContext)
    extends BaseGameLoop[T] {
  import menuParams._

  override def run: BaseGameLoop[T] = {
    menu.printMenu(console)
    val futureLoopWithNewMenu: Future[BaseGameLoop[T]] =
      for {
        selection <- Future.fromTry(console.readUntyped())
        newLoopOrMenu = menu.processSelection(selection)
      } yield {
        newLoopOrMenu match {
          case Left(newMenuSelection) => this.copy(menu = newMenuSelection)
          case Right(newGameLoop)     => newGameLoop
        }
      }
    Await.result(futureLoopWithNewMenu, runTimeout)
  }

  override def setState(newState: T): BaseGameLoop[T] =
    this.copy(menuParams = newState)

  override def getState: T = menuParams
}
