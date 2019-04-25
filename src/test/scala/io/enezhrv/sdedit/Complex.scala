package io.enezhrv.sdedit

class Complex extends UnitTest
{
    test("Complex diagram") {
        val input =
            """
              |objects {
              |    test ProtocolAdapterTest existing
              |    adapter ProtocolAdapter
              |    configurator ProtocolAdapterConfigurator
              |    confHelp ConfigurationHelper
              |    manager Manager
              |    protocolBuilder ProtocolBuilder
              |    protocol Protocol
              |}
              |
              |object test {
              |    new adapter;
              |
              |    call adapter init(configuration) {
              |
              |        new configurator(this, configuration) {
              |            new confHelp(this);
              |        }
              |
              |        new manager(configurator) {
              |
              |            new protocolBuilder;
              |
              |            diagramLink "Create_Metadata.sd" 1;
              |
              |            call protocolBuilder add (Metadata);
              |
              |            diagramLink "Create_Leaf.sd" 2;
              |
              |            call protocolBuilder add (Leaf);
              |
              |            loop {
              |                call protocolBuilder add (Hierarchy);
              |            }
              |
              |            call protocolBuilder add (Top);
              |            call protocolBuilder add (Timestamp);
              |
              |            call protocolBuilder build {
              |
              |                new protocol(this) {
              |                    call protocol "Copy items from builder";
              |                }
              |            }
              |        }
              |    }
              |}
            """.stripMargin

        val expected =
            """
              |test:ProtocolAdapterTest[a]
              |/adapter:ProtocolAdapter[a]
              |/configurator:ProtocolAdapterConfigurator[a]
              |/confHelp:ConfigurationHelper[a]
              |/manager:Manager[a]
              |/protocolBuilder:ProtocolBuilder[a]
              |/protocol:Protocol[a]
              |
              |test:adapter.new
              |test:adapter.init(configuration)
              |    adapter:configurator.new(this, configuration)
              |        configurator:confHelp.new(this)
              |    adapter:manager.new(configurator)
              |        manager:protocolBuilder.new
              |        *1 manager
              |            link:Create_Metadata.sd
              |        *1
              |        manager:protocolBuilder.add(Metadata)
              |        *2 manager
              |            link:Create_Leaf.sd
              |        *2
              |        manager:protocolBuilder.add(Leaf)
              |        [c:loop]
              |            manager:protocolBuilder.add(Hierarchy)
              |        [/c]
              |        manager:protocolBuilder.add(Top)
              |        manager:protocolBuilder.add(Timestamp)
              |        manager:protocolBuilder.build
              |            protocolBuilder:protocol.new(this)
              |                protocol:protocol.Copy items from builder
            """.stripLines

        parseAssert (input, expected, true)
    }
}
