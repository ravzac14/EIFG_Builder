package base

trait AppWithJsonFormats extends App {
  implicit val formats = org.json4s.DefaultFormats
}

