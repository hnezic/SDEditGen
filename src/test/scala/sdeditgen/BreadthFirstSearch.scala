package sdeditgen

class BreadthFirstSearch extends UnitTest
{
    test("Breadth-first search") {
        val input =
            """
              |objects {
              |    bfs: BFS | existing
              |    queue: FIFO | named
              |    someNode: Node | named existing
              |    node: Node | named existing
              |    adjList: List | named existing
              |    adj: Node | named existing
              |}
              |
              |object bfs {
              |    new queue
              |    call someNode.setLevel(0)
              |    call queue.insert(someNode)
              |    loop "while queue != ()" {
              |        call queue.remove() return node
              |        call node.getLevel() return level
              |        call node.getAdjacentNodes() return adjList
              |        loop "0 <= i < #adjList" {
              |            call adjList.get(i) return adj
              |            call adj.getLevel() return nodeLevel
              |            alt "nodeLevel IS NOT defined" {
              |                call adj.setLevel(`level+1`)
              |                call queue.insert(adj)
              |            section "else"
              |            }
              |        }
              |    }
              |    call queue.destroy()
              |}
            """.stripMargin

        val expected=
            """
              |bfs:BFS[a]
              |/queue:FIFO
              |someNode:Node
              |node:Node
              |adjList:List
              |adj:Node
              |
              |bfs:queue.new
              |bfs:someNode.setLevel(0)
              |bfs:queue.insert(someNode)
              |[c:loop while queue != ()]
              |    bfs:node=queue.remove()
              |    bfs:level=node.getLevel()
              |    bfs:adjList=node.getAdjacentNodes()
              |    [c:loop 0 <= i < #adjList]
              |        bfs:adj=adjList.get(i)
              |        bfs:nodeLevel=adj.getLevel()
              |        [c:alt nodeLevel IS NOT defined]
              |            bfs:adj.setLevel(level+1)
              |            bfs:queue.insert(adj)
              |      --[else]
              |        [/c]
              |    [/c]
              |[/c]
              |bfs:queue.destroy()""".stripMargin

        parseCheckSuccess (input)
    }
}
