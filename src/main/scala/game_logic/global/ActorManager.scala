package game_logic

import game_logic.character.Actor
import game_logic.stats.StatDescriptor

class ActorManager(pcs: Set[Actor]) {
  def partyWiped: Boolean = pcs.forall(_.stats.health == StatDescriptor.Empty)
}