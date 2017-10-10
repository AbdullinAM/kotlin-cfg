package kspt

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import jdk.nashorn.internal.ir.FunctionCall

class AstVisitor : VoidVisitorAdapter<String>() {

    class MethodVisitor : VoidVisitorAdapter<Graph>() {
        val continues = mutableListOf<MutableList<Node>>()
        val breaks = mutableListOf<MutableList<Node>>()

        override fun visit(n: AssignExpr, graph: Graph) {
            val node = ActionNode(n)
            graph.addNode(node)
        }

        override fun visit(n: BinaryExpr, graph: Graph) {
            val node = ActionNode(n)
            graph.addNode(node)
        }

        override fun visit(n: BlockStmt, graph: Graph) {
            val blockGraph = Graph(graph.getActiveOutputs(), graph.condition)
            super.visit(n, blockGraph)
            graph.merge(blockGraph)
        }

        override fun visit(n: BreakStmt, graph: Graph) {
            if (!n.label.isPresent) {
                val node = TerminateNode(n.clone().setLabel(SimpleName(graph.toString())))
                graph.nodes.add(node)
                graph.getActiveOutputs().forEach {
                    if (graph.condition != null) {
                        it.addConditionalSuccessor(node, graph.condition!!)
                        graph.condition = null
                    } else {
                        it.addSuccessor(node)
                    }
                    node.addPredecessor(it)
                }
                breaks.last().add(node)
                graph.outputs = hashSetOf()
            } else {
                println("Labeled continues and breaks are not supported!!")
            }
        }

        override fun visit(n: ContinueStmt, graph: Graph) {
            if (!n.label.isPresent) {
                val node = TerminateNode(n.clone().setLabel(SimpleName(graph.toString())))
                node.setReturn()
                graph.nodes.add(node)
                graph.getActiveOutputs().forEach {
                    if (graph.condition != null) {
                        it.addConditionalSuccessor(node, graph.condition!!)
                        graph.condition = null
                    } else {
                        it.addSuccessor(node)
                    }
                    node.addPredecessor(it)
                }
                graph.outputs.add(node)
                continues.last().add(node)
            } else {
                println("Labeled continues and breaks are not supported!!")
            }
        }

        override fun visit(n: MethodCallExpr, graph: Graph) {
            val node = ActionNode(n)
            graph.addNode(node)
        }

        override fun visit(n: ReturnStmt, graph: Graph) {
            val node = ActionNode(n)
            node.setReturn()
            graph.addNode(node)
        }

        override fun visit(n: UnaryExpr, graph: Graph) {
            val node = ActionNode(n)
            graph.addNode(node)
        }

        override fun visit(n: VariableDeclarator, graph: Graph) {
            val node = ActionNode(n)
            graph.addNode(node)
        }

        override fun visit(n: IfStmt, graph: Graph) {
            val condNode = ConditionNode(n.condition)

            graph.addNode(condNode)
            val thenGraph = Graph(graph.getActiveOutputs(), Condition.TRUE)
            val elseGraph = Graph(graph.getActiveOutputs(), Condition.FALSE)

            n.thenStmt.accept(this, thenGraph)
            n.elseStmt.ifPresent {
                it.accept(this, elseGraph)
            }

            graph.merge(thenGraph)
            elseGraph.nodes.forEach { graph.nodes.add(it) }
            elseGraph.getActiveOutputs().forEach { graph.outputs.add(it) }
        }

        override fun visit(n: ForStmt, graph: Graph) {
            continues.add(mutableListOf())
            breaks.add(mutableListOf())
            val initGraph = Graph(graph.getActiveOutputs())
            n.initialization.accept(this, initGraph)
            graph.merge(initGraph)

            n.compare.ifPresent {
                graph.addNode(ConditionNode(it))
            }

            val bodyGraph = Graph(graph.getActiveOutputs(), Condition.TRUE)
            n.body.accept(this, bodyGraph)
            graph.merge(bodyGraph)

            val updGraph = Graph(graph.getActiveOutputs())
            n.update.accept(this, updGraph)
            graph.merge(updGraph)

            graph.getActiveOutputs().forEach {
                bodyGraph.inputs.forEach { ita ->
                    it.addSuccessor(ita)
                    ita.addPredecessor(it)
                }
            }

            graph.outputs = bodyGraph.inputs.toHashSet()

            continues.last().forEach {
                updGraph.outputs.forEach { itu ->
                    itu.addPredecessor(it)
                    it.addSuccessor(itu)
                }
            }
            continues.removeAt(continues.size - 1)
            breaks.last().forEach {
                graph.outputs.add(it)
            }
            breaks.removeAt(breaks.size - 1)
        }

        override fun visit(n: WhileStmt, graph: Graph) {
            continues.add(mutableListOf())
            breaks.add(mutableListOf())
            val condNode = ConditionNode(n.condition)
            graph.addNode(condNode)

            val bodyGraph = Graph(graph.getActiveOutputs(), Condition.TRUE)
            n.body.accept(this, bodyGraph)
            graph.merge(bodyGraph)
            graph.getActiveOutputs().forEach {
                bodyGraph.inputs.forEach { ita ->
                    it.addSuccessor(ita)
                    ita.addPredecessor(it)
                }
            }
            graph.outputs = bodyGraph.inputs.toHashSet()

            continues.last().forEach {
                condNode.addPredecessor(it)
                it.addSuccessor(condNode)
            }
            continues.removeAt(continues.size - 1)
            breaks.last().forEach {
                graph.outputs.add(it)
            }
            breaks.removeAt(breaks.size - 1)
        }
    }

    override fun visit(n: MethodDeclaration, v: String) {
        val begin = BeginNode(n.name)
        val cfg = Graph(hashSetOf(begin))
        n.accept(MethodVisitor(), cfg)
        cfg.removeTerminateNodes()
        cfg.view()
    }
}