package base

object AsciiHelpers {

  private val EmptyChar = Seq("","","","","","","","","","")
  private def isAsciiCharEmpty(ac: Seq[String]): Boolean = ac.forall(_.isEmpty)
  private def isAsciiCharSpace(ac: Seq[String]): Boolean = !isAsciiCharEmpty(ac) && ac.forall(_.trim.isEmpty)

  def buildAsciiString(title: String): String = buildAsciiCharArray(title).mkString("\n")

  private def buildAsciiCharArray(title: String): Seq[String] = {
    val chars = title.toCharArray
    val asciiChars = chars.map(toAsciiChar)
    asciiChars.foldRight(EmptyChar) { (rows, acc) =>
      require(rows.length == acc.length)
      Seq(
        rows(0) ++ " " ++ acc(0),
        rows(1) ++ " " ++ acc(1),
        rows(2) ++ " " ++ acc(2),
        rows(3) ++ " " ++ acc(3),
        rows(4) ++ " " ++ acc(4),
        rows(5) ++ " " ++ acc(5),
        rows(6) ++ " " ++ acc(6),
        rows(7) ++ " " ++ acc(7),
        rows(8) ++ " " ++ acc(8),
        rows(9) ++ " " ++ acc(9)
      )
    }
  }

  private def toAsciiChar(c: Char): Seq[String] = c match {
    case 'a' =>
      Seq("      ",
          "      ",
          "      ",
          "      ",
          ".oOoO'",
          "O   o ",
          "o   O ",
          "`OoO'o",
          "      ",
          "      ")
    case 'A' =>
      Seq("   Oo   ",
          "  o  O  ",
          " O    o ",
          "oOooOoOo",
          "o      O",
          "O      o",
          "o      O",
          "O.     O",
          "        ",
          "        ")
    case 'b' =>
      Seq("o    ",
          "O    ",
          "O    ",
          "o    ",
          "OoOo.",
          "O   o",
          "o   O",
          "`OoO'",
          "     ",
          "     ")
    case 'B' =>
      Seq("o.oOOOo. ",
          " o     o ",
          " O     O ",
          " oOooOO. ",
          " o     `O",
          " O      o",
          " o     .O",
          " `OooOO' ",
          "         ",
          "         ")
    case 'c' =>
      Seq("     ",
          "     ",
          "     ",
          "     ",
          ".oOo ",
          "O    ",
          "o    ",
          "`OoO'",
          "     ",
          "     ")
    case 'C' =>
      Seq(" .oOOOo. ",
          ".O     o ",
          "o        ",
          "o        ",
          "o        ",
          "O        ",
          "`o     .o",
          " `OoooO' ",
          "         ",
          "         ")
    case 'd' =>
      Seq("     o",
          "    O ",
          "    o ",
          "    o ",
          ".oOoO ",
          "o   O ",
          "O   o ",
          "`OoO'o",
          "      ",
          "      ")
    case 'D' =>
      Seq("o.OOOo.  ",
          " O    `o ",
          " o      O",
          " O      o",
          " o      O",
          " O      o",
          " o    .O'",
          " OooOO'  ",
          "         ",
          "         ")
    case 'e' =>
      Seq("     ",
          "     ",
          "     ",
          "     ",
          ".oOo.",
          "OooO'",
          "O    ",
          "`OoO'",
          "     ",
          "     ")
    case 'E' =>
      Seq("o.OOoOoo",
          " O      ",
          " o      ",
          " ooOO   ",
          " O      ",
          " o      ",
          " O      ",
          "ooOooOoO",
          "        ",
          "        ")
    case 'f' =>
      Seq(".oOo",
          "O   ",
          "o   ",
          "OoO ",
          "o   ",
          "O   ",
          "o   ",
          "O'  ",
          "    ",
          "    ")
    case 'F' =>
      Seq("OOooOoO",
          "o      ",
          "O      ",
          "oOooO  ",
          "O      ",
          "o      ",
          "o      ",
          "O'     ",
          "       ",
          "       ")
    case 'g' =>
      Seq("     ",
          "     ",
          "     ",
          "     ",
          ".oOoO",
          "o   O",
          "O   o",
          "`OoOo",
          "    O",
          " OoO'")
    case 'G' =>
      Seq(" .oOOOo. ",
          ".O     o ",
          "o        ",
          "O        ",
          "O   .oOOo",
          "o.      O",
          " O.    oO",
          "  `OooO' ",
          "         ",
          "         ")
    case 'h' =>
      Seq(" o   ",
          "O    ",
          "o    ",
          "O    ",
          "OoOo.",
          "o   o",
          "o   O",
          "O   o",
          "     ",
          "     ")
    case 'H' =>
      Seq("o      O",
          "O      o",
          "o      O",
          "OoOooOOo",
          "o      O",
          "O      o",
          "o      o",
          "o      O",
          "        ",
          "        ")
    case 'i' =>
      Seq("  ",
          "o ",
          "  ",
          "  ",
          "O ",
          "o ",
          "O ",
          "o'",
          "  ",
          "  ")
    case 'I' =>
      Seq("ooOoOOo",
          "   O   ",
          "   o   ",
          "   O   ",
          "   o   ",
          "   O   ",
          "   O   ",
          "ooOOoOo",
          "       ",
          "       ")
    case 'j' =>
      Seq("   ",
          "  O",
          "   ",
          "   ",
          " 'o",
          "  O",
          "  o",
          "  O",
          "  o",
          "oO'")
    case 'J' =>
      Seq("  OooOoo",
          "      O ",
          "      o ",
          "      O ",
          "      o ",
          "      O ",
          "O     o ",
          "`OooOO' ",
          "        ",
          "        ")
    case 'k' =>
      Seq("o    ",
          "O    ",
          "o    ",
          "o    ",
          "O  o ",
          "OoO  ",
          "o  O ",
          "O   o",
          "     ",
          "     ")
    case 'K' =>
      Seq("`o    O ",
          " o   O  ",
          " O  O   ",
          " oOo    ",
          " o  o   ",
          " O   O  ",
          " o    o ",
          " O     O",
          "        ",
          "        ")
    case 'l' =>
      Seq(" o",
          "O ",
          "o ",
          "O ",
          "o ",
          "O ",
          "o ",
          "Oo",
          "  ",
          "  ")
    case 'L' =>
      Seq(" o     ",
          "O      ",
          "o      ",
          "o      ",
          "O      ",
          "O      ",
          "o     .",
          "OOoOooO",
          "       ",
          "       ")
    case 'm' =>
      Seq("        ",
          "        ",
          "        ",
          "        ",
          "`oOOoOO.",
          " O  o  o",
          " o  O  O",
          " O  o  o",
          "        ",
          "        ")
    case 'M' =>
      Seq("Oo      oO",
          "O O    o o",
          "o  o  O  O",
          "O   Oo   O",
          "O        o",
          "o        O",
          "o        O",
          "O        o",
          "          ",
          "          ")
    case 'n' =>
      Seq("      ",
          "      ",
          "      ",
          "      ",
          "'OoOo.",
          " o   O",
          " O   o",
          " o   O",
          "      ",
          "      ")
    case 'N' =>
      Seq("o.     O",
          "Oo     o",
          "O O    O",
          "O  o   o",
          "O   o  O",
          "o    O O",
          "o     Oo",
          "O     `o",
          "        ",
          "        ")
    case 'o' =>
      Seq("     ",
          "     ",
          "     ",
          "     ",
          ".oOo.",
          "O   o",
          "o   O",
          "`OoO'",
          "     ",
          "     ")
    case 'O' =>
      Seq(" .oOOOo. ",
          ".O     o.",
          "O       o",
          "o       O",
          "O       o",
          "o       O",
          "`o     O'",
          " `OoooO' ",
          "         ",
          "         ")
    case 'p' =>
      Seq("     ",
          "     ",
          "     ",
          "     ",
          ".oOo.",
          "O   o",
          "o   O",
          "oOoO'",
          "O    ",
          "o'   ")
    case 'P' =>
      Seq("OooOOo. ",
          "O     `O",
          "o      O",
          "O     .o",
          "oOooOO' ",
          "o       ",
          "O       ",
          "o'      ",
          "        ",
          "        ")
    case 'q' =>
      Seq("      ",
          "      ",
          "      ",
          "      ",
          ".oOoO'",
          "O   o ",
          "o   O ",
          "`OoOo ",
          "    O ",
          "    `o")
    case 'Q' =>
      Seq(" .oOOOo.  ",
          ".O     o. ",
          "o       O ",
          "O       o ",
          "o       O ",
          "O    Oo o ",
          "`o     O' ",
          " `OoooO Oo",
          "          ",
          "          ")
    case 'r' =>
      Seq("      ",
          "      ",
          "      ",
          "      ",
          "`OoOo.",
          " o    ",
          " O    ",
          " o    ",
          "      ",
          "      ")
    case 'R' =>
      Seq("`OooOOo. ",
          " o     `o",
          " O      O",
          " o     .O",
          " OOooOO' ",
          " o    o  ",
          " O     O ",
          " O      o",
          "         ",
          "         ")
    case 's' =>
      Seq("     ",
          "     ",
          "     ",
          "     ",
          ".oOo ",
          "`Ooo.",
          "    O",
          "`OoO'",
          "     ",
          "     ")
    case 'S' =>
      Seq(".oOOOo. ",
          "o     o ",
          "O.      ",
          " `OOoo. ",
          "      `O",
          "       o",
          "O.    .O",
          " `oooO' ",
          "        ",
          "        ")
    case 't' =>
      Seq("    ",
          "    ",
          " O  ",
          "oOo ",
          " o  ",
          " O  ",
          " o  ",
          " `oO",
          "    ",
          "    ")
    case 'T' =>
      Seq("oOoOOoOOo",
          "    o    ",
          "    o    ",
          "    O    ",
          "    o    ",
          "    O    ",
          "    O    ",
          "    o'   ",
          "         ",
          "         ")
    case 'u' =>
      Seq("      ",
          "      ",
          "      ",
          "      ",
          "O   o ",
          "o   O ",
          "O   o ",
          "`OoO'o",
          "      ",
          "      ")
    case 'U' =>
      Seq("O       o",
          "o       O",
          "O       o",
          "o       o",
          "o       O",
          "O       O",
          "`o     Oo",
          " `OoooO'O",
          "         ",
          "         ")
    case 'v' =>
      Seq("      ",
          "      ",
          "      ",
          "      ",
          "`o   O",
          " O   o",
          " o  O ",
          " `o'  ",
          "      ",
          "      ")
    case 'V' =>
      Seq("o      'O",
          "O       o",
          "o       O",
          "o       o",
          "O      O'",
          "`o    o  ",
          " `o  O   ",
          "  `o'    ",
          "         ",
          "         ")
    case 'w' =>
      Seq("        ",
          "        ",
          "        ",
          "        ",
          "'o     O",
          " O  o  o",
          " o  O  O",
          " `Oo'oO'",
          "        ",
          "        ")
    case 'W' =>
      Seq("o          `O",
          "O           o",
          "o           O",
          "O           O",
          "o     o     o",
          "O     O     O",
          "`o   O o   O'",
          " `OoO' `OoO' ",
          "             ",
          "             ")
    case 'x' =>
      Seq("     ",
          "     ",
          "     ",
          "     ",
          "o   O",
          " OoO ",
          " o o ",
          "O   O",
          "     ",
          "     ")
    case 'X' =>
      Seq("o      O",
          " O    o ",
          "  o  O  ",
          "   oO   ",
          "   Oo   ",
          "  o  o  ",
          " O    O ",
          "O      o",
          "        ",
          "        ")
    case 'y' =>
      Seq("     ",
          "     ",
          "     ",
          "     ",
          "O   o",
          "o   O",
          "O   o",
          "`OoOO",
          "    o",
          " OoO'")
    case 'Y' =>
      Seq("o       O",
          "O       o",
          "`o     O'",
          "  O   o  ",
          "   `O'   ",
          "    o    ",
          "    O    ",
          "    O    ",
          "         ",
          "         ")
    case 'z' =>
      Seq("    ",
          "    ",
          "    ",
          "    ",
          "ooOO",
          "  o ",
          " O  ",
          "OooO",
          "    ",
          "    ")
    case 'Z' =>
      Seq("OoooOOoO",
          "      o ",
          "     O  ",
          "    o   ",
          "   O    ",
          "  o     ",
          " O      ",
          "OOooOooO",
          "        ",
          "        ")
    case ' ' =>
      Seq("     ",
          "     ",
          "     ",
          "     ",
          "     ",
          "     ",
          "     ",
          "     ",
          "     ",
          "     ")
    case _ => EmptyChar
  }
}