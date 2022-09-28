package base

import base.DateTime.Constants._
import base.DateTime._

import scala.math.Integral.Implicits.infixIntegralOps
import scala.util.{ Failure, Success, Try }

case class DateTime(
    year: Int = 0,
    month: Int = 0,
    day: Int = 0,
    hour: Int = 0,
    minute: Int = 0,
    second: Int = 0,
    millis: Long = 0) {

  /* Helpers */
  // Exclude current month and current day because they are only partially finished
  private def numFullDaysElapsed: Int =
    Months.take(month - 1).map(_.lastDayOfMonth(year)).sum + day - 1

  // Include current month and day which is only partially finished
  private def remainingDaysInclusive: Int =
    MAX_DAYS_PER_YEAR(year) - numFullDaysElapsed

  /* Mutators - ADD */
  def addBreakdown(b: Breakdown): DateTime = {
    val (numSecondsToAdd, newMillis) =
      safeDivRem((millis + b.millis), MAX_MILLIS_PER_SEC, (0L, millis))
    val (numMinToAdd, newSeconds) =
      safeDivRem(
        (second + numSecondsToAdd.toInt + b.seconds).toInt,
        MAX_SEC_PER_MIN,
        (0, second))
    val (numHourToAdd, newMinutes) =
      safeDivRem(
        (minute + numMinToAdd + b.minutes).toInt,
        MAX_MIN_PER_HOUR,
        (0, minute))
    val (numDayToAdd, newHours) =
      safeDivRem(
        (hour + numHourToAdd + b.hours).toInt,
        MAX_HOUR_PER_DAY,
        (0, hour))
    addDays(numDayToAdd).copy(
      hour = newHours,
      minute = newMinutes,
      second = newSeconds,
      millis = newMillis)
  }
  def addMillis(i: Long): DateTime = addBreakdown(breakDownMillis(i))
  def addSeconds(i: Int): DateTime = addBreakdown(breakDownSeconds(i))
  def addMinutes(i: Int): DateTime = addBreakdown(breakDownMinutes(i))
  def addHours(i: Int): DateTime = addBreakdown(breakDownHours(i))
  //  TODO: Months and years should convert to days but im lazy
  //  def addMonths(i: Int): DateTime = this.copy(month = month + i)
  //  def addYears(i: Int): DateTime = this.copy(year = year + i)
  def addDays(i: Int): DateTime = {
    if (i < remainingDaysInclusive) {
      val newFullDaysElapsed = i + numFullDaysElapsed
      val (newDay, maybeNewMonth) =
        monthAndDayForYear(newFullDaysElapsed, year)
      maybeNewMonth
        .map(m => this.copy(month = m.monthOfYear, day = newDay))
        .getOrElse(this)
    } else if (i > 0) {
      val maybeNewResult: Option[DateTime] = {
        val yearWithRemainingDays: Stream[(Int, Int)] =
          (year, remainingDaysInclusive) +: Stream
            .from(year + 1)
            .map(y => (y, MAX_DAYS_PER_YEAR(y)))

        val foundNewYearAndFullDaysElapsed: Option[(Int, Int)] =
          yearWithRemainingDays.zipWithIndex
            .map { case ((loopYear, loopRemainingDays), index) =>
              val sumUntilThisYear: Int =
                yearWithRemainingDays.take(index).map(_._2).sum
              val unaccountedDays = i - sumUntilThisYear
              ((loopYear, loopRemainingDays, unaccountedDays), index)
            }
            .collectFirst {
              case (
                    (loopYear, loopRemainingDays, unaccountedForDaysSoFar),
                    index) if loopRemainingDays > unaccountedForDaysSoFar =>
                (loopYear, unaccountedForDaysSoFar)
            }

        for {
          (newYear, newFullElapsedDays) <- foundNewYearAndFullDaysElapsed
          (newDay, maybeNewMonth) = monthAndDayForYear(
            newFullElapsedDays,
            newYear)
          newMonth <- maybeNewMonth
        } yield this.copy(
          year = newYear,
          month = newMonth.monthOfYear,
          day = newDay)
      }
      maybeNewResult.getOrElse(this)
    } else {
      this.minusDays(Math.abs(i))
    }
  }

  /* Mutators - MINUS */
  /** TODO(zack): this and then back to the game
    * right now its addBreakdown copy pastad
    *  * might not need this ... will keep a timestamp in the command queue to undo
    */
  def minusBreakdown(b: Breakdown): DateTime = {
//    val (numSecondsToMinus, newMillis) =
//      safeDivRem((millis + b.millis), MAX_MILLIS_PER_SEC, (0L, millis))
//    val (numMinToMinus, newSeconds) =
//      safeDivRem(
//        (second + numSecondsToMinus.toInt + b.seconds).toInt,
//        MAX_SEC_PER_MIN,
//        (0, second))
//    val (numHourToMinus, newMinutes) =
//      safeDivRem(
//        (minute + numMinToMinus + b.minutes).toInt,
//        MAX_MIN_PER_HOUR,
//        (0, minute))
//    val (numDayToMinus, newHours) =
//      safeDivRem(
//        (hour + numHourToMinus + b.hours).toInt,
//        MAX_HOUR_PER_DAY,
//        (0, hour))
//    minusDays(numDayToMinus).copy(
//      hour = newHours,
//      minute = newMinutes,
//      second = newSeconds,
//      millis = newMillis)
    ???
  }
  def minusMillis(i: Long): DateTime = minusBreakdown(breakDownMillis(i))
  def minusSeconds(i: Int): DateTime = minusBreakdown(breakDownSeconds(i))
  def minusMinutes(i: Int): DateTime = minusBreakdown(breakDownMinutes(i))
  def minusHours(i: Int): DateTime = minusBreakdown(breakDownHours(i))
  //  TODO: Months and years should convert to days but im lazy
  //  def minusMonths(i: Int): DateTime = this.copy(month = month + i)
  //  def minusYears(i: Int): DateTime = this.copy(year = year + i)
  def minusDays(i: Int): DateTime = {
    if (i <= numFullDaysElapsed) {
      val newFullDaysElapsed = numFullDaysElapsed - i
      val (newDay, maybeNewMonth) =
        monthAndDayForYear(newFullDaysElapsed, year)
      maybeNewMonth
        .map(m => this.copy(month = m.monthOfYear, day = newDay))
        .getOrElse(this)
    } else if (i > 0) {
      val maybeNewResult: Option[DateTime] = {
        val yearWithRemainingDays: Stream[(Int, Int)] =
          (year, numFullDaysElapsed) +: Stream
            .from(year - 1, step = -1)
            .map(y => (y, MAX_DAYS_PER_YEAR(y)))

        val foundNewYearAndFullDaysElapsed: Option[(Int, Int)] =
          yearWithRemainingDays.zipWithIndex
            .map { case ((loopYear, loopRemainingDays), index) =>
              val sumUntilThisYear: Int =
                yearWithRemainingDays.take(index).map(_._2).sum
              val unaccountedDays = i - sumUntilThisYear
              ((loopYear, loopRemainingDays, unaccountedDays), index)
            }
            .collectFirst {
              case ((loopYear, loopRemainingDays, unaccountedForDaysSoFar), _)
                  if loopRemainingDays > unaccountedForDaysSoFar =>
                (loopYear, unaccountedForDaysSoFar)
            }

        for {
          (newYear, newFullDaysFromEOY) <- foundNewYearAndFullDaysElapsed
          newFullElapsedDays = MAX_DAYS_PER_YEAR(newYear) - newFullDaysFromEOY
          (newDay, maybeNewMonth) = monthAndDayForYear(
            newFullElapsedDays,
            newYear)
          newMonth <- maybeNewMonth
        } yield this.copy(
          year = newYear,
          month = newMonth.monthOfYear,
          day = newDay)
      }
      maybeNewResult.getOrElse(this)
    } else {
      this.addDays(Math.abs(i))
    }
  }
}

