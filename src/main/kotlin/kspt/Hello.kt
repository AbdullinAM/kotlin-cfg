package kspt

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.MethodDeclaration
import java.io.FileInputStream
import kotlin.system.exitProcess
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import info.leadinglight.jdot.Edge
import info.leadinglight.jdot.Graph
import info.leadinglight.jdot.enums.Shape

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("No input file specified")
        exitProcess(-1)
    }

    val `in` = FileInputStream(args[0])

    // parse the file
    val cu = JavaParser.parse(`in`)

    // prints the resulting compilation unit to default system output
    cu.accept(ClassVisitor(), "")
}

