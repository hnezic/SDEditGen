package io.enezhrv.sdedit

class SimpleStd extends UnitTest
{
    test("Simple diagram (simple new and call statements)") {
        val input =
            """
              | objects {
              |     test: AdapterTest named existing
              |     adapter: Adapter
              |     manager: Manager
              |     context: Context
              | }
              |
              | object test {
              |     new adapter
              |
              |     call adapter.init {
              |         new manager {
              |             new context
              |         }
              |     }
              |
              |     call manager.manage(arg1, arg2) {
              |         loop {
              |             call context.getItem
              |         }
              |     }
              | }
            """.stripMargin

        val expected =
            """
              |test:AdapterTest
              |/adapter:Adapter[a]
              |/manager:Manager[a]
              |/context:Context[a]
              |
              |test:adapter.new
              |test:adapter.init
              |    adapter:manager.new
              |        manager:context.new
              |test:manager.manage(arg1, arg2)
              |    [c:loop]
              |        manager:context.getItem
              |    [/c]
            """.stripLines

        parseCompareResult (input, expected)
    }
}
