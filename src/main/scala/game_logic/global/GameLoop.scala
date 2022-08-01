package game_logic.global

import commands.CommandHelpers
import commands.outcomes.{
  CommandOutcome,
  ConsoleReadMoreInfoOutcome,
  ConsoleWriteOutcome,
  UnitOutcome,
  UpdateGameStateOutcome
}
import commands.types.BaseCommand
import game_logic.event.{ CombatEvent, TimedEvent }
import ui.menu.MenuTree
import ui.console._

import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration.Duration
import scala.util.{ Failure, Success, Try }

// run returns the next GameLoop, which can just be the same loop updated
trait GameLoopParams {}
abstract class GameLoop(val params: GameLoopParams) { def run: GameLoop }
case class EmptyParams() extends GameLoopParams

// TODO: turn commands into queues
case class State(
    gameState: GameState,
    console: Console,
    lastCommandRaw: Option[String] = None,
    lastCommandTyped: Option[BaseCommand] = None,
    timeout: Duration)
    extends GameLoopParams {
  def addCommandInfo(rawCommand: String, typedCommand: BaseCommand): State =
    this.copy(
      lastCommandRaw = Some(rawCommand),
      lastCommandTyped = Some(typedCommand)
    )
}
case class LoopDeLoop(dependencies: State)(implicit ec: ExecutionContext)
    extends GameLoop(dependencies) {
  import dependencies._

  // TODO: finish
  def processCommandOutcome(outcome: CommandOutcome): Future[State] =
    outcome match {
      case UnitOutcome(commander) =>
        Future.successful(dependencies)
      case ConsoleReadMoreInfoOutcome(prompt, commander) =>
        Future.successful(dependencies)
      case ConsoleWriteOutcome(message, commander) =>
        Future.fromTry(console.writeUntyped(message)).map(_ => dependencies)
      case UpdateGameStateOutcome(maybeMessage, newGameState, commander) =>
        maybeMessage.foreach(console.writeUntyped)
        Future.successful(dependencies.copy(gameState = newGameState))
    }

  def run: GameLoop = {
    // TODO: Remove this debug line
    if (lastCommandRaw.nonEmpty) {
      console.writeUntyped(
        s"You last entered [${lastCommandRaw.getOrElse("")}] which was " +
          s"parsed as [${lastCommandTyped.map(_.name).getOrElse("")}]")
    }
    val rawCommand: Try[String] = console.readUntyped()
    val parsedCommand: Try[BaseCommand] =
      rawCommand.flatMap(CommandHelpers.parseLineAsCommand(_, gameState))
    val futureStateWithOutcome: Future[State] =
      for {
        command <- Future.fromTry(parsedCommand)
        action <- command.action
        result <- processCommandOutcome(action)
      } yield result
    val stateWithOutcome = Await.result(futureStateWithOutcome, timeout)

    val buildNewState =
      for {
        newRaw <- rawCommand
        newCommand <- parsedCommand
      } yield LoopDeLoop(stateWithOutcome.addCommandInfo(newRaw, newCommand))

    buildNewState match {
      case Success(s) => s
      case Failure(ex) =>
        console.writeUntyped(
          s"Encountered fatal error: ${ex.getMessage}. Exiting."
        )
        ExitGameLoop()
    }
  }
}

//case class MenuLoopParams(val menu: MenuTree) extends GameLoopParams
//case class MenuLoop(override val params: MenuLoopParams) extends GameLoop(params) {
//  override def run: GameLoop = {
//    params.menu.printMenu
//    val selection = Console.readLine()
//
//    if (Console.defaultCommands.contains(selection)) {
//      Console.processDefaultCommand(selection)
//      this
//    } else {
//      val maybeNewGameLoop: Either[MenuTree, GameLoop] = params.menu.processSelection(selection)
//      maybeNewGameLoop match {
//        case Left(mt) => this.copy(params.copy(menu = mt))
//        case Right(gl) => gl
//      }
//    }
//  }
//}
//
//// These probably take a bunch of globals
//case class CombatLoopParams(val actorManager: ActorManager, val event: CombatEvent) extends GameLoopParams
//class CombatLoop(params: CombatLoopParams) extends GameLoop(params) {
//  override def run: GameLoop =
//    if (params.actorManager.isPartyWiped || params.event.enemies.forall(_.isDead)) {
//      // Do the sideEffects for either case
//      ???
//    } else {
//      // Run one iteration of combat loop and hit run again
//      ???
//    }
//}
//
//// TODO: timeElapsed should be some Time Unit if I do that to TimedEvent
//case class TimedLoopParams(val actorManager: ActorManager, event: TimedEvent, timeElapsed: Int) extends GameLoopParams
//class TimedLoop(params: TimedLoopParams) extends GameLoop(params) {
//  override def run: GameLoop =
//    if (params.timeElapsed >= params.event.duration) {
//      params.event.concludeEvent
//      ???
//    } else {
//      // Run one iteration of the timed loop and hit run again
//      ???
//    }
//}

// TODO: Add all the game state data as param, to save it before exit (update run)
case class ExitGameLoop(override val params: GameLoopParams = EmptyParams())
    extends GameLoop(params) {
  override def run: GameLoop = { System.exit(1); this }
}
