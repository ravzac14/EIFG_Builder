package commands.types

import scala.util.Try

trait HasFreeformInput {

  def validateFreeformInput(inputWords: Seq[String]): Try[Unit]
}
