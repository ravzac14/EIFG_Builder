package ui.prompt

import commands.CommandHelpers
import game_logic.global.game_loop.{ BaseGameLoop, GameLoopParams }
import system.logger.Logger

case class PromptTree[T <: GameLoopParams](
    name: String,
    prompts: Seq[Prompt[T]],
    currentPrompt: Prompt[T]) {

  // Either it takes care of it's own side effects or it describes to the Loop where to go next
  def processSelection(
      selection: String): Either[PromptTree[T], BaseGameLoop[T]] =
    currentPrompt.children.find { child =>
      child.matchingValues
        .map(CommandHelpers.formatLine)
        .contains(CommandHelpers.formatLine(selection))
    } match {
      case Some(r: NextPromptResult[T]) =>
        Left(this.copy(currentPrompt = r.prompt))
      case Some(r: SideEffectResult[T]) =>
        Right(r.newLoop)
      case _ =>
        // TODO: Improve this experience
        Logger.logStdOut(PromptHelpers.unknownSelectionMessage(selection))
        Left(this)
    }
}

case class Prompt[T <: GameLoopParams](
    message: String,
    children: Seq[PromptResult[T]])

sealed trait PromptResult[T <: GameLoopParams] {
  val name: String
  val matchingValues: Seq[String]
}
case class NextPromptResult[T <: GameLoopParams](
    name: String,
    matchingValues: Seq[String],
    prompt: Prompt[T])
    extends PromptResult[T]
case class SideEffectResult[T <: GameLoopParams](
    name: String,
    matchingValues: Seq[String],
    newLoop: BaseGameLoop[T])
    extends PromptResult[T]

object PromptHelpers {
  def affirmativePromptValues: Seq[String] =
    Seq("y", "yes", "affirmative", "yea", "ya")

  def negativePromptValues: Seq[String] =
    Seq("n", "no", "negative", "nope")

  def unknownSelectionMessage(selection: String): String =
    s"Unknown selection '${selection.trim}'."
}
