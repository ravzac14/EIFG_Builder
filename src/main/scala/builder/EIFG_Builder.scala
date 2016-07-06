package builder

import game_logic.global.MenuLoop
import game_logic.menu.MenuTree

object EIFG_Builder extends App {
  override def main(args: Array[String]): Unit = {
    val mainMenuTree = new MenuTree()
    val initialLoop = new MenuLoop()
  }
}