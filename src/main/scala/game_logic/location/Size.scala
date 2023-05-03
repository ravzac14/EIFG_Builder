package game_logic.location

/** @eifgb_doc_enum: [[Size]] is a way for the game engine to calculate things like
  *             distance and time travelled. It is optional to make these statistics
  *             available to the Player, and if you decline to do so, [[Size]] doesn't
  *             really matter.
  */
// TODO: there will need to be concrete `Game*` versions of these Sizes that have been
// built when compiling there game. Anything that references these base sizes should
// actually use those (or possibly just multiply by some config value)
sealed trait Size {
  val min: Int
  val max: Int

  def range(scale: Int): Range =
    Range.inclusive(min * scale, max * scale)

  def average(scale: Int): Int =
    Math.round((range(scale).end - range(scale).start) / 2)
}

case class Huge(min: Int = 51, max: Int = 1000) extends Size {
  override def toString: String = "huge"
}

case class Large(min: Int = 11, max: Int = 50) extends Size {
  override def toString: String = "large"
}

case class Medium(min: Int = 5, max: Int = 10) extends Size {
  override def toString: String = "medium"
}

case class Small(min: Int = 1, max: Int = 4) extends Size {
  override def toString: String = "huge"
}
