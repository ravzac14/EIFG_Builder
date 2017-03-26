package system.serialize

import base.data_structures.Meta
import base.types.FileEntryId
import system.serialize.PathHelpers.Constants

import scala.io.Source

case class FileEntry(
  meta: Meta[FileEntryId],
  path: String,
  fileName: String,
  gameTitle: String,
  fileType: FileType.Value,
  alias: Option[String] = None) {
  def isValid = FileEntry.isFileEntryInMaster(this)

  def toMasterFileLine =
    PathHelpers.MasterFileLineFormat(
      path = path,
      timeCreated = Some(meta.timeCreated),
      lastModified = Some(meta.lastModified),
      alias = alias)
}

object FileType extends Enumeration {
  val Save = Value(Constants.SaveFilePrefix)

  val prefixToFileTypeMap = this.values.map { ft =>
    ft.toString -> ft
  }.toMap

  def fileExtension(fileType: FileType.Value) = fileType match {
    case Save => Some(Constants.EIFGFileType)
    case _ => None
  }
}

object FileEntry {
  def isFileEntryInMaster(fileEntry: FileEntry): Boolean =
    Source
      .fromFile(PathHelpers.MasterFilePath(fileEntry.gameTitle))
      .getLines()
      .contains(fileEntry.toMasterFileLine)
}

