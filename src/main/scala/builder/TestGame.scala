package builder

import base.DateTime
import game_logic.character.{ Actor, CharacterState }
import game_logic.global.game_loop.{
  BaseGameLoop,
  GameLoopParams,
  MainGameLoopParams
}
import game_logic.global.{ GameConfig, GameState }
import game_logic.global.game_loop.menus.MainMenuLoop
import game_logic.global.game_loop.title_sequences.BaseTitleSequenceLoop
import game_logic.global.managers.{
  GameManager,
  GameWorldManager,
  PlayerManager
}
import game_logic.location.GameWorld
import ui.console.{ ConsoleConfig, StdOutConsole }
import ui.title_sequence.TitleSequenceHelpers

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object TestGame extends App {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  val TestWorld: GameWorld = ???

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
      startingGameTime = DateTime(year = 1990, month = 1, day = 1))
  val player = new Actor()
  val world = TestWorld
  val gameManager =
    GameManager(
      GameState.empty(startingDateTime = gameConfig.startingGameTime),
      PlayerManager(player, CharacterState.empty),
      GameWorldManager(world),
      gameConfig)
  val state =
    MainGameLoopParams.empty(console, timeout = 30.seconds, gameManager)
  val secondLoop = new MainMenuLoop(state)
  val openingTitleSequence =
    TitleSequenceHelpers.buildTitleSequence(
      cardArts = Seq("First Card.", "Second Card.", "Third Card."),
      ultimateLoop = secondLoop,
      cardTimer = 5.seconds)
  val initialLoop = BaseTitleSequenceLoop(state, openingTitleSequence)

  looper(initialLoop)
}
