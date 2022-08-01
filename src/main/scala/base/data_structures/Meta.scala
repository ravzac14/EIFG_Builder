package base.data_structures

import base.Utils

/** Holds the inner life/identifier data for any given object
  *
  * @param id - A unique 32 bit id
  * @param isActive - Describes the life-cycle of an item
  * @tparam T - Should describe the Id type and extend String
  */
case class Meta[T <: String](
    val id: T = Utils.generateUntypedId.asInstanceOf[T],
    val isActive: Boolean = true,
    val timeCreated: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
) {
  def touch(
      newLastModified: Long = System.currentTimeMillis(),
      newIsActive: Option[Boolean] = None
  ) =
    if (newIsActive.isDefined)
      this.copy(isActive = newIsActive.get, lastModified = newLastModified)
    else this.copy(lastModified = newLastModified)
  def deactivate() = copy(isActive = false)
  def activate() = copy(isActive = true)
}
