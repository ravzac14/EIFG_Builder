package commands

import commands.Exceptions.InvalidInputException

import scala.util.{ Failure, Success, Try }

object InputValidators {

  def isNonEmpty(i: Seq[String])(commandName: String): Try[Unit] =
    if (i.exists(_.nonEmpty))
      Success(())
    else
      Failure(
        new InvalidInputException(
          name = commandName,
          inputWords = i,
          reason = "Found empty input"))

  def isAllNumber(i: Seq[String])(commandName: String): Try[Unit] =
    if (i.forall(CommandHelpers.isNumber)) {
      Success(())
    } else {
      Failure(
        new InvalidInputException(
          name = commandName,
          inputWords = i,
          reason = "Found non-numeral input"))
    }

  def isInputSize(i: Seq[String], expected: Int)(
      commandName: String): Try[Unit] =
    if (i.size == expected) {
      Success(())
    } else {
      Failure(
        new InvalidInputException(
          name = commandName,
          inputWords = i,
          reason = s"Found input not of size $expected"
        )
      )
    }

  def isContainedBy(i: String, values: Seq[String])(
      commandName: String): Try[Unit] =
    if (values.contains(i)) {
      Success(())
    } else {
      Failure(
        new InvalidInputException(
          name = commandName,
          inputWords = Seq(i),
          reason = "Given value not contained in given set"
        )
      )
    }
}
