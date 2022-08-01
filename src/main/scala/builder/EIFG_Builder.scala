package builder

import game_logic.character.CharacterState
import game_logic.global._
import game_logic.item.Notebook
import ui.console.{ ConsoleConfig, StdOutConsole }

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object EIFG_Builder extends App {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  def looper(gameLoop: GameLoop): GameLoop = {
    val newLoop = gameLoop.run
    looper(newLoop)
  }

//  val mainMenuTree = MenuHelpers.buildDefaultMainMenu(
//    gameTitle = "The Butt Game",
//    initialGameLoop = Defaults.startGameLoop,
//    exitGameLoop = Defaults.exitGameLoop)
  val consoleConfig =
    ConsoleConfig(writePrefix = Some("\n"), readPrefix = Some("\n\n> "))
  val console = new StdOutConsole(consoleConfig)
  val gameState = GameState(CharacterState(Notebook.empty, ()))
  val deps = State(gameState, console, timeout = 30.seconds)
  val initialLoop = LoopDeLoop(deps)

  console.writeUntyped("Welcome.")
  looper(initialLoop)
}
