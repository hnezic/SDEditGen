package io.enezhrv.sdedit

class WithoutStatements extends UnitTest
{
    test("Statements are not required") {
        val input =
            """
              | objects {
              |     test AdapterTest | named existing
              |     adapter Adapter
              |     manager:Manager
              |     context: Context
              | }
            """.stripMargin

        val expected =
            """
              |test:AdapterTest
              |/adapter:Adapter[a]
              |/manager:Manager[a]
              |/context:Context[a]
              |
            """.stripLines

        parseCompareResult (input, expected)
    }
}
