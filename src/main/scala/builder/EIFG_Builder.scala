package builder

import base.DateTime
import game_logic.global.game_loop.{
  BaseGameLoop,
  GameLoopParams,
  MainGameLoopState
}
import game_logic.global.GameConfig
import game_logic.global.game_loop.menus.MainMenuLoop
import game_logic.global.game_loop.title_sequences.BaseTitleSequenceLoop
import ui.console.{ ConsoleConfig, StdOutConsole }
import ui.title_sequence.TitleSequenceHelpers

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
//import scala.tools.jline_embedded.{ Terminal, TerminalFactory }

object EIFG_Builder extends App {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  @tailrec
  def looper[T <: GameLoopParams](
      gameLoop: BaseGameLoop[T]): BaseGameLoop[T] = {
    val newLoop = gameLoop.run
    looper(newLoop)
  }

  val consoleConfig =
    ConsoleConfig(writePrefix = Some("\n"), readPrefix = Some("> "))
  val console = new StdOutConsole(consoleConfig)
  val gameConfig =
    GameConfig.empty(
      gameTurnsPerMinute = 1,
      startingGameTime = DateTime(year = 2022, month = 8, day = 12))
  val state = MainGameLoopState.empty(console, timeout = 30.seconds, gameConfig)
  val secondLoop = new MainMenuLoop(state)
  val openingTitleSequence =
    TitleSequenceHelpers.buildTitleSequence(
      cardArts = Seq("First Card.", "Second Card.", "Third Card."),
      ultimateLoop = secondLoop,
      cardTimer = 5.seconds)
  val initialLoop = BaseTitleSequenceLoop(state, openingTitleSequence)

  looper(initialLoop)
}
