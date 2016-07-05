package game_logic.global

import game_logic.event.{TimedEvent, CombatEvent}
import game_logic.menu.MenuTree

import scala.io.StdIn
import scala.io.StdIn._

trait GameLoop {
  def run: Unit
}

class MenuLoop(menu: MenuTree) extends GameLoop {
  override def run: Unit = {
    menu.printMenu
    print("$: ")
    val selection = StdIn.readLine()
    val maybeNewGameLoop: Option[GameLoop] = menu.processSelection(selection)
    ??? // TODO: How to get the new game loop to the game
  }
}

// These probably take a bunch of globals
class CombatLoop(actorManager: ActorManager, event: CombatEvent) extends GameLoop {
  override def run: Unit =
    if (actorManager.isPartyWiped || event.enemies.forall(_.isDead)) {
      // Do the sideEffects for either case
      ???
    } else {
      // Run one iteration of combat loop and hit run again
    }
}

// TODO: timeElapsed should be some Time Unit if I do that to TimedEvent
class TimedLoop(actorManager: ActorManager, event: TimedEvent, timeElapsed: Int) extends GameLoop {
  override def run: Unit =
    if (timeElapsed >= event.duration) {
      event.concludeEvent
    } else {
      // Run one iteration of the timed loop and hit run again
    }
}