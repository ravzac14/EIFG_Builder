package game_logic.global.game_loop.menus

import game_logic.global.game_loop.menus.MainMenuLoop.mainMenuTree
import game_logic.global.game_loop.{
  ExitGameLoop,
  MainGameLoop,
  MainGameLoopParams
}
import ui.menu.{ MenuHelpers, MenuTree }

import scala.concurrent.ExecutionContext

class MainMenuLoop(mainGameLoopState: MainGameLoopParams)(implicit
                                                          ec: ExecutionContext)
    extends BaseMenuLoop[MainGameLoopParams](
      mainGameLoopState,
      mainMenuTree(mainGameLoopState))

object MainMenuLoop {

  def mainMenuTree(state: MainGameLoopParams)(implicit
                                              ec: ExecutionContext): MenuTree[MainGameLoopParams] =
    MenuHelpers.buildDefaultMainMenu(
      gameTitle = "Test Game",
      initialGameLoop = MainGameLoop(state),
      exitGameLoop = ExitGameLoop[MainGameLoopParams](state))
}
