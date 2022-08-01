package commands

object ListCommandHelpers {
  sealed trait ListTarget { val name: String }
  case object Command extends ListTarget {
    val name: String = "command"
  }
}
