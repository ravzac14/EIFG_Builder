package game_logic.global.game_loop.prompts

import game_logic.global.game_loop.menus.MainMenuLoop
import game_logic.global.game_loop.prompts.ExitToMenuLoop.exitToMenuPromptTree
import game_logic.global.game_loop.{ BaseGameLoop, MainGameLoopState }
import ui.console.Console
import ui.prompt._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

class ExitToMenuLoop(
    previousGameLoop: BaseGameLoop[MainGameLoopState],
    console: Console,
    timeout: Duration)(implicit ec: ExecutionContext)
    extends BasePromptLoop[MainGameLoopState](
      exitToMenuPromptTree(previousGameLoop),
      previousGameLoop,
      console,
      timeout)

object ExitToMenuLoop {

  def exitToMenuPromptTree(previousGameLoop: BaseGameLoop[MainGameLoopState])(
      implicit ec: ExecutionContext): PromptTree[MainGameLoopState] = {
    val confirmExitPrompt =
      Prompt(
        "Are you sure you would like to exit to the main menu? Y/N",
        Seq(
          // TODO: This should actually send a new loop and carry along the state/ or update it
          SideEffectResult(
            name = "yes_exit",
            matchingValues = PromptHelpers.affirmativePromptValues,
            newLoop = new MainMenuLoop(previousGameLoop.getState)),
          SideEffectResult(
            name = "no_exit",
            matchingValues = PromptHelpers.negativePromptValues,
            newLoop = previousGameLoop)
        )
      )
    PromptTree("exitToMenu", Seq(confirmExitPrompt), confirmExitPrompt)
  }
}
