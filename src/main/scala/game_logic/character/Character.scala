package game_logic.character

import game_logic.character.Character.{ Disposition, Neutral }
import game_logic.stats.ProficientStat.ProficientStat
import game_logic.stats.SavingThrowStat.SavingThrowStat
import game_logic.stats.{ Buff, StatDescriptor, Stats }

// TODO: I think most of this we won't use or will read in from config
case class Character(
    name: String = "Steve",
    description: String = "",
    age: Int = 23,
    disposition: Disposition = Neutral,
    buffs: Set[Buff] = Set(),
    stats: Stats = Stats()) {
  def setName(n: String): Character = copy(name = n)
  def setDescription(d: String): Character = copy(description = d)
  def appendDescription(d: String): Character =
    copy(description = this.description + " " + d)
  def setAge(a: Int): Character = copy(age = a)
  def modifyAge(v: Int): Character = copy(age = age + v)
  def setDisposition(d: Disposition): Character = copy(disposition = d)
  def addBuff(b: Buff): Character = copy(buffs = this.buffs + b)
  def removeBuff(b: Buff): Character = copy(buffs = this.buffs - b)

  private def updateStat(f: Stats => Stats): Character = copy(stats = f(stats))
  def modifyHealth(v: Int): Character = updateStat(_.modifyHealth(v))
  def modifyStamina(v: Int): Character = updateStat(_.modifyStamina(v))
  def setArmorModifier(v: Float): Character = updateStat(_.setArmorModifier(v))
  def modifyMana(v: Int): Character = updateStat(_.modifyMana(v))
  def modifyStrength(v: Int): Character = updateStat(_.modifyStrength(v))
  def modifyDexterity(v: Int): Character = updateStat(_.modifyDexterity(v))
  def modifyConstitution(v: Int): Character = updateStat(
    _.modifyConstitution(v))
  def modifyIntelligence(v: Int): Character = updateStat(
    _.modifyIntelligence(v))
  def modifyWisdom(v: Int): Character = updateStat(_.modifyWisdom(v))
  def modifyCharisma(v: Int): Character = updateStat(_.modifyCharisma(v))
  def addProficientSavingThrow(s: SavingThrowStat): Character = updateStat(
    _.addProficientSavingThrow(s)
  )
  def removeProficientSavingThrow(s: SavingThrowStat): Character = updateStat(
    _.removeProficientSavingThrow(s)
  )
  def addProficientSkill(s: ProficientStat): Character = updateStat(
    _.addProficientSkill(s)
  )
  def removeProficientSkill(s: ProficientStat): Character = updateStat(
    _.removeProficientSkill(s)
  )

  def modifyExperience(v: Int): Character = updateStat(_.modifyExperience(v))

  // TODO: Expand on this (alter based on stats, age, race?, etc)
  def defaultDescription: String =
    s"$name is $age years old, and has a $disposition disposition."
  def isDead: Boolean = stats.health.description == StatDescriptor.Empty
}

object Character {
  sealed trait Disposition {
    val name: String
  }
  case object Friendly extends Disposition {
    val name: String = "friendly"
  }
  case object Neutral extends Disposition {
    val name: String = "neutral"
  }
  case object Hostile extends Disposition {
    val name: String = "hostile"
  }

  sealed trait EquipmentZone {
    val name: String
  }
  case object Hand {
    val name: String = "hand"
  }
  case object Feet {
    val name: String = "feet"
  }
  case object Eyes {
    val name: String = "eyes"
  }
  case object Head {
    val name: String = "head"
  }
}
