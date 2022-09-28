package game_logic.global.managers

import game_logic.character.Actor

class ActorManager(pcs: Set[Actor]) {
  def isPartyWiped: Boolean = pcs.forall(_.isDead)
}
