package system.logger

object Logger {
  sealed trait MODE
  case object ERROR extends MODE
  case object WARN extends MODE
  case object DEBUG extends MODE
  case object INFO extends MODE

  sealed trait LOCATION
  case object STD_OUT extends LOCATION
  case object FILE extends LOCATION

  def log(
      message: String,
      mode: MODE = INFO,
      location: LOCATION = STD_OUT): Unit =
    location match {
      case STD_OUT => logStdOut(message, mode)
      case FILE    => logFile(message, mode)
    }

  def logStdOut(message: String, mode: MODE = INFO): Unit = {
    val loggerPrefix: String = mode.toString
    println(s"[$loggerPrefix] $message")
  }

  def logFile(message: String, mode: MODE = INFO): Unit = {
    val loggerPrefix: String = mode.toString
    val output = s"[$loggerPrefix] $message"
    // TODO: Log to file
  }
}
