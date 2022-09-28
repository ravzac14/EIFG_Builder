package game_logic.global.game_loop.prompts

import game_logic.global.game_loop.{ BaseGameLoop, GameLoopParams }
import ui.console.Console
import ui.prompt.PromptTree

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, ExecutionContext, Future }

case class BasePromptLoop[T <: GameLoopParams](
    promptTree: PromptTree[T],
    previousGameLoop: BaseGameLoop[T],
    console: Console,
    timeout: Duration)(implicit ec: ExecutionContext)
    extends BaseGameLoop[T] {

  override def setState(newState: T): BaseGameLoop[T] =
    this.copy(previousGameLoop = previousGameLoop.setState(newState))

  override def getState: T = previousGameLoop.getState

  override def run: BaseGameLoop[T] = {
    val resultNewLoop = {
      for {
        writeResult <- console.writeUntyped(promptTree.currentPrompt.message)
        rawReadResult <- console.readUntyped()
        eitherNextPromptOrNewState = promptTree.processSelection(rawReadResult)
      } yield {
        eitherNextPromptOrNewState match {
          case Left(newPromptTree) =>
            this.copy(newPromptTree, previousGameLoop, console)
          case Right(newLoop) =>
            newLoop
        }
      }
    }
    Await.result(Future.fromTry(resultNewLoop), timeout)
  }
}
