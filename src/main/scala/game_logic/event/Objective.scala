package game_logic.event

// Something with a description and an acceptance criteria
trait Objective {
  var hasBeenMet: Boolean = false

  def successEffects: Unit
  def failedEffects: Unit
}
