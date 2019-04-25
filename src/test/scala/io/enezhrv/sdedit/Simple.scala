package io.enezhrv.sdedit

class Simple extends UnitTest
{
    test("Simple diagram (simple create and call statements)") {
        val input =
            """
              | objects {
              |     test AdapterTest named existing
              |     adapter Adapter
              |     manager Manager
              |     context Context
              | }
              |
              | object test {
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

        parseAssert (input, expected)
    }
}
