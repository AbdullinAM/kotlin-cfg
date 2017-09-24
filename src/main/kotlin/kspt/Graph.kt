package kspt

import info.leadinglight.jdot.Edge
import info.leadinglight.jdot.Graph as DotGraph
import info.leadinglight.jdot.enums.Shape

class Node(val name: String) {
    val successors: MutableList<Node> = mutableListOf()
    val predecessors: MutableList<Node> = mutableListOf()
    var isReturn = false

    fun addPredecessor(pred: Node) {
        predecessors.add(pred)
    }

    fun addSuccessor(succ: Node) {
        successors.add(succ)
    }

    fun setReturn() {
        isReturn = true
    }

    override fun toString() = name

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Node) return false
        return name.equals(other.name)
    }
}

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
        outputs = other.outputs
    }

    fun addNode(n: Node) {
        nodes.add(n)
        outputs.forEach {
            if (!it.isReturn) {
                it.addSuccessor(n)
                n.addPredecessor(it)
            }
        }
        outputs = hashSetOf(n)
    }

}

fun printToDot(graph: Graph, name: String) : DotGraph {
    val g = DotGraph(name)
    val deque = Deque<Node>()
    graph.inputs.forEach { deque.pushBack(it) }
    val visited = hashSetOf<Node>();
    while (deque.isNotEmpty()) {
        val current = deque.getFront()
        if (current == null) throw NullPointerException()
        println(current)

        g.addNode(info.leadinglight.jdot.Node(current.toString()).setShape(Shape.box))
        current.successors.forEach { it -> if (!visited.contains(it)) deque.pushBack(it) }
        visited.add(current)
    }
    deque.clear()
    visited.clear()
    graph.inputs.forEach { deque.pushBack(it) }
    while (deque.isNotEmpty()) {
        val current = deque.getFront()
        if (current == null) throw NullPointerException()

        for (succ in current.successors) {
            g.addEdge(Edge().addNode(current.toString()).addNode(succ.toString()))
            if (!visited.contains(succ)) deque.pushBack(succ)
            visited.add(current)
        }
    }

    return g
}