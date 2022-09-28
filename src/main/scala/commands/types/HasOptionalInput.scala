package commands.types

import scala.util.{ Success, Try }

trait HasOptionalInput {

  protected def validateInput(inputWords: Seq[String]): Try[Unit]

  final def validateOptionalInput(inputWords: Seq[String]): Try[Unit] =
    if (inputWords.isEmpty) Success(())
    else validateInput(inputWords)
}
