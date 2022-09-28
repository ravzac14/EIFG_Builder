package ui.menu

import base.AsciiHelpers
import commands.CommandHelpers
import game_logic.global.game_loop.{
  BaseGameLoop,
  GameLoopParams,
  MainGameLoopState
}
import system.logger.Logger
import ui.console.Console

case class MenuTree[T <: GameLoopParams](
    name: String,
    menuScreens: Seq[MenuScreen[T]],
    currentMenuScreen: MenuScreen[T]) {

  def printMenu(console: Console): Unit = {
    val childrenNameLines =
      currentMenuScreen.children.map(c => " * " + c.name).mkString("\n")
    val output =
      s"""============================================================
         |===== ${currentMenuScreen.name} =====
         |============================================================
         |$childrenNameLines
         |============================================================
         |""".stripMargin
    console.writeUntyped(output)
  }

  // Either it takes care of it's own side effects or it describes to the Loop where to go next
  def processSelection(
      selection: String): Either[MenuTree[T], BaseGameLoop[T]] =
    currentMenuScreen.children.find { child =>
      CommandHelpers.formatLine(child.name) == CommandHelpers.formatLine(
        selection)
    } match {
      case Some(r: MenuScreenResult[T]) =>
        Left(this.copy(currentMenuScreen = r.menuScreen))
      case Some(r: SideEffectResult[T]) =>
        Right(r.gameLoop)
      case _ =>
        // TODO: Improve this experience
        Logger.logStdOut(MenuHelpers.unknownSelectionMessage(selection))
        Left(this)
    }
}

case class MenuScreen[T <: GameLoopParams](
    name: String,
    children: Seq[MenuResult[T]])

sealed trait MenuResult[T <: GameLoopParams] {
  val name: String
}
case class MenuScreenResult[T <: GameLoopParams](
    name: String,
    menuScreen: MenuScreen[T])
    extends MenuResult[T]
case class SideEffectResult[T <: GameLoopParams](
    name: String,
    gameLoop: BaseGameLoop[T])
    extends MenuResult[T]

object MenuHelpers {
  def unknownSelectionMessage(selection: String): String =
    s"Menu selection '${selection.trim}' could not be found."

  // TODO: This
//  def getSavesAsMenuResults(): Seq[SideEffectResult] = Seq()

  /** Should build the standard main menu (ie. New Game, Load Game, Settings, Exit)
    *
    * @param gameTitle - comes in as a computer readable string
    */
  def buildDefaultMainMenu(
      gameTitle: String,
      initialGameLoop: BaseGameLoop[MainGameLoopState],
      exitGameLoop: BaseGameLoop[MainGameLoopState]
  ): MenuTree[MainGameLoopState] = {
    // Screen 3: Settings
    val settingsMenuScreen: MenuScreen[MainGameLoopState] =
      MenuScreen("Settings", Seq())

    // Screen 2: Load Game
//    val saves: Seq[SideEffectResult] = getSavesAsMenuResults()
    val loadGameMenuScreen: MenuScreen[MainGameLoopState] =
      MenuScreen("Load Game", Seq.empty)

    // Screen 1: Main Menu
    val playNewGame: SideEffectResult[MainGameLoopState] =
      SideEffectResult("New Game", initialGameLoop)
    val loadGame: MenuScreenResult[MainGameLoopState] =
      MenuScreenResult("Load Game", loadGameMenuScreen)
    val settings: MenuScreenResult[MainGameLoopState] =
      MenuScreenResult("Settings", settingsMenuScreen)
    val exit: SideEffectResult[MainGameLoopState] =
      SideEffectResult("Exit", exitGameLoop)
    val mainMenuScreen: MenuScreen[MainGameLoopState] =
      MenuScreen("Main Menu", Seq(playNewGame, loadGame, settings, exit))

    MenuTree(
      AsciiHelpers.buildAsciiString(gameTitle),
      Seq(mainMenuScreen, loadGameMenuScreen, settingsMenuScreen),
      mainMenuScreen
    )
  }
}
