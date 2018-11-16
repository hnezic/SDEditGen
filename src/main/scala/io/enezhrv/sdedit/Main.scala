package io.enezhrv.sdedit

import ammonite.ops.{Path, read, write}
import fastparse._

object Main extends SDEditGenParser with PathUtils
{
    def main(args: Array[String]): Unit =
    {
        val input1 =
            """
              | objects {
              | abc List named
              | xyz Adapter
              | whatever Test existing named
              |}
              |diagramLink "this is my link"  123
              |diagramLink "another link" abc 4567 ;""".stripMargin

        val input2 =
            """diagramLink "this is my link"  123
              |diagramLink "another link" abc 4567 ;
            """.stripMargin

        val input3 =
            """
              |title "Skills421 - BookingService"
              |
              |objects {
              |	delegate Actor existing
              |	bs BookingService existing named
              |	b Booking existing named
              |	sc ScheduleCourse existing named
              |}
              |
              |code delegate {
              |	call bs bookScheduledCourse (schedId,numPlaces) {
              |		call b createBooking() {
              |			call sc "[checkAvailability(schedId,numPlaces)='true']"
              |			call sc allocatePlaces(schdedId,numPlaces)
              |		}
              |		call delegate "SUCCESS/FAILURE"
              |	}
              |}
            """.stripMargin

        // Get arguments
        if (args.length != 2)
            throw new IllegalArgumentException ("Usage: <input path> <relative output folder>")

        val (sourcePath, targetPath) = getPaths (args(0), args(1), "sdgen", "sd")
        val input = read! sourcePath

        val parseResult = parse(input, program(_))

        parseResult match
        {
            case Parsed.Success(ast, _) =>
                val output = ast.generate
                write.over (targetPath, output)

            case failure: Parsed.Failure => println(failure)
        }
    }
}
