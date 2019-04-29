package io.enezhrv.sdedit

class SimpleMin extends UnitTest
{
    def input(initialEntity: String) =
        s"""
          | objects {
          |     test: AdapterTest | named existing
          |     adapter: Adapter
          |     manager: Manager
          |     context: Context
          | }
          |
          | $initialEntity {
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

    test("Simple diagram (object test)")
    {
        parseCompareResult (input("object test"), expected)
    }

    test("Simple diagram (method test testMethod)")
    {
        parseCompareResult (input("method test testMethod"), expected)
    }

    test("Simple diagram (constructor test)")
    {
        parseCompareResult (input("constructor test"), expected)
    }
}
