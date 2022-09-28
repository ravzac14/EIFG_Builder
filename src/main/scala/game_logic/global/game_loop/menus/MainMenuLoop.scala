package game_logic.global.game_loop.menus

import game_logic.global.game_loop.menus.MainMenuLoop.mainMenuTree
import game_logic.global.game_loop.{
  ExitGameLoop,
  MainGameLoop,
  MainGameLoopState
}
import ui.menu.{ MenuHelpers, MenuTree }

import scala.concurrent.ExecutionContext

class MainMenuLoop(mainGameLoopState: MainGameLoopState)(implicit
    ec: ExecutionContext)
    extends BaseMenuLoop[MainGameLoopState](
      mainGameLoopState,
      mainMenuTree(mainGameLoopState))

object MainMenuLoop {

  def mainMenuTree(state: MainGameLoopState)(implicit
      ec: ExecutionContext): MenuTree[MainGameLoopState] =
    MenuHelpers.buildDefaultMainMenu(
      gameTitle = "Test Game",
      initialGameLoop = MainGameLoop(state),
      exitGameLoop = ExitGameLoop[MainGameLoopState](state))
}
