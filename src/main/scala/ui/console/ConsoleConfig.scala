package ui.console

case class ConsoleConfig(
    writePrefix: Option[String] = None,
    writePostfix: Option[String] = None,
    readPrefix: Option[String] = None
)
