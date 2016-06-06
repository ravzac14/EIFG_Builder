package game_logic

import game_logic.MessageSubject.MessageSubject

/** Designed to be able to through messages to the user given
  *   some sort of side-effectual/expected effect
 *
  * @param subject - It will probably get annoying to add these
  * @param message - Something to show the users when they try something
  * @param isActive - Used to describe a messages life-cycle
  */
class Message(val subject: MessageSubject,
              val message: String,
              val isActive: Boolean = true)

object MessageSubject extends Enumeration {
  type MessageSubject = Value

  // Add anything sends messages here
  val ActionUndoFailed = Value("ActionUndoFailed")
  val ActionRedoFailed = Value("ActionRedoFailed")
}

/** Note: Not sure if this is how I want to handle printable logging
  * but it might be a global queue or it might be a smarter way.
  */
class GlobalMessenger {
  var messages: List[Message] = List.empty[Message]

  def lastActiveMessage: Message = messages.filter(_.isActive).head
}