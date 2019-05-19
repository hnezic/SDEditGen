package object sdeditgen
{
    type Arguments = Option[List[Argument]]

    val lineSep: String = System.lineSeparator

    implicit class ExtendedString (input: String)
    {
        def stripLines : String =
        {
            val lines = input.stripMargin.split(lineSep)
            lines.drop(1).dropRight(1).mkString(lineSep)
        }

        def rtrimLines : String =
        {
            val lines = input.stripMargin.split(lineSep)
            lines.map(_.rtrim).mkString(lineSep)
        }

        def rtrim : String =
        {
            input.replaceAll("""\s+$""", "")
        }
    }
}
