package kspt

import com.github.javaparser.ast.Node as AstNode

enum class Condition {
    NONE,
    TRUE,
    FALSE
}

sealed class Node(val content: AstNode) {
    val successors: HashMap<Node, Condition> = hashMapOf()
    val predecessors: MutableList<Node> = mutableListOf()
    var isReturn = false

    fun addPredecessor(pred: Node) {
        predecessors.add(pred)
    }

    fun addSuccessor(succ: Node) {
        successors[succ] = Condition.NONE
    }

    fun addConditionalSuccessor(succ: Node, c: Condition) {
        successors[succ] = c
    }

    fun setReturn() {
        isReturn = true
    }

    fun removeSuccessor(succ: Node) {
        successors.remove(succ)
    }

    fun removePredecessor(pred: Node) {
        predecessors.remove(pred)
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
class TerminateNode(init: AstNode): Node(init) {

    fun selfDelete() {
        successors.forEach {
            it.key.removePredecessor(this)
        }
        predecessors.forEach {
            val oldCond = it.successors[this]
            it.removeSuccessor(this)
            successors.forEach { its ->
                its.key.addPredecessor(it)
                it.addConditionalSuccessor(its.key, oldCond!!)
            }
        }
    }
}
