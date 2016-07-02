package game_logic.global

import game_logic.ActorManager
import game_logic.event.{TimedEvent, CombatEvent}
import game_logic.stats.StatDescriptor

trait GameLoop {
  def run: Unit = ???
}

// These probably take a bunch of globals
class CombatLoop(actorManager: ActorManager, event: CombatEvent) extends GameLoop {
  override def run: Unit =
    if (actorManager.partyWiped || event.enemies.forall(_.stats.health == StatDescriptor.Empty)) {
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