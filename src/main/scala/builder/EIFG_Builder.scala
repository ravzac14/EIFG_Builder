package builder

import game_logic.global.MenuLoop
import game_logic.menu.{MenuHelpers, MenuTree}

object EIFG_Builder extends App {
  override def main(args: Array[String]): Unit = {
    val mainMenuTree = MenuHelpers.buildDefaultMainMenu()
    val initialLoop = new MenuLoop()
  }
}