object DateTime {

  object Constants {
    sealed trait MONTH {
      val monthOfYear: Int
      def lastDayOfMonth(year: Long): Int
    }
    case object JAN extends MONTH {
      val monthOfYear: Int = 1
      def lastDayOfMonth(year: Long): Int = 31
    }
    case object FEB extends MONTH {
      val monthOfYear: Int = 2
      def lastDayOfMonth(year: Long): Int =
        if (isLeapYear(year)) 29
        else 28
    }
    case object MAR extends MONTH {
      val monthOfYear: Int = 3
      def lastDayOfMonth(year: Long): Int = 31
    }
    case object APR extends MONTH {
      val monthOfYear: Int = 4
      def lastDayOfMonth(year: Long): Int = 30
    }
    case object MAY extends MONTH {
      val monthOfYear: Int = 5
      def lastDayOfMonth(year: Long): Int = 31
    }
    case object JUN extends MONTH {
      val monthOfYear: Int = 6
      def lastDayOfMonth(year: Long): Int = 30
    }
    case object JUL extends MONTH {
      val monthOfYear: Int = 7
      def lastDayOfMonth(year: Long): Int = 31
    }
    case object AUG extends MONTH {
      val monthOfYear: Int = 8
      def lastDayOfMonth(year: Long): Int = 31
    }
    case object SEP extends MONTH {
      val monthOfYear: Int = 9
      def lastDayOfMonth(year: Long): Int = 30
    }
    case object OCT extends MONTH {
      val monthOfYear: Int = 10
      def lastDayOfMonth(year: Long): Int = 31
    }
    case object NOV extends MONTH {
      val monthOfYear: Int = 11
      def lastDayOfMonth(year: Long): Int = 30
    }
    case object DEC extends MONTH {
      val monthOfYear: Int = 12
      def lastDayOfMonth(year: Long): Int = 31
    }
    val Months: Seq[MONTH] =
      Seq(JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC)

