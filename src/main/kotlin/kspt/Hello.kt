package kspt

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import java.io.FileInputStream
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("No input file specifyed")
        exitProcess(-1)
    }

    val `in` = FileInputStream(args[0])

    // parse the file
    val cu = JavaParser.parse(`in`)

    // prints the resulting compilation unit to default system output
    println(cu.toString())
}

