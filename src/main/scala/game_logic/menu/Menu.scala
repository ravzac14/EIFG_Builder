package game_logic.menu

import game_logic.global.GameLoop

// TODO: This will be given the global console
trait MenuTree {
  val name: String
  val menuScreens: Seq[MenuScreen]

  var currentMenuScreen: MenuScreen

  def printMenu: Unit = {
    println("$name\n")
    currentMenuScreen.children.foreach(ms => println(ms.name))
  }

  // Either it takes care of it's own side effects or it describes to the Loop where to go next
  def processSelection(selection: String): Option[GameLoop] =
    currentMenuScreen.children.find(_.name.toLowerCase() == selection.trim.toLowerCase) match {
      case Some(r: MenuScreenResult) =>
        this.currentMenuScreen = r.menuScreen
        None
      case Some(r: SideEffectResult) =>
        Some(r.gameLoop)
      case _ =>
        println(MenuHelpers.unknownSelectionMessage(selection))
        None
    }
}

trait MenuScreen {
  val name: String
  val parent: MenuScreen
  val children: Seq[MenuResult]
}

trait MenuResult {
  val name: String
}

// Holds the "next" screen if selected
case class MenuScreenResult(name: String, val menuScreen: MenuScreen) extends MenuResult
// Holds the "next" game loop if selected
case class SideEffectResult(name: String, val gameLoop: GameLoop) extends MenuResult

object MenuHelpers {
  def unknownSelectionMessage(selection: String) = s"Selection \"${selection.trim}\" could not be found."
}
