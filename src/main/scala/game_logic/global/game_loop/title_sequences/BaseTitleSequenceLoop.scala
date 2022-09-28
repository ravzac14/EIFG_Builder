package game_logic.global.game_loop.title_sequences

import game_logic.global.game_loop.{ BaseGameLoop, GameLoopParams }
import ui.title_sequence.TitleSequence

import scala.concurrent.{ Await, ExecutionContext, Future }

case class BaseTitleSequenceLoop[T <: GameLoopParams](
    state: T,
    titleSequence: TitleSequence[T])(implicit ec: ExecutionContext)
    extends BaseGameLoop[T] {
  override def setState(newState: T): BaseGameLoop[T] =
    this.copy(state = newState)

  override def getState: T = state

  override def run: BaseGameLoop[T] = {
    assert(titleSequence.cards.nonEmpty, "Given an empty title card sequence.")
    val result: Future[BaseGameLoop[T]] =
      for {
        _ <- Future.fromTry(state.console.clear())
        _ <- Future.fromTry(
          state.console.writeUntyped(titleSequence.cards.head.cardArt))
        _ <- Future.fromTry(
          state.console.waitForInterrupt(titleSequence.current.duration))
      } yield {
        titleSequence.next() match {
          case Left(remainingSeq) =>
            this.copy(titleSequence = remainingSeq)
          case Right(newLoop) =>
            state.console.clear()
            newLoop
        }
      }
    Await.result(result, state.runTimeout)
  }
}
