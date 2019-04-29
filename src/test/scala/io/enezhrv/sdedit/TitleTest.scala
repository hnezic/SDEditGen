package io.enezhrv.sdedit

class TitleTest extends UnitTest
{
    test("Optional title before object declarations") {
        val input =
            """
              | title "Skills421 - BookingService"
              | objects {
              |     test AdapterTest | named existing
              |     adapter Adapter
              |     manager Manager
              |     context Context
              | }
            """.stripMargin

        val expected =
            """
              |#![Skills421 - BookingService]
              |test:AdapterTest
              |/adapter:Adapter[a]
              |/manager:Manager[a]
              |/context:Context[a]
              |
            """.stripLines

        parseCompareResult (input, expected)
    }
}
