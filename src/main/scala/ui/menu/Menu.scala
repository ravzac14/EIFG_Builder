package ui.menu

import base.AsciiHelpers
import commands.CommandHelpers
import game_logic.global.game_loop.{
  BaseGameLoop,
  GameLoopParams,
  MainGameLoopParams
}
import system.logger.Logger
import ui.console.Console
import ui.menu.MenuHelpers.MenuBackCommands

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
      selection: String): Either[MenuTree[T], BaseGameLoop[T]] = {
    val formattedInput = CommandHelpers.formatLine(selection)
    currentMenuScreen.children.find { child =>
      CommandHelpers.formatLine(child.name) == formattedInput
    } match {
      case Some(r: MenuScreenResult[T]) =>
        Left(
          this.copy(currentMenuScreen =
            r.menuScreen.copy(parent = Some(this.currentMenuScreen))))
      case Some(r: SideEffectResult[T]) =>
        Right(r.gameLoop)
      case None if MenuBackCommands.contains(formattedInput) =>
        val parentScreen: MenuScreen[T] = this.currentMenuScreen.parent.get
        Left(this.copy(currentMenuScreen = parentScreen))
      case _ =>
        // TODO: Improve this experience
        Logger.logStdOut(MenuHelpers.unknownSelectionMessage(selection))
        Left(this)
    }
  }
}

case class MenuScreen[T <: GameLoopParams](
    name: String,
    children: Seq[MenuResult[T]],
    parent: Option[MenuScreen[T]] = None)

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
  val MenuBackCommands: Seq[Seq[String]] =
    Seq("back", "return", "up").map(Seq(_))

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
      initialGameLoop: BaseGameLoop[MainGameLoopParams],
      exitGameLoop: BaseGameLoop[MainGameLoopParams]
  ): MenuTree[MainGameLoopParams] = {
    // Screen 3: Settings
    val baseSettingsMenuScreen: MenuScreen[MainGameLoopParams] =
      MenuScreen("Settings", Seq())

    // Screen 2: Load Game
//    val saves: Seq[SideEffectResult] = getSavesAsMenuResults()
    val baseLoadGameMenuScreen: MenuScreen[MainGameLoopParams] =
      MenuScreen("Load Game", Seq.empty)

    // Screen 1: Main Menu
    val playNewGame: SideEffectResult[MainGameLoopParams] =
      SideEffectResult("New Game", initialGameLoop)
    val loadGame: MenuScreenResult[MainGameLoopParams] =
      MenuScreenResult("Load Game", baseLoadGameMenuScreen)
    val settings: MenuScreenResult[MainGameLoopParams] =
      MenuScreenResult("Settings", baseSettingsMenuScreen)
    val exit: SideEffectResult[MainGameLoopParams] =
      SideEffectResult("Exit", exitGameLoop)
    val mainMenuScreen: MenuScreen[MainGameLoopParams] =
      MenuScreen("Main Menu", Seq(playNewGame, loadGame, settings, exit))

    MenuTree(
      AsciiHelpers.buildAsciiString(gameTitle),
      Seq(mainMenuScreen, baseLoadGameMenuScreen, baseSettingsMenuScreen),
      mainMenuScreen
    )
  }
}
