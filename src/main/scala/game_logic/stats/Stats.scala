package game_logic.stats

import game_logic.stats.ProficientStat.ProficientStat
import game_logic.stats.SavingThrowStat.SavingThrowStat
import game_logic.stats.StatDescriptor.StatDescriptor

/** Something that pretty much everything has, but won't
  *   necessarily use.
  *
  * @param health - 0-100 value, represents a percent
  * @param stamina - 0-100 value, represents a percent
  * @param armorMultiplier - 0.0 - 2.0, used to multiply incoming damage for
  *                        reduction or increase
  */
class BaseStats(val health: DescriptiveStat = new DescriptiveStat(100),
                val stamina: DescriptiveStat = new DescriptiveStat(100),
                val armorMultiplier: Float = 1.0F)

/** Got to think about this a bit
  *
  * @param mana - 0-100 value, represents a percent
  */
class MagicUserStats(val mana: DescriptiveStat = new DescriptiveStat(100)) extends BaseStats {

}

/** This can calculate any combat, probably will just use
  *   strength mostly
  *
  * @param level - Opportunities for stat gain
  * @param strength - How hard one hits stuff
  * @param dexterity - Sneak/quick/jumpy
  * @param constitution - Drinking/eating/resists
  * @param intelligence - Traps/Magic
  * @param wisdom - Nature/Survival
  * @param charisma - Talkin/convincin/intimidatin
  */
class ExtendedStats(val level: Int = 1,
                    val strength: ModifierStat = new ModifierStat(10),
                    val dexterity: ModifierStat = new ModifierStat(10),
                    val constitution: ModifierStat = new ModifierStat(10),
                    val intelligence: ModifierStat = new ModifierStat(10),
                    val wisdom: ModifierStat = new ModifierStat(10),
                    val charisma: ModifierStat = new ModifierStat(10)) extends BaseStats

class DndStats(val proficientSavingThrows: Set[SavingThrowStat] = Set(),
               val proficientSkills: Set[ProficientStat] = Set()) extends ExtendedStats

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
