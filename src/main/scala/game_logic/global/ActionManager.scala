package game_logic.global

import base.data_structures.DoQueue
import base.types.ActionTakerId
import game_logic.action.Action

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

  override def undo =
    if (queue.nonEmpty && queue.head.canUndo)
      this.copy(queue = queue.tail, undoQueue = queue.head :: undoQueue)
    else {
      messenger.addMessage(ActionHelpers.failedUndoMessage)
      this
    }

  override def redo =
    if (undoQueue.nonEmpty && undoQueue.head.canRedo)
      this.copy(queue = undoQueue.head :: queue, undoQueue = undoQueue.tail)
    else {
      messenger.addMessage(ActionHelpers.failedRedoMessage)
      this
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
