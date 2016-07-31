package game_logic.menu

import base.AsciiHelpers
import game_logic.global.GameLoop

// TODO: This will be given the global console
class MenuTree(
  val name: String,
  val menuScreens: Seq[MenuScreen],
  var currentMenuScreen: MenuScreen) {

  def printMenu: Unit = {
    println(s"${currentMenuScreen.name}\n")
    currentMenuScreen.children.foreach(ms => println(ms.name))
  }

  // Either it takes care of it's own side effects or it describes to the Loop where to go next
  def processSelection(selection: String): Either[MenuTree, GameLoop] =
    currentMenuScreen.children.find(_.name.toLowerCase() == selection.trim.toLowerCase) match {
      case Some(r: MenuScreenResult) => this.currentMenuScreen = r.menuScreen; Left(this)
      case Some(r: SideEffectResult) => Right(r.gameLoop)
      case _ => println(MenuHelpers.unknownSelectionMessage(selection)); Left(this)
    }
}

class MenuScreen(
  val name: String,
  val children: Seq[MenuResult])

abstract class MenuResult(val name: String)
// Holds the "next" screen if selected
case class MenuScreenResult(screenName: String, val menuScreen: MenuScreen) extends MenuResult(screenName)
// Holds the "next" game loop if selected
case class SideEffectResult(screenName: String, val gameLoop: GameLoop) extends MenuResult(screenName)

object MenuHelpers {
  def unknownSelectionMessage(selection: String): String = s"Menu selection '${selection.trim}' could not be found."

  // TODO: This
  def getSavesAsMenuResults(): Seq[SideEffectResult] = Seq()

  /** Should build the standard main menu (ie. New Game, Load Game, Settings, Exit)
 *
    * @param gameTitle - comes in as a computer readable string
    */
  def buildDefaultMainMenu(gameTitle: String, initialGameLoop: GameLoop, exitGameLoop: GameLoop): MenuTree = {
    // Screen 3: Settings
    val settingsMenuScreen: MenuScreen = new MenuScreen("Settings", Seq())

    // Screen 2: Load Game
    val saves: Seq[SideEffectResult] = getSavesAsMenuResults()
    val loadGameMenuScreen: MenuScreen = new MenuScreen("Load Game", saves)

    // Screen 1: Main Menu
    val playNewGame: SideEffectResult = new SideEffectResult("New Game", initialGameLoop)
    val loadGame: MenuScreenResult = new MenuScreenResult("Load Game", loadGameMenuScreen)
    val settings: MenuScreenResult = new MenuScreenResult("Settings", settingsMenuScreen)
    val exit: SideEffectResult = new SideEffectResult("Exit", exitGameLoop)
    val mainMenuScreen: MenuScreen = new MenuScreen("Main Menu", Seq(playNewGame, loadGame, settings, exit))

    new MenuTree(
      AsciiHelpers.buildAsciiString(gameTitle),
      Seq(mainMenuScreen, loadGameMenuScreen, settingsMenuScreen),
      mainMenuScreen)
  }
}
