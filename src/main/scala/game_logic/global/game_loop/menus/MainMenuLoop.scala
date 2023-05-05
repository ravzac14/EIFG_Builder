package game_logic.global.game_loop.menus

import game_logic.global.game_loop.{
  BaseGameLoop,
  ExitGameLoop,
  GameplayLoop,
  MainGameLoopParams
}
import ui.menu.{ MenuHelpers, MenuTree }

import scala.concurrent.{ Await, ExecutionContext, Future }

case class MainMenuLoop(
    mainGameLoopState: MainGameLoopParams,
    menu: MenuTree[MainGameLoopParams])(implicit ec: ExecutionContext)
    extends BaseMenuLoop[MainGameLoopParams] {
  import mainGameLoopState._

  override def run: BaseGameLoop[MainGameLoopParams] = {
    menu.printMenu(console)
    val futureLoopWithNewMenu =
      for {
        selection <- Future.fromTry(console.readUntyped())
        newLoopOrMenu = menu.processSelection(selection)
        result <- newLoopOrMenu match {
          case Left(newMenuSelection) =>
            Future.successful(this.copy(menu = newMenuSelection))
          case Right(newGameLoop: ExitGameLoop[MainGameLoopParams]) =>
            Future.successful(newGameLoop)
          case Right(newGameLoop) =>
            val currentRoomDesc =
              newGameLoop.getParams.gameManager.playerManager.position
                .description(
                  newGameLoop.getParams.gameManager.getDescriptionLevel())
            Future
              .fromTry(console.writeUntyped(currentRoomDesc))
              .map(_ => newGameLoop)
        }
      } yield result
    Await.result(futureLoopWithNewMenu, runTimeout)
  }

  override def getParams: MainGameLoopParams = mainGameLoopState

  override def setParams(
      newState: MainGameLoopParams): BaseGameLoop[MainGameLoopParams] =
    this.copy(mainGameLoopState = newState)
}

object MainMenuLoop {

  def mainMenuTree(state: MainGameLoopParams)(implicit
      ec: ExecutionContext): MenuTree[MainGameLoopParams] =
    MenuHelpers.buildDefaultMainMenu(
      gameTitle = "Test Game",
      initialGameLoop = GameplayLoop(state),
      exitGameLoop = ExitGameLoop[MainGameLoopParams](state))
}
