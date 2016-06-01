package game_logic

import game_logic.types.ActionTakerId

trait Action {
  def mainEffect(): Unit
  def sideEffects(): Unit
}

trait ActionTaker {
  val meta: MetaData[ActionTakerId]
  def sendActionRequest(id: ActionTakerId, action: Action): Unit
  def sendUndoActionRequest(id: ActionTakerId): Unit
  def sendRedoActionRequest(id: ActionTakerId): Unit
}

class ActionManager() {
  var queue: Seq[Action] = Seq.empty[Action]
  def undo(actionTakerId: ActionTakerId): Unit = ???
  def redo(actionTakerId: ActionTakerId): Unit = ???
}