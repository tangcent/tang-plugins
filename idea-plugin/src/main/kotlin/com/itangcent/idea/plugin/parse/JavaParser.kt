package com.itangcent.idea.plugin.parse

import com.sun.tools.javac.api.JavacTool
import com.sun.tools.javac.file.JavacFileManager
import com.sun.tools.javac.util.Context
import java.nio.charset.Charset

class JavaParser {
    private val javacTool: JavacTool = JavacTool.create()

    fun parse(path: String): JavaTree {
        val context = Context()
        val fileManager = JavacFileManager(context, true, Charset.defaultCharset())
        val files = fileManager.getJavaFileObjects(path)
        val javacTask = javacTool.getTask(null, fileManager, null, null, null, files)
        return JavaTree(javacTask)
    }
}