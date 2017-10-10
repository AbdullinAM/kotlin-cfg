package kspt

import info.leadinglight.jdot.Edge
import info.leadinglight.jdot.Graph as DotGraph
import info.leadinglight.jdot.Node as DotNode
import info.leadinglight.jdot.enums.Shape
import kotlin.concurrent.thread

class Graph(val inputs: Set<Node>) {
    var condition: Condition? = null
    val nodes = hashSetOf<Node>()
    var outputs = hashSetOf<Node>()

    init {
        inputs.forEach {
            nodes.add(it)
            outputs.add(it)
        }
    }

    constructor(inp: Set<Node>, cond: Condition?): this(inp) {
        condition = cond
    }

    fun merge(other: Graph) {
        other.nodes.forEach { nodes.add(it) }
        outputs = other.getActiveOutputs().toHashSet()
    }

    fun addNode(n: Node) {
        nodes.add(n)
        getActiveOutputs().forEach {
            if (condition != null) {
                it.addConditionalSuccessor(n, condition!!)
                condition = null
            } else {
                it.addSuccessor(n)
            }
            n.addPredecessor(it)
        }
        outputs = hashSetOf(n)
    }

    fun getActiveOutputs() = outputs.filter { !it.isReturn }.toSet()

    fun removeTerminateNodes() {
        val removableNodes = mutableListOf<Node>()
        nodes.forEach {
            if (it is TerminateNode) {
                it.selfDelete()
                removableNodes.add(it)
            }
        }
        removableNodes.forEach { nodes.remove(it) }
    }

    fun view() {
        val graph = DotGraph()
        nodes.forEach {
            val node = DotNode(it.toString()).setShape(
                when (it) {
                    is BeginNode -> Shape.ellipse
                    is ActionNode -> Shape.box
                    is ConditionNode -> Shape.diamond
                    is TerminateNode -> Shape.egg   /// this should not happen in normal workflow
                }
            )
            graph.addNode(node)
        }
        nodes.forEach {
            for (succ in it.successors) {
                graph.addEdge(Edge(it.toString()).addNode(succ.key.toString()).setColor(
                        when (succ.value) {
                            Condition.NONE -> "#000000"
                            Condition.TRUE -> "#00FF00"
                            Condition.FALSE -> "#FF0000"
                        }
                ))
            }
        }
        thread {
            graph.viewSvg()
        }
    }
}