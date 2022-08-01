package base

object StringFormatters {
  def gameTitleKey(title: String) =
    title.trim.filter(_.isLetterOrDigit).capitalize
}
