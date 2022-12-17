package game_logic.action

/** how do we want to log dependent actions? a list of prereqs?
  * if its prereqs, the side effects can unlock a prereq of a future action?
  *
  * Im inclined not to provide undo/redo in terms of the game world actions, at least
  * at the start. Rather let them abuse saves.
  *
  * is it worth this being a trait that has types of actions with hardcoded sideeffects,
  * such as PortalCreationSideEffect, ZoneAlterSideEffect, RoomAlterSideEffect, except those
  * are kind of all RoomAlterSideEffects...
  */
case class GameWorldAction(
    name: String,
    description: String,
    // I'm thinking 1-10 should be sufficient
    priority: Int = 5) {

  def mainEffect(): Unit = ???

  def sideEffects(): Unit = ???
}

/** This should be a sequential, related series of game world actions that will
  * have a larger side effect. IE. "You may have to go to a tree and place a totem,
  * and then you may have to travel to a basement and pray at an altar, and doing so
  * may open a doorway that didn't exist before."
  */
case class GameWorldActionChain(
    name: String,
    description: String,
    actions: Seq[GameWorldAction])
