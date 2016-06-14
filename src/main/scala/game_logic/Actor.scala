package game_logic

import game_logic.Disposition.Disposition
import game_logic.stats.Stats

case class Character(val name: String = "Steve",
                     val age: Int = 23,
                     val disposition: Disposition = Disposition.Neutral,
                     val buffs: Set[Buff] = Set(),
                     val inventory: Set[Item] = Set()) {
  def setName(n: String) = copy(name = n)
  def modifyAge(v: Int) = copy(age = age + v)
  def setDisposition(d: Disposition) = copy(disposition = d)
  def addBuff(b: Buff) = copy(buffs = this.buffs + b)
  def removeBuff(b: Buff) = copy(buffs = this.buffs - b)
  def addItem(i: Item) = copy(inventory = this.inventory + i)
  def removeItem(i: Item) = copy(inventory = this.inventory - i)

  var stats: Stats = Stats()
  def updateStats(f: Stats => Stats): Unit = stats = f(stats)
}

class Actor(canUnRe: Boolean) extends ActionTaker {
  canUndoRedo = canUnRe

  var character: Character = new Character()
  def updateCharacter(f: Character => Character): Unit = character = f(character)
}

object Disposition extends Enumeration {
  type Disposition = Value

  val Happy = Value("happy")
  val Neutral = Value("neutral")
  val Angry = Value("angry")
}

object ActorHelpers {

  // TODO: This will become way more complex
  def buildPlayableCharacter(canUndoRedo: Boolean): Actor = new Actor(canUndoRedo)
}
