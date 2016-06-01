package game_logic

trait MetaData[T] {
  // Describes the life-cycle of an item
  val isActive: Boolean
  // A unique 32 bit id
  val id: T
}

package object types {
  type ActionTakerId = String
}
