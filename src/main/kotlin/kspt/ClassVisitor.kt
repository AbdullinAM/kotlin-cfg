package kspt

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.UnaryExpr
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.ForStmt
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.ReturnStmt
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import java.io.File

class ClassVisitor: VoidVisitorAdapter<String>() {

    class MethodVisitor : VoidVisitorAdapter<Graph>() {
        override fun visit(n: AssignExpr, graph: Graph) {
            val node = Node(n.toString())
            graph.addNode(node)
        }

        override fun visit(n: BinaryExpr, graph: Graph) {
            val node = Node(n.toString())
            graph.addNode(node)
        }

        override fun visit(n: BlockStmt, graph: Graph) {
            val blockGraph = Graph(graph.outputs.filter { !it.isReturn }.toSet())
            super.visit(n, blockGraph)
            graph.merge(blockGraph)
        }

        override fun visit(n: ReturnStmt, graph: Graph) {
            val node = Node(n.toString())
            node.setReturn()
            graph.addNode(node)
        }

        override fun visit(n: UnaryExpr, graph: Graph) {
            val node = Node(n.toString())
            node.setReturn()
            graph.addNode(node)
        }

        override fun visit(n: VariableDeclarator, graph: Graph) {
            val node = Node(n.toString())
            graph.addNode(node)
        }

        override fun visit(n: IfStmt, graph: Graph) {
            val condNode = Node(n.condition.toString())

            graph.addNode(condNode)
            val thenGraph = Graph(graph.outputs)
            n.thenStmt.accept(this, thenGraph)

            val elseGraph = Graph(graph.outputs)
            n.elseStmt.ifPresent {
                it.accept(this, thenGraph)
            }

            graph.merge(thenGraph)
            if (n.elseStmt.isPresent) graph.merge(elseGraph)
            else graph.outputs.add(condNode)
        }

        override fun visit(n: ForStmt, graph: Graph) {
            val initGraph = Graph(graph.outputs)
            n.initialization.accept(this, initGraph)
            graph.merge(initGraph)

            n.compare.ifPresent {
                graph.addNode(Node(it.toString()))
            }

            val bodyGraph = Graph(graph.outputs)
            n.body.accept(this, bodyGraph)
            graph.merge(bodyGraph)

            val updGraph = Graph(graph.outputs)
            n.update.accept(this, updGraph)
            graph.merge(updGraph)

            graph.outputs.forEach {
                bodyGraph.inputs.forEach { ita ->
                    it.addSuccessor(ita)
                    ita.addPredecessor(it)
                }
            }

            graph.outputs = bodyGraph.inputs.toHashSet()
        }
    }

    override fun visit(n: MethodDeclaration, v: String) {
        var sign = "${n.getType().asString()} ${n.getName()} ("
        n.parameters.forEach { sign += "$it, "}
        sign += ")"
        val begin = Node(sign)
        val cfg = Graph(hashSetOf(begin))
        n.accept(MethodVisitor(), cfg)
        val graph = printToDot(cfg, n.nameAsString)
        File("${n.getName()}.dot").printWriter().use { it.println(graph.toDot()) }
    }
}