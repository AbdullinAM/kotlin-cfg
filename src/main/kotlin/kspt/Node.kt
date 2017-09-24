package kspt

import com.github.javaparser.ast.Node as AstNode

sealed class Node(val content: AstNode) {
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

    override fun toString() = content.toString()

    override fun hashCode(): Int {
        return content.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Node) return false
        return content.equals(other.content)
    }
}

class ActionNode(init: AstNode): Node(init)
class BeginNode(init: AstNode): Node(init)
class ConditionNode(init: AstNode): Node(init)
