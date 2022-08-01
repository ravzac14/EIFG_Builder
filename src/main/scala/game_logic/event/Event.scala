package game_logic.event

import game_logic.character.NonPlayableCharacter
import game_logic.location.{ BoonZone, Zone, HazardZone }

// TODO: This should be something that "happens" and can be
// triggered in any number of way (ie. EnterLocation, AcquireItem, FailedObstacle, BypassedObstacle, etc)
// ***Should be highly scriptable
trait Event {
  val description: String
  var hasBegun: Boolean
  var hasConcluded: Boolean

  def beginEvent: Unit = this.hasBegun = true
  def concludeEvent: Unit = {
    this.hasConcluded = true
  }
}

trait FailableEvent extends Event {
  var isFailed: Boolean = false
  var isSucceeded: Boolean = false
  def failSideEffects: Unit
  def successSideEffects: Unit

  val requiredObjectives: Seq[Objective]
  val optionalObjectives: Seq[Objective]

  override def beginEvent: Unit = {
    this.hasBegun = true
  }
  override def concludeEvent: Unit = {
    // Conclude event
    this.hasBegun = false
    this.hasConcluded = true

    // Evaluate if event was succeeded
    if (requiredObjectives.forall(_.hasBeenMet)) {
      this.isSucceeded = true
      this.successSideEffects
      requiredObjectives.foreach(_.successEffects)
    } else {
      this.isFailed = true
      this.failSideEffects
      requiredObjectives.foreach(_.failedEffects)
    }

    // Handle optionals
    optionalObjectives.foreach(o =>
      if (o.hasBeenMet) o.successEffects else o.failedEffects)
  }

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
