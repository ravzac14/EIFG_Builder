package game_logic.global.game_loop

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
