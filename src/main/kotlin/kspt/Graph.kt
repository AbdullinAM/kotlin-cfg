package kspt

import info.leadinglight.jdot.Edge
import info.leadinglight.jdot.Graph as DotGraph
import info.leadinglight.jdot.Node as DotNode
import info.leadinglight.jdot.enums.Shape

class Graph(val inputs: Set<Node>) {
    val nodes = hashSetOf<Node>()
    var outputs = hashSetOf<Node>()

    init {
        inputs.forEach {
            nodes.add(it)
            outputs.add(it)
        }
    }

    fun merge(other: Graph) {
        other.nodes.forEach { nodes.add(it) }
        outputs = other.getActiveOutputs().toHashSet()
    }

    fun addNode(n: Node) {
        nodes.add(n)
        getActiveOutputs().forEach {
            it.addSuccessor(n)
            n.addPredecessor(it)
        }
        outputs = hashSetOf(n)
    }

    fun getActiveOutputs() = outputs.filter { !it.isReturn }.toSet()


    fun view() {
        val graph = DotGraph()
        nodes.forEach {
            val node = DotNode(it.toString()).setShape(
                when (it) {
                    is BeginNode -> Shape.ellipse
                    is ActionNode -> Shape.box
                    is ConditionNode -> Shape.diamond
                }
            )
            graph.addNode(node)
        }
        nodes.forEach {
            for (succ in it.successors) {
                graph.addEdge(Edge(it.toString()).addNode(succ.toString()))
            }
        }
        graph.viewSvg()
    }
}