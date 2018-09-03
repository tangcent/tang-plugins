package com.itangcent.idea.plugin.fields

import com.google.gson.GsonBuilder
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.sun.source.tree.VariableTree
import java.io.IOException
import java.util.*
import kotlin.streams.toList

class FieldJsonGenerator : BasedFieldGenerator() {

    @Throws(IOException::class)
    fun generateFieldJson(editor: Editor, document: Document): String {

        val javaTree = buildTree(document)
        val classTree = findCurrentClass(javaTree, editor, document) ?: return ""

        val fields = classTree.members.stream()
                .filter { VariableTree::class.java.isInstance(it) }
                .map { node -> node as VariableTree }
                .filter(BasedFieldGenerator.filedPredict)
                .map { node -> node.name.toString() }
                .toList()

        return generateFieldJson(fields)
    }

    private fun generateFieldJson(fields: List<String>): String {
        val map = HashMap<String, String>()
        for (field in fields) {
            map[field] = ""
        }
        return gson.toJson(map)
    }

    companion object {

        private val gson = GsonBuilder()
                .setPrettyPrinting()
                .create()
    }
}
