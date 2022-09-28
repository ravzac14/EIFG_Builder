package ui.title_sequence

import game_logic.global.game_loop.{ BaseGameLoop, GameLoopParams }

import scala.concurrent.duration.Duration
import scala.concurrent.duration._

case class TitleSequence[T <: GameLoopParams](
    cards: Seq[TitleCard],
    ultimateLoop: BaseGameLoop[T]) {

  def current: TitleCard = cards.head

  def next(): Either[TitleSequence[T], BaseGameLoop[T]] =
    if (cards.tail.isEmpty)
      Right(ultimateLoop)
    else
      Left(this.copy(cards = cards.tail))
}

case class TitleCard(cardArt: String, duration: Duration = 5.seconds)

object TitleSequenceHelpers {

  def buildTitleSequence[T <: GameLoopParams](
      cardArts: Seq[String],
      ultimateLoop: BaseGameLoop[T],
      cardTimer: Duration): TitleSequence[T] =
    TitleSequence[T](cardArts.map(TitleCard(_, cardTimer)), ultimateLoop)
}
