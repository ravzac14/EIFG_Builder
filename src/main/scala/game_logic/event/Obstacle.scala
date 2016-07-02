package game_logic.event

import game_logic.action.Action

/** This needs some information on how to be solved/bypassed/failed
  * NOTE: Not sure if this should be more general to apply to
  * something greater.
  * *** should be highly scriptable
  */
trait Obstacle {
  val description: String
  var discovered: Boolean
  var bypassed: Boolean
  var failed: Boolean

  // A list of algorithms to bypass/fail the obstacle
  // ie. one possible could be:
  // Seq(DiscoverObstacle, DisarmObstacle)
  // or
  // Seq(DiscoverObstacle, AvoidObstacle[ProficientStat])
  val possibleActionsToBypass: Seq[Seq[Action]]
  val possibleActionsToFail: Seq[Seq[Action]]

  // The sequence of events triggered by a failed/bypassed obstacle
  val bypassedEvents: Seq[Event]
  val failedEvents: Seq[Event]
}
