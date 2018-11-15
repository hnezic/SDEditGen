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
          | objects {
          |     test AdapterTest named existing
          |     adapter Adapter
          |     manager Manager
          |     context Context
          | }
          |
          | code test {
          |     create adapter
          |
          |     call adapter init
          |         method adapter init {
          |             create manager
          |                 constructor manager {
          |                     create context
          |                 }
          |         }
          |
          |     call manager manage(arg1, arg2)
          |         method manager manage {
          |             loop {
          |                 call context getItem
          |             }
          |         }
          | }
        """.stripMargin

    val parseResult = parse(input, program(_))

    parseResult match
    {
        case Parsed.Success(value, _) => println(value)
        case failure: Parsed.Failure => println(failure)
    }
}
