package game_logic.global

import game_logic.event.{ TimedEvent, CombatEvent }
import game_logic.menu.MenuTree
import base.Utils._

// run returns the next GameLoop, which can just be the same loop updated
trait GameLoopParams {}
abstract class GameLoop(val params: GameLoopParams) { def run: GameLoop }

case class MenuLoopParams(val menu: MenuTree) extends GameLoopParams
case class MenuLoop(override val params: MenuLoopParams) extends GameLoop(params) {
  override def run: GameLoop = {
    params.menu.printMenu
    val selection = Console.readLine()

    if (Console.defaultCommands.contains(selection)) {
      Console.processDefaultCommand(selection)
      this
    } else {
      val maybeNewGameLoop: Either[MenuTree, GameLoop] = params.menu.processSelection(selection)
      maybeNewGameLoop match {
        case Left(mt) => this.copy(params.copy(menu = mt))
        case Right(gl) => gl
      }
    }
  }
}

// These probably take a bunch of globals
case class CombatLoopParams(val actorManager: ActorManager, val event: CombatEvent) extends GameLoopParams
class CombatLoop(params: CombatLoopParams) extends GameLoop(params) {
  override def run: GameLoop =
    if (params.actorManager.isPartyWiped || params.event.enemies.forall(_.isDead)) {
      // Do the sideEffects for either case
      ???
    } else {
      // Run one iteration of combat loop and hit run again
      ???
    }
}

// TODO: timeElapsed should be some Time Unit if I do that to TimedEvent
case class TimedLoopParams(val actorManager: ActorManager, event: TimedEvent, timeElapsed: Int) extends GameLoopParams
class TimedLoop(params: TimedLoopParams) extends GameLoop(params) {
  override def run: GameLoop =
    if (params.timeElapsed >= params.event.duration) {
      params.event.concludeEvent
      ???
    } else {
      // Run one iteration of the timed loop and hit run again
      ???
    }
}

// TODO: Add all the game state data as param, to save it before exit (update run)
case class ExitGameLoopParams() extends GameLoopParams
class ExitGameLoop(params: ExitGameLoopParams) extends GameLoop(params) {
  override def run: GameLoop = { Console.exit(); this }
}