    val MIN: Int = 0
    val MAX_MILLIS_PER_SEC: Int = 1000
    val MAX_MILLIS_PER_MIN: Int = 60000
    val MAX_MILLIS_PER_HOUR: Int = 3600000
    val MAX_MILLIS_PER_DAY: Int = 86400000
    val MAX_SEC_PER_MIN: Int = 60
    val MAX_SEC_PER_HOUR: Int = 3600
    val MAX_SEC_PER_DAY: Int = 86400
    val MAX_MIN_PER_HOUR: Int = 60
    val MAX_MIN_PER_DAY: Int = 1440
    val MAX_HOUR_PER_DAY: Int = 24
    val MAX_MONTH_PER_YEAR: Int = 12
    def MAX_DAYS_PER_YEAR(year: Long): Int =
      if (isLeapYear(year)) 366
      else 365
  }

  case class Breakdown(
      days: Long = 0,
      hours: Long = 0,
      minutes: Long = 0,
      seconds: Long = 0,
      millis: Long = 0)

  class UnknownMonthException(givenMonth: Int)
      extends Exception(s"Unknown month number $givenMonth")

  def empty: DateTime = DateTime()

  def isLeapYear(year: Long): Boolean =
    (year % 100 != 0 && year % 4 == 0) || year % 400 == 0

  def safeDivRem(a: Int, b: Int, default: (Int, Int)): (Int, Int) =
    if (b != 0) a /% b else default

  def safeDivRem(a: Long, b: Long, default: (Long, Long)): (Long, Long) = {
    val result = if (b != 0) a /% b else default
    println(
      s"safeDivRem a/%b, a is $a, b is $b, and default is $default - result is $result")
    result
  }

  def zeros: (Long, Long) = (0L, 0L)

  def breakDownMillis(i: Long): Breakdown = {
    val (days, remainingAfterDays) = safeDivRem(i, MAX_MILLIS_PER_DAY, zeros)
    val (hours, remainingAfterHours) =
      safeDivRem(remainingAfterDays, MAX_MILLIS_PER_HOUR, zeros)
    val (mins, remainingAfterMins) =
      safeDivRem(remainingAfterHours, MAX_MILLIS_PER_MIN, zeros)
    val (secs, remainingAfterSecs) =
      safeDivRem(remainingAfterMins, MAX_MILLIS_PER_SEC, zeros)
    Breakdown(
      days = days,
      hours = hours,
      minutes = mins,
      seconds = secs,
      millis = remainingAfterSecs)
  }

  def breakDownSeconds(i: Long): Breakdown = {
    val (days, remainingAfterDays) =
      safeDivRem(i, MAX_SEC_PER_DAY.toLong, zeros)
    val (hours, remainingAfterHours) =
      safeDivRem(remainingAfterDays, MAX_SEC_PER_HOUR.toLong, zeros)
    val (mins, remainingAfterMins) =
      safeDivRem(remainingAfterHours, MAX_SEC_PER_MIN.toLong, zeros)
    Breakdown(
      days = days,
      hours = hours,
      minutes = mins,
      seconds = remainingAfterMins)
  }

  def breakDownMinutes(i: Long): Breakdown = {
    val (days, remainingAfterDays) =
      safeDivRem(i, MAX_MIN_PER_DAY.toLong, zeros)
    val (hours, remainingAfterHours) =
      safeDivRem(remainingAfterDays, MAX_MIN_PER_HOUR.toLong, zeros)
    Breakdown(days = days, hours = hours, minutes = remainingAfterHours)
  }

  def breakDownHours(i: Long): Breakdown = {
    val (days, remainingAfterDays) =
      safeDivRem(i, MAX_HOUR_PER_DAY.toLong, zeros)
    Breakdown(days = days, hours = remainingAfterDays)
  }

  def monthAndDayForYear(
      numFullDaysElapsed: Int,
      year: Int): (Int, Option[MONTH]) = {
    assert(
      numFullDaysElapsed <= MAX_DAYS_PER_YEAR(year),
      s"numDaysElapsed [$numFullDaysElapsed] cannot be greater " +
        s"than the number of days in given year [${MAX_DAYS_PER_YEAR(year)}]"
    )
    assert(
      numFullDaysElapsed >= MIN,
      s"numDaysElapsed [$numFullDaysElapsed] cannot be negative")

    Months.foldLeft[(Int, Option[MONTH])]((numFullDaysElapsed, None)) {
      case ((daysLeft, None), currentMonth)
          if daysLeft >= currentMonth.lastDayOfMonth(year) =>
        (daysLeft - currentMonth.lastDayOfMonth(year), None)
      case ((daysLeft, None), currentMonth) =>
        (daysLeft + 1, Some(currentMonth))
      case ((foundDay, Some(foundMonth)), _) =>
        (foundDay, Some(foundMonth))
    }
  }

  def intAsMonth(i: Int): Try[MONTH] =
    i match {
      case 1  => Success(JAN)
      case 2  => Success(FEB)
      case 3  => Success(MAR)
      case 4  => Success(APR)
      case 5  => Success(MAY)
      case 6  => Success(JUN)
      case 7  => Success(JUL)
      case 8  => Success(AUG)
      case 9  => Success(SEP)
      case 10 => Success(OCT)
      case 11 => Success(NOV)
      case 12 => Success(DEC)
      case _  => Failure(new UnknownMonthException(i))
    }
}
