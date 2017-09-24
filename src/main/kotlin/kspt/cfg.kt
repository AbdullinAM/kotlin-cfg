package kspt

import com.github.javaparser.JavaParser
import java.io.FileInputStream
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("No input file specified")
        exitProcess(-1)
    }

    val `in` = FileInputStream(args[0])

    // parse the file
    val cu = JavaParser.parse(`in`)

    // prints the resulting compilation unit to default system output
    cu.accept(AstVisitor(), "")
}

