package game_logic.event

import game_logic.character.NonPlayableCharacter
import game_logic.location.{Obstacle, BoonZone, Zone, HazardZone}

// TODO: This should be something that "happens" and can be
// triggered in any number of way (ie. EnterLocation, AcquireItem, FailedObstacle, BypassedObstacle, etc)
// ***Should be highly scriptable
trait Event {
  val description: String
}

trait FailableEvent extends Event {
  val failSideEffects: Unit
  val successSideEffects: Unit
}

trait CombatEvent extends FailableEvent {
  val enemies: Seq[NonPlayableCharacter]
  val zones: Seq[Zone]

  def hazardZones = zones.filter { case _: HazardZone => true }
  def boonZones = zones.filter { case _: BoonZone => true }
}

trait TimedEvent extends FailableEvent {
  val obstacles: Seq[Obstacle]
  val duration: Int // Probably should be something better than an int
}