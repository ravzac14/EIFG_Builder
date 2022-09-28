package game_logic.global.game_loop

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
