package base.data_structures

import base.Utils

case class Meta[T <: String](
    id: T = Utils.generateUntypedId.asInstanceOf[T],
    isActive: Boolean = true,
    timeCreated: Long = System.currentTimeMillis(),
    lastModified: Long = System.currentTimeMillis()) {

  def touch(
      newLastModified: Long = System.currentTimeMillis(),
      newIsActive: Option[Boolean] = None): Meta[T] =
    if (newIsActive.isDefined)
      this.copy(isActive = newIsActive.get, lastModified = newLastModified)
    else
      this.copy(lastModified = newLastModified)

  def deactivate(): Meta[T] = copy(isActive = false)

  def activate(): Meta[T] = copy(isActive = true)
}
