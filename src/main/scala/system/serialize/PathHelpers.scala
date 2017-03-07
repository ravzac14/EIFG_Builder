package system.serialize

import scala.reflect.io.Path._
import scala.util.Try

object PathHelpers {
  object Constants {
    val HomeDirectoryPath: String = System.getProperty("user.home")
    val GameDirectoryPath = HomeDirectoryPath + "/eifg"
    val GameFileType = ".eifg"

    /* Save File Constants */
    val SaveFilePrefix = "youcantryitmightbreaktho"
    val SaveFilePathPrefix = GameDirectoryPath + "/" + SaveFilePrefix
    val SaveFileRegex = s"""$SaveFilePrefix[0-9]+$GameFileType""".r
  }

  object FileType extends Enumeration {
    val Save = Value
  }

  case class FileEntry(
    path: String,
    fileType: FileType.Value) {
    val fileName =
      path.replaceFirst(Constants.GameDirectoryPath, "")

    val createdAt: Option[Long] = {
      val trimmed =
        fileName.dropWhile(!_.isDigit).dropRight(Constants.GameFileType.length)
      if (isValidTimeString(trimmed)) Some(trimmed.toLong) else None
    }
  }

  private def isValidTimeString(s: String) =
    s.forall(_.isDigit) && (s.length >= 10) // Current epoch time in seconds

  private def constantsForFileType(fileType: FileType.Value) =
    fileType match {
      case FileType.Save =>
        (Constants.SaveFileRegex, Constants.SaveFilePathPrefix)

      case _ => throw new Exception("Unknown file type!")
    }

  def allFileEntries(fileType: FileType.Value): Try[Seq[FileEntry]] = Try {
    val (regex, _) = constantsForFileType(fileType)
    val allEntries =
      Constants.GameDirectoryPath
        .toDirectory
        .files
        .map(_.name)
        .flatMap {
          case matchingFile@regex() => Some(FileEntry(matchingFile, fileType))
          case _ => None
        }
        .toSeq
    assert(allEntries.forall(_.createdAt.isDefined),
      s"""FILE ERROR - Malformed file creation time for files:
          |  ${allEntries.collect { case e if e.createdAt.isEmpty => e.path }.mkString("\n  ")}""".stripMargin)
    allEntries
  }

  def nNewestFileEntries(fileType: FileType.Value, n: Int = 1): Try[Seq[FileEntry]] =
    for {
      allFileEntries <- allFileEntries(fileType)
    } yield allFileEntries.sortBy(_.createdAt.getOrElse(Long.MinValue)).take(n)

  def nOldestFileEntries(fileType: FileType.Value, n: Int = 1): Try[Seq[FileEntry]] =
    for {
      allFileEntries <- allFileEntries(fileType)
    } yield allFileEntries.sortBy(-_.createdAt.getOrElse(Long.MaxValue)).take(n)

  def getNextFileEntry(fileType: FileType.Value): Try[FileEntry] = {
    val (_, pathPrefix: String) = constantsForFileType(fileType)
    val currentTimeInSeconds: Long = System.currentTimeMillis() / 1000L
    val newPath = s"$pathPrefix$currentTimeInSeconds${Constants.GameFileType}"
    for {
      allFileEntries <- allFileEntries(fileType)
      if !allFileEntries.map(_.path).contains(newPath)
    } yield FileEntry(newPath, fileType)
  }
}