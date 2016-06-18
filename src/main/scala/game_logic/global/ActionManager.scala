package game_logic.global

import base.DoQueue
import base.types.ActionTakerId
import game_logic.action.Action

class ActionManager(messenger: GlobalMessenger) {
  val actionQueueMap: Map[ActionTakerId, ActionQueue] = Map.empty[ActionTakerId, ActionQueue]

  def failedActionMessage(action: Action) = new Message(
    subject = MessageSubject.ActionFailed,
    message = s"Action: ${action.name} is not allowed at this time.",
    isActive = true)

  def sendFailedAction(action: Action, actionTakerId: ActionTakerId): Unit =
    messenger.addMessage(failedActionMessage(action))

  def enqueueAction(action: Action, actionTakerId: ActionTakerId): Unit =
    actionQueueMap.updated(
      actionTakerId,
      actionQueueMap.getOrElse(actionTakerId, new ActionQueue(messenger)).enqueue(action)
    )

  def undo(actionTakerId: ActionTakerId): Unit =
    if (actionQueueMap.get(actionTakerId).isDefined)
      actionQueueMap.get(actionTakerId).get.undo

  def redo(actionTakerId: ActionTakerId): Unit =
    if (actionQueueMap.get(actionTakerId).isDefined)
      actionQueueMap.get(actionTakerId).get.redo
}

class ActionQueue(messenger: GlobalMessenger,
                  override val queue: List[Action] = List.empty[Action],
                  override val undoQueue: List[Action] = List.empty[Action])
  extends DoQueue[Action](queue, undoQueue) {

  val failedUndoMessage = new Message(
    subject = MessageSubject.ActionUndoFailed,
    message = "No possible action to undo...",
    isActive = true)

  val failedRedoMessage = new Message(
    subject = MessageSubject.ActionRedoFailed,
    message = "No possible action to redo...",
    isActive = true)

  override def undo: ActionQueue =
    if (queue.nonEmpty && queue.head.canUndo)
      new ActionQueue(messenger, queue.tail, queue.head :: undoQueue)
    else {
      messenger.addMessage(failedUndoMessage)
      this
    }

  override def redo: ActionQueue =
    if (undoQueue.nonEmpty && undoQueue.head.canRedo)
      new ActionQueue(messenger, undoQueue.head :: queue, undoQueue.tail)
    else {
      messenger.addMessage(failedRedoMessage)
      this
    }
}
