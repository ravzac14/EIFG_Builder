package game_logic.location

import commands.CommandHelpers

import scala.util.{ Failure, Success, Try }

/** @eifgb_doc_enum */
sealed trait Direction {
  val name: String
  val abbreviation: String
}
case object North extends Direction {
  val name: String = "north"
  val abbreviation: String = "n"
}
case object Northeast extends Direction {
  val name: String = "northeast"
  val abbreviation: String = "ne"
}
case object Northwest extends Direction {
  val name: String = "northwest"
  val abbreviation: String = "nw"
}
case object East extends Direction {
  val name: String = "east"
  val abbreviation: String = "e"
}
case object South extends Direction {
  val name: String = "south"
  val abbreviation: String = "s"
}
case object Southeast extends Direction {
  val name: String = "southeast"
  val abbreviation: String = "se"
}
case object Southwest extends Direction {
  val name: String = "southwest"
  val abbreviation: String = "sw"
}
case object West extends Direction {
  val name: String = "west"
  val abbreviation: String = "w"
}

case object Up extends Direction {
  val name: String = "up"
  val abbreviation: String = "u"
}
case object Down extends Direction {
  val name: String = "down"
  val abbreviation: String = "d"
}

case object Fore extends Direction {
  val name: String = "fore"
  val abbreviation: String = "f"
}
case object Aft extends Direction {
  val name: String = "aft"
  val abbreviation: String = "a"
}
case object Port extends Direction {
  val name: String = "port"
  val abbreviation: String = "p"
}
case object Starboard extends Direction {
  val name: String = "starboard"
  val abbreviation: String = "st"
}

object Direction {

  class UnknownDirectionException(given: String)
      extends Exception(s"Input [$given] does not match any direction.")

  def findByAnyMeans(s: String): Try[Direction] = {
    val nameResult = byName(s)
    val abbreviationResult = byAbbreviation(s)

    if (nameResult.isSuccess) {
      nameResult
    } else {
      abbreviationResult
    }
  }

  def byAbbreviation(s: String): Try[Direction] =
    CommandHelpers.formatWord(s) match {
      case North.abbreviation     => Success(North)
      case Northeast.abbreviation => Success(Northeast)
      case Northwest.abbreviation => Success(Northwest)
      case East.abbreviation      => Success(East)
      case South.abbreviation     => Success(South)
      case Southeast.abbreviation => Success(Southeast)
      case Southwest.abbreviation => Success(Southwest)
      case West.abbreviation      => Success(West)

      case Up.abbreviation   => Success(Up)
      case Down.abbreviation => Success(Down)

      case Fore.abbreviation      => Success(Fore)
      case Aft.abbreviation       => Success(Aft)
      case Port.abbreviation      => Success(Port)
      case Starboard.abbreviation => Success(Starboard)

      case given => Failure(new UnknownDirectionException(given))
    }

  def byName(s: String): Try[Direction] = CommandHelpers.formatWord(s) match {
    case North.name     => Success(North)
    case Northeast.name => Success(Northeast)
    case Northwest.name => Success(Northwest)
    case East.name      => Success(East)
    case South.name     => Success(South)
    case Southeast.name => Success(Southeast)
    case Southwest.name => Success(Southwest)
    case West.name      => Success(West)

    case Up.name   => Success(Up)
    case Down.name => Success(Down)

    case Fore.name      => Success(Fore)
    case Aft.name       => Success(Aft)
    case Port.name      => Success(Port)
    case Starboard.name => Success(Starboard)

    case given => Failure(new UnknownDirectionException(given))
  }

  def opposite(d: Direction): Direction = d match {
    case North     => South
    case Northeast => Southwest
    case Northwest => Southeast
    case East      => West
    case South     => North
    case Southeast => Northwest
    case Southwest => Northeast
    case West      => East

    case Up   => Down
    case Down => Up

    case Fore      => Aft
    case Aft       => Fore
    case Port      => Starboard
    case Starboard => Port
  }
}
