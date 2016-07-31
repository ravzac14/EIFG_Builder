package game_logic.global

import game_logic.event.{ TimedEvent, CombatEvent }
import game_logic.menu.MenuTree
import base.Utils._

trait GameLoopParams {}
abstract class GameLoop(val params: GameLoopParams) { def run: Unit }

case class MenuLoopParams(val menu: MenuTree) extends GameLoopParams
class MenuLoop(params: MenuLoopParams) extends GameLoop(params) {
  override def run: Unit = {
    params.menu.printMenu
    val selection = Console.readLine()
    val maybeNewGameLoop: Option[GameLoop] = params.menu.processSelection(selection)
    ??? // TODO: How to get the new game loop to the game
  }
}

// These probably take a bunch of globals
case class CombatLoopParams(val actorManager: ActorManager, val event: CombatEvent) extends GameLoopParams
class CombatLoop(params: CombatLoopParams) extends GameLoop(params) {
  override def run: Unit =
    if (params.actorManager.isPartyWiped || params.event.enemies.forall(_.isDead)) {
      // Do the sideEffects for either case
    } else {
      // Run one iteration of combat loop and hit run again
    }
}

// TODO: timeElapsed should be some Time Unit if I do that to TimedEvent
case class TimedLoopParams(val actorManager: ActorManager, event: TimedEvent, timeElapsed: Int) extends GameLoopParams
class TimedLoop(params: TimedLoopParams) extends GameLoop(params) {
  override def run: Unit =
    if (params.timeElapsed >= params.event.duration) {
      params.event.concludeEvent
    } else {
      // Run one iteration of the timed loop and hit run again
    }
}