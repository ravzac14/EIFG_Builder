package commands.types

import scala.util.Try

trait HasInput {

  def validateInput(inputWords: Seq[String]): Try[Unit]
}
