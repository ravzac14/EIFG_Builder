package builder

import game_logic.global._
import game_logic.menu.MenuHelpers

object EIFG_Builder extends App {
  def looper(gameLoop: GameLoop): GameLoop = {
    val newLoop = gameLoop.run
    looper(newLoop)
  }
  override def main(args: Array[String]): Unit = {
    val mainMenuTree = MenuHelpers.buildDefaultMainMenu("The Butt Game", Defaults.startGameLoop, Defaults.exitGameLoop)
    val initialLoop = new MenuLoop(MenuLoopParams(menu = mainMenuTree))
    looper(initialLoop)
  }
}

object Defaults {
  // TODO: Change these later?
  val startGameLoop: GameLoop = new ExitGameLoop(new ExitGameLoopParams())
  val exitGameLoop: GameLoop = new ExitGameLoop(new ExitGameLoopParams())
}