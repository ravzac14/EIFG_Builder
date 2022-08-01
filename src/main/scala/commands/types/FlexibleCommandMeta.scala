package commands.types

import commands.CommandHelpers.CommandWordDelim

trait FlexibleCommandMeta extends CommandMeta {
  def singleValues: Seq[String]
  def primaryCommandValues: Seq[String]
  def secondaryCommandValues: Seq[String]

  def values: Seq[String] = {
    val compoundValues =
      for {
        pValue <- primaryCommandValues
        sValue <- secondaryCommandValues
      } yield Seq(pValue, sValue).mkString(CommandWordDelim)
    singleValues ++ compoundValues
  }
}
