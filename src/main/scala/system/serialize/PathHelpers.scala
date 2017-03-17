package system.serialize

import base.Utils

import scala.util.Try
import scala.util.matching.Regex

object PathHelpers {
  object Constants {
    val HomeDirectoryPath: String = System.getProperty("user.home")
    val EIFGDirectoryPath = HomeDirectoryPath + "/eifg"
    val EIFGFileType = ".eg"
    val SerializedFileType = ".sl"
    val ConfigFileType = ".cg"

    /******************** Master File Related **********************/
    val MasterFileName =
      "youcantryitmightbreaktho" + FileType.fileExtension(FileType.Save)

    /******************** Save File Constants **********************/
    val SaveFilePrefix = "save"
    val SaveFileRegex =
      s"""$SaveFilePrefix[0-9]+${FileType.fileExtension(FileType.Save)}""".r

    /******************** File Sets **********************/
    val allFilePrefixes = Set(SaveFilePrefix)
    val allFileTypes = Set(EIFGFileType, SerializedFileType, ConfigFileType)
  }

  /******************** Master File Related **********************/
  // TODO(zack): Add backup/older versions of Master file
  def MasterFilePath(title: String) = GameDirectoryPath(title) + "/" + Constants.MasterFileName
  def MasterFileLineFormat(
    path: String,
    timeCreated: Option[Long],
    lastModified: Option[Long],
    alias: Option[String]) =
    Seq(path, timeCreated.getOrElse(""), lastModified.getOrElse(""), alias.getOrElse("")).mkString(",")

  /******************** Game specific directories **********************/
  def GameDirectoryPath(title: String) =
    Constants.EIFGDirectoryPath + "/" + Utils.gameTitleKey(title)

  /******************** Save File Constants **********************/
  def MasterSaveFileLineFormat(path: String, timeCreated: Long, lastModified: Long, alias: String) =
    Seq(path, timeCreated, lastModified, alias).mkString(",")

  private def constantsForFileType(fileType: FileType.Value): (Regex, String) =
    fileType match {
      case FileType.Save =>
        (Constants.SaveFileRegex, Constants.SaveFilePrefix)

      case _ => throw new Exception("Unknown file type!")
    }

  // TODO(zack): Rewrite to pull from master file
//  def allFileEntries(gameTitle: String): Try[Seq[FileEntry]] = Try {
//    val (regex, _) = constantsForFileType(fileType)
//    val gameDir = Constants.GameDirectoryPath(gameTitle)
//    val allEntries =
//      gameDir
//        .toDirectory
//        .files
//        .map(_.name)
//        .flatMap {
//          case matchingFile@regex() => pathToFileEntry(matchingFile.trim)
//          case _ => None
//        }
//        .toSeq
//    val allValidEntries = fileType match {
//      case FileType.Save =>
//        val invalidEntries = allEntries.collect { case e if isFileEntryInMaster(e, gameTitle) => e.path }
//        println(s"FILE ERROR - Files missing from Master:\n  ${invalidEntries.mkString("\n  ")}")
//        allEntries.collect { case e if isFileEntryInMaster(e, gameTitle) => e }
//
//      case _ =>
//        val invalidEntriesPaths = allEntries.collect { case e if !e.isValid => e.path }
//        println(s"FILE ERROR - Malformed file creation time for files or Files missing from Master:\n  ${invalidEntriesPaths.mkString("\n  ")}")
//        allEntries.collect { case e if e.isValid => e }
//    }
//    allValidEntries
//  }
//
//  def nNewestFileEntries(gameTitle: String, fileType: FileType.Value, n: Int = 1): Try[Seq[FileEntry]] =
//    for {
//      allFileEntries <- allFileEntries(gameTitle, fileType)
//    } yield allFileEntries.sortBy(_.createdAtFromPath.getOrElse(Long.MinValue)).take(n)
//
//  def nOldestFileEntries(gameTitle: String, fileType: FileType.Value, n: Int = 1): Try[Seq[FileEntry]] =
//    for {
//      allFileEntries <- allFileEntries(gameTitle, fileType)
//    } yield allFileEntries.sortBy(-_.createdAtFromPath.getOrElse(Long.MaxValue)).take(n)

  // TODO(zack): Add a check for valid alias here
//  def getNewFileEntry(
//    gameTitle: String,
//    fileType: FileType.Value,
//    aliasFileName: Option[String]): Try[FileEntry] =
//    for {
//      allFileEntries <- allFileEntries(gameTitle, fileType)
//      (_, fileNamePrefix: String) = constantsForFileType(fileType)
//      gameDirPath = Constants.GameDirectoryPath(gameTitle)
//      currentTimeInMillis: Long = System.currentTimeMillis()
//      fileExtension = FileType.fileExtension(fileType) if fileExtension.isDefined
//      fileName = s"$fileNamePrefix$currentTimeInMillis${fileExtension.get}"
//      newPath = s"$gameDirPath/$fileName"
//      if !allFileEntries.map(_.path).contains(newPath)
//    } yield FileEntry(newPath, fileName, gameTitle, fileType, aliasFileName)
}