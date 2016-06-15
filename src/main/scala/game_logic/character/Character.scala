package game_logic.character

import game_logic.character.Disposition.Disposition
import game_logic.item.Item
import game_logic.stats.ProficientStat.ProficientStat
import game_logic.stats.SavingThrowStat.SavingThrowStat
import game_logic.stats.{Stats, Buff}

case class Character(val name: String = "Steve",
                     val description: String = "",
                     val age: Int = 23,
                     val disposition: Disposition = Disposition.Neutral,
                     val buffs: Set[Buff] = Set(),
                     val inventory: Set[Item] = Set()) {
  def setName(n: String) = copy(name = n)
  def setDescription(d: String) = copy(description = d)
  def modifyAge(v: Int) = copy(age = age + v)
  def setDisposition(d: Disposition) = copy(disposition = d)
  def addBuff(b: Buff) = copy(buffs = this.buffs + b)
  def removeBuff(b: Buff) = copy(buffs = this.buffs - b)
  def addItem(i: Item) = copy(inventory = this.inventory + i)
  def removeItem(i: Item) = copy(inventory = this.inventory - i)

  var stats: Stats = Stats()
  def updateStat(f: Stats => Stats): Unit = stats = f(stats)
  def modifyHealth(v: Int) = updateStat(_.modifyHealth(v))
  def modifyStamina(v: Int) = updateStat(_.modifyStamina(v))
  def setArmorModifier(v: Float) = updateStat(_.setArmorModifier(v))
  def modifyMana(v: Int) = updateStat(_.modifyMana(v))
  def modifyStrength(v: Int) = updateStat(_.modifyStrength(v))
  def modifyDexterity(v: Int) = updateStat(_.modifyDexterity(v))
  def modifyConstitution(v: Int) = updateStat(_.modifyConstitution(v))
  def modifyIntelligence(v: Int) = updateStat(_.modifyIntelligence(v))
  def modifyWisdom(v: Int) = updateStat(_.modifyWisdom(v))
  def modifyCharisma(v: Int) = updateStat(_.modifyCharisma(v))
  def addProficientSavingThrow(s: SavingThrowStat) = updateStat(_.addProficientSavingThrow(s))
  def removeProficientSavingThrow(s: SavingThrowStat) = updateStat(_.removeProficientSavingThrow(s))
  def addProficientSkill(s: ProficientStat) = updateStat(_.addProficientSkill(s))
  def removeProficientSkill(s: ProficientStat) = updateStat(_.removeProficientSkill(s))

  // This one is special, because it could mean "leveling up"
  def modifyExperience(v: Int, withLevelUp: Boolean = true) = {
    val startingLevel = stats.level
    updateStat(_.modifyExperience(v))
    if (withLevelUp) {
      val endingLevel = stats.level
      if (endingLevel > startingLevel)
        (startingLevel to endingLevel).foreach(CharacterHelpers.levelUp(this, _))
    }
  }

  // TODO: Expand on this (alter based on stats, age, race?, etc)
  def defaultDescription = s"$name is $age years old, and has a $disposition disposition."
}

object Disposition extends Enumeration {
  type Disposition = Value

  val Friendly = Value("friendly")
  val Neutral = Value("neutral")
  val Hostile = Value("hostile")
}

object CharacterHelpers {
  // TODO: Expand on this when more has been fleshed out
  def levelUp(c: Character, l: Int) = ???
}
