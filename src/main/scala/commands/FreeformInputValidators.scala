package commands

import commands.Exceptions.InvalidFreeformInputException

import scala.util.{ Failure, Success, Try }

object FreeformInputValidators {

  def isNonEmpty(i: Seq[String])(commandName: String): Try[Unit] =
    if (i.exists(_.nonEmpty))
      Success(())
    else
      Failure(
        new InvalidFreeformInputException(
          name = commandName,
          inputWords = i,
          reason = "Found empty input"))

  def isAllNumber(i: Seq[String])(commandName: String): Try[Unit] =
    if (i.forall(CommandHelpers.isNumber)) {
      Success(())
    } else {
      Failure(
        new InvalidFreeformInputException(
          name = commandName,
          inputWords = i,
          reason = "Found non-numeral input"))
    }
}
