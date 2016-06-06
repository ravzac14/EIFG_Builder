package game_logic

import base.MetaData
import base.types.ActionTakerId
import base.Utils._

trait Action {
  // I'm thinking 1-10 should be sufficient
  val priority: Int = 5
  val canUndo: Boolean = true
  val canRedo: Boolean = true

  def mainEffect(): Unit
  def sideEffects(): Unit
}

trait ActionTaker {
  val meta: MetaData[ActionTakerId] = new MetaData[ActionTakerId](makeUntypedId)
  var canUndoRedo:Boolean = true

  def sendActionRequest(action: Action, actionManager: ActionManager): Unit =
    actionManager.enqueueAction(action, meta.id)
  def sendUndoActionRequest(actionManager: ActionManager): Unit =
    if (canUndoRedo) actionManager.undo(meta.id)
  def sendRedoActionRequest(actionManager: ActionManager): Unit =
    if (canUndoRedo) actionManager.redo(meta.id)
}
