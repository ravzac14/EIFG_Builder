package game_logic.action

import base.Utils._
import base.data_structures.Meta
import base.types.ActionTakerId
import game_logic.global.ActionManager

trait Action {
  val name: String
  val description: String
  // I'm thinking 1-10 should be sufficient
  val priority: Int = 5
  val canUndo: Boolean = true
  val canRedo: Boolean = true

  def mainEffect(): Unit

  def sideEffects(): Unit
}

trait ActionTaker {
  val meta: Meta[ActionTakerId] = new Meta[ActionTakerId](generateUntypedId)
  var canUndoRedo: Boolean = true
  var prohibitedActions: Seq[Action] = Seq.empty[Action]

  def addProhibitedActions(actions: Seq[Action]): Unit = prohibitedActions ++ actions

  def sendActionRequest(action: Action, actionManager: ActionManager): Unit =
    if (!prohibitedActions.contains(action)) actionManager.enqueueAction(action, meta.id)
    else actionManager.sendFailedAction(action, meta.id)

  def sendUndoActionRequest(actionManager: ActionManager): Unit =
    if (canUndoRedo) actionManager.undo(meta.id)

  def sendRedoActionRequest(actionManager: ActionManager): Unit =
    if (canUndoRedo) actionManager.redo(meta.id)
}
