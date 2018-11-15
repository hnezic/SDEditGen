package io.enezhrv.sdedit

import fastparse._

object Main extends App with SDEditGenParser
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

    val input =
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

    val parseResult = parse(input, program(_))

    parseResult match
    {
        case Parsed.Success(value, _) => println(value)
        case failure: Parsed.Failure => println(failure)
    }
}
