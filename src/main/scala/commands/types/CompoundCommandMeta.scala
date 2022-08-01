package commands.types

import commands.CommandHelpers.CommandWordDelim

trait CompoundCommandMeta extends CommandMeta {
  def primaryCommandValues: Seq[String]
  def secondaryCommandValues: Seq[String]

  def values: Seq[String] =
    for {
      pValue <- primaryCommandValues
      sValue <- secondaryCommandValues
    } yield Seq(pValue, sValue).mkString(CommandWordDelim)
}
