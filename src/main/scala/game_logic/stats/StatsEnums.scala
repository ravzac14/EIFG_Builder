package game_logic.stats

object StatDescriptor extends Enumeration {
  type StatDescriptor = Value

  // Value represents a minimum value
  val Full = Value(100)
  val Brimming = Value(80)
  val Ample = Value(50)
  val Lacking = Value(20)
  val Drained = Value(1)
  val Empty = Value(0)
}

object SavingThrowStat extends Enumeration {
  type SavingThrowStat = Value

  val Strength = Value("strength")
  val Dexterity = Value("dexterity")
  val Constitution = Value("constitution")
  val Intelligence = Value("intelligence")
  val Wisdom = Value("wisdom")
  val Charisma = Value("charisma")
}

object ProficientStat extends Enumeration {
  type ProficientStat = Value

  val Acrobatics = Value("acrobatics")
  val AnimalHandling = Value("animal handling")
  val Arcana = Value("arcana")
  val Athletics = Value("athletics")
  val Deception = Value("deception")
  val History = Value("history")
  val Insight = Value("insight")
  val Intimidation = Value("intimidation")
  val Investigation = Value("investigation")
  val Medicine = Value("medicine")
  val Nature = Value("nature")
  val Perception = Value("perception")
  val Performance = Value("performance")
  val Persuasion = Value("persuasion")
  val Religion = Value("religion")
  val SleightOfHand = Value("sleight of hand")
  val Stealth = Value("stealth")
  val Survival = Value("survival")
}
