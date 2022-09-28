package game_logic.global.managers

import base.data_structures.DoQueue
import base.types.ActionTakerId
import game_logic.action.Action
import game_logic.global.{ GlobalMessenger, Message, MessageSubject }

class ActionManager(messenger: GlobalMessenger) {

  val actionQueueMap: Map[ActionTakerId, ActionQueue] =
    Map.empty[ActionTakerId, ActionQueue]

  def enqueueAction(action: Action, actionTakerId: ActionTakerId): Unit =
    actionQueueMap.updated(
      actionTakerId,
      actionQueueMap
        .getOrElse(actionTakerId, new ActionQueue(messenger))
        .enqueue(action)
    )

  def sendFailedAction(action: Action, actionTakerId: ActionTakerId): Unit =
    messenger.addMessage(ActionHelpers.failedActionMessage(action))

  def undo(actionTakerId: ActionTakerId): Unit =
    if (actionQueueMap.get(actionTakerId).isDefined)
      actionQueueMap.get(actionTakerId).get.undo
    else
      messenger.addMessage(ActionHelpers.failedUndoMessage)

  def redo(actionTakerId: ActionTakerId): Unit =
    if (actionQueueMap.get(actionTakerId).isDefined)
      actionQueueMap.get(actionTakerId).get.redo
    else
      messenger.addMessage(ActionHelpers.failedRedoMessage)
}

class ActionQueue(
    messenger: GlobalMessenger,
    override val queue: List[Action] = List.empty[Action],
    override val undoQueue: List[Action] = List.empty[Action]
) extends DoQueue[Action](queue, undoQueue) {

  override def undo: (Option[Action], DoQueue[Action]) =
    if (queue.nonEmpty && queue.head.canUndo) {
      super.undo
    } else {
      messenger.addMessage(ActionHelpers.failedUndoMessage)
      super.undo
    }

  override def redo: (Option[Action], DoQueue[Action]) =
    if (undoQueue.nonEmpty && undoQueue.head.canRedo)
      super.redo
    else {
      messenger.addMessage(ActionHelpers.failedRedoMessage)
      super.redo
    }
}

object ActionHelpers {
  def failedActionMessage(action: Action) = new Message(
    subject = MessageSubject.ActionFailed,
    message = s"Action: ${action.name} is not allowed at this time."
  )

  def failedUndoMessage = new Message(
    subject = MessageSubject.ActionUndoFailed,
    message = "Impossible undo request..."
  )

  def failedRedoMessage = new Message(
    subject = MessageSubject.ActionRedoFailed,
    message = "Impossible redo request..."
  )
}
