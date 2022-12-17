//package commands
//
//import commands.outcomes.{
//  CommandOutcome,
//  RedoCommandOutcome,
//  UndoCommandOutcome
//}
//import commands.types.{ BaseCommand, CommandMeta }
//import game_logic.global.GameState
//
//import scala.concurrent.Future
//
//// TODO: this
//object SupportUndoCommands {
//
//  case class Undo(atSystemTime: Long) extends BaseCommand {
//    def meta: CommandMeta = Undo
//    def action: Future[CommandOutcome] =
//      Future.successful {
//        UndoCommandOutcome(this, atSystemTime)
//      }
//  }
//  object Undo extends CommandMeta {
//    override val isGameTimeExempt: Boolean = true
//    override val isTurnExempt: Boolean = true
//    override val isQueueExempt: Boolean = true
//    def name: String = "undo"
//    def values: Seq[String] = Seq("undo")
//    def tooltip: Option[String] = Some("Undo the last command you ran.")
//  }
//
//  case class Redo(atSystemTime: Long) extends BaseCommand {
//    def meta: CommandMeta = Redo
//    def action: Future[CommandOutcome] =
//      Future.successful {
//        RedoCommandOutcome(this, atSystemTime)
//      }
//  }
//  object Redo extends CommandMeta {
//    override val isGameTimeExempt: Boolean = true
//    override val isTurnExempt: Boolean = true
//    override val isQueueExempt: Boolean = true
//    def name: String = "redo"
//    def values: Seq[String] = Seq("redo")
//    def tooltip: Option[String] = Some("Redo the last undone command.")
//  }
//
////  def commands: Seq[BaseCommand] =
////    Seq(Redo, Undo)
//}
