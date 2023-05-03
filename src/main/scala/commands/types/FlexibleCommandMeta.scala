package commands.types

import commands.CommandHelpers.CommandWordDelim

trait FlexibleCommandMeta extends CommandMeta {
  def singleValues: Seq[String] = Seq.empty
  def primaryCommandValues: Seq[String] = Seq.empty
  def secondaryCommandValues: Seq[String] = Seq.empty

  def values: Seq[String] = {
    val compoundValues =
      for {
        pValue <- primaryCommandValues
        sValue <- secondaryCommandValues
      } yield Seq(pValue, sValue).mkString(CommandWordDelim)
    singleValues ++ compoundValues
  }
}
