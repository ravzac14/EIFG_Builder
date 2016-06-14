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
  * @param level        - Opportunities for stat gain
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
case class Stats(val health: DescriptiveStat = new DescriptiveStat(100),
                 val stamina: DescriptiveStat = new DescriptiveStat(100),
                 val armorMultiplier: Float = 1.0F,
                 val mana: DescriptiveStat = new DescriptiveStat(100),
                 val level: Int = 1,
                 val strength: ModifierStat = new ModifierStat(10),
                 val dexterity: ModifierStat = new ModifierStat(10),
                 val constitution: ModifierStat = new ModifierStat(10),
                 val intelligence: ModifierStat = new ModifierStat(10),
                 val wisdom: ModifierStat = new ModifierStat(10),
                 val charisma: ModifierStat = new ModifierStat(10),
                 val proficientSavingThrows: Set[SavingThrowStat] = Set(),
                 val proficientSkills: Set[ProficientStat] = Set()) {
  def modifyHealth(v: Int) = copy(health = new DescriptiveStat(this.health.value + v))
  def modifyStamina(v: Int) = copy(stamina = new DescriptiveStat(this.stamina.value + v))
  def setArmorModifier(v: Float) = copy(armorMultiplier = v)
  def modifyMana(v: Int) = copy(mana = new DescriptiveStat(this.mana.value + v))
  def modifyLevel(v: Int) = copy(level = this.level + v)
  def modifyStrength(v: Int) = copy(strength = new ModifierStat(this.strength.value + v))
  def modifyDexterity(v: Int) = copy(dexterity = new ModifierStat(this.dexterity.value + v))
  def modifyConstitution(v: Int) = copy(constitution = new ModifierStat(this.constitution.value + v))
  def modifyIntelligence(v: Int) = copy(intelligence = new ModifierStat(this.intelligence.value + v))
  def modifyWisdom(v: Int) = copy(wisdom = new ModifierStat(this.wisdom.value + v))
  def modifyCharisma(v: Int) = copy(charisma = new ModifierStat(this.charisma.value + v))

  def addProficientSavingThrow(s: SavingThrowStat) = copy(proficientSavingThrows = this.proficientSavingThrows + s)
  def removeProficientSavingThrow(s: SavingThrowStat) = copy(proficientSavingThrows = this.proficientSavingThrows - s)
  def addProficientSkill(s: ProficientStat) = copy(proficientSkills = this.proficientSkills + s)
  def removeProficientSkill(s: ProficientStat) = copy(proficientSkills = this.proficientSkills - s)

  val proficiencyBonus = level match {
    case l if l >= 15 => 5
    case l if l >= 10 => 4
    case l if l >= 5 => 3
    case _ => 2
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
    case v if v >= 8 => -1
    case v if v >= 6 => -2
    case v if v >= 4 => -3
    case v if v >= 2 => -4
    case _ => -5
  }
}

class DescriptiveStat(val value: Int) {
  val description: StatDescriptor = value match {
    case v if v >= 100 => StatDescriptor.Full
    case v if v >= 80 => StatDescriptor.Brimming
    case v if v >= 50 => StatDescriptor.Ample
    case v if v >= 20 => StatDescriptor.Lacking
    case v if v >= 1 => StatDescriptor.Drained
    case v if v == 0 => StatDescriptor.Empty
  }
}
