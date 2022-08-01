package game_logic.stats

import game_logic.stats.ProficientStat.ProficientStat
import game_logic.stats.SavingThrowStat.SavingThrowStat
import game_logic.stats.StatDescriptor.StatDescriptor

/** Most of these stats are optional because most
  *   games won't be using the in-depth stats
  *
  * @param health          - 0-100 value, represents a percent
  * @param stamina         - 0-100 value, represents a percent
  * @param armorMultiplier - 0.0 - 2.0, used to multiply incoming damage for
  *                        reduction or increase
  *
  * This can calculate any combat, probably will just use
  *   strength mostly
  * @param experience   - Gauges a characters level
  * @param strength     - How hard one hits stuff
  * @param dexterity    - Sneak/quick/jumpy
  * @param constitution - Drinking/eating/resists
  * @param intelligence - Traps/Magic
  * @param wisdom       - Nature/Survival
  * @param charisma     - Talkin/convincin/intimidatin
  *
  * These are around for further character customization,
  *   they pretty much just say what specific thing you're good at
  * @param proficientSavingThrows - Which saving throws you're good at
  * @param proficientSkills - Which skills you're especially good at
  */
case class Stats(
    val health: DescriptiveStat = new DescriptiveStat(100),
    val stamina: DescriptiveStat = new DescriptiveStat(100),
    val armorMultiplier: Float = 1.0f,
    val mana: DescriptiveStat = new DescriptiveStat(100),
    val experience: Int = 1,
    val strength: ModifierStat = new ModifierStat(10),
    val dexterity: ModifierStat = new ModifierStat(10),
    val constitution: ModifierStat = new ModifierStat(10),
    val intelligence: ModifierStat = new ModifierStat(10),
    val wisdom: ModifierStat = new ModifierStat(10),
    val charisma: ModifierStat = new ModifierStat(10),
    val proficientSavingThrows: Set[SavingThrowStat] = Set(),
    val proficientSkills: Set[ProficientStat] = Set()
) {
  def modifyHealth(v: Int) =
    copy(health = new DescriptiveStat(this.health.value + v))
  def modifyStamina(v: Int) =
    copy(stamina = new DescriptiveStat(this.stamina.value + v))
  def setArmorModifier(v: Float) = copy(armorMultiplier = v)
  def modifyMana(v: Int) = copy(mana = new DescriptiveStat(this.mana.value + v))
  def modifyExperience(v: Int) = copy(experience = this.experience + v)
  def modifyStrength(v: Int) =
    copy(strength = new ModifierStat(this.strength.value + v))
  def modifyDexterity(v: Int) =
    copy(dexterity = new ModifierStat(this.dexterity.value + v))
  def modifyConstitution(v: Int) =
    copy(constitution = new ModifierStat(this.constitution.value + v))
  def modifyIntelligence(v: Int) =
    copy(intelligence = new ModifierStat(this.intelligence.value + v))
  def modifyWisdom(v: Int) =
    copy(wisdom = new ModifierStat(this.wisdom.value + v))
  def modifyCharisma(v: Int) =
    copy(charisma = new ModifierStat(this.charisma.value + v))

  def addProficientSavingThrow(s: SavingThrowStat) =
    copy(proficientSavingThrows = this.proficientSavingThrows + s)
  def removeProficientSavingThrow(s: SavingThrowStat) =
    copy(proficientSavingThrows = this.proficientSavingThrows - s)
  def addProficientSkill(s: ProficientStat) =
    copy(proficientSkills = this.proficientSkills + s)
  def removeProficientSkill(s: ProficientStat) =
    copy(proficientSkills = this.proficientSkills - s)

  // TODO: This might be something based arbitrarily, not dnd stats
  // but for now here's dnd stats
  def level = experience match {
    case e if e >= 355000 => 20
    case e if e >= 305000 => 19
    case e if e >= 265000 => 18
    case e if e >= 225000 => 17
    case e if e >= 195000 => 16
    case e if e >= 165000 => 15
    case e if e >= 140000 => 14
    case e if e >= 120000 => 13
    case e if e >= 100000 => 12
    case e if e >= 85000  => 11
    case e if e >= 64000  => 10
    case e if e >= 48000  => 9
    case e if e >= 34000  => 8
    case e if e >= 23000  => 7
    case e if e >= 14000  => 6
    case e if e >= 6500   => 5
    case e if e >= 2700   => 4
    case e if e >= 900    => 3
    case e if e >= 300    => 2
    case _                => 1
  }

  val proficiencyBonus = level match {
    case l if l >= 17 => 6
    case l if l >= 13 => 5
    case l if l >= 9  => 4
    case l if l >= 5  => 3
    case _            => 2
  }
}

class ModifierStat(val value: Int) {
  val modifier: Int = value match {
    case v if v >= 30 => 10
    case v if v >= 28 => 9
    case v if v >= 26 => 8
    case v if v >= 24 => 7
    case v if v >= 22 => 6
    case v if v >= 20 => 5
    case v if v >= 18 => 4
    case v if v >= 16 => 3
    case v if v >= 14 => 2
    case v if v >= 12 => 1
    case v if v >= 10 => 0
    case v if v >= 8  => -1
    case v if v >= 6  => -2
    case v if v >= 4  => -3
    case v if v >= 2  => -4
    case _            => -5
  }
}

class DescriptiveStat(val value: Int) {
  val description: StatDescriptor = value match {
    case v if v >= 100 => StatDescriptor.Full
    case v if v >= 80  => StatDescriptor.Brimming
    case v if v >= 50  => StatDescriptor.Ample
    case v if v >= 20  => StatDescriptor.Lacking
    case v if v >= 1   => StatDescriptor.Drained
    case v if v == 0   => StatDescriptor.Empty
  }
}
