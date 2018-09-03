package com.itangcent.idea.plugin.fields

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.itangcent.idea.plugin.util.FieldUtils
import com.sun.source.tree.VariableTree
import org.apache.commons.collections.CollectionUtils
import java.io.IOException
import kotlin.streams.toList

class FieldGenerator : BasedFieldGenerator() {

    @Throws(IOException::class)
    fun generateFields(editor: Editor, document: Document) {
        val javaTree = buildTree(document)
        val classTree = findCurrentClass(javaTree, editor, document) ?: return

        val existFields = classTree.members.stream()
                .filter { VariableTree::class.java.isInstance(it) }
                .map { node -> node as VariableTree }
                .map { node -> node.name.toString() }
                .toList()

        val fields = classTree.members.stream()
                .filter { VariableTree::class.java.isInstance(it) }
                .map { node -> node as VariableTree }
                .filter { variableTree -> CollectionUtils.containsAny(variableTree.modifiers.flags, BasedFieldGenerator.fieldModifiers) }
                .map { node -> node.name.toString() }
                .filter { field -> !existFields.contains(FieldUtils.buildFiledName(field)) }
                .toList()
        if (CollectionUtils.isEmpty(fields)) {
            return
        }

        val insertIndex = javaTree.getEndPosition(classTree) - 1
        document.insertString(insertIndex.toInt(), generateFields(fields))
    }

    private fun generateFields(fields: List<String>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\n    //region Fields-------------------------------------------------------------\n")
        for (field in fields) {

            stringBuilder.append("    public static final String ")
                    .append(FieldUtils.buildFiledName(field))
                    .append(" = \"")
                    .append(field)
                    .append("\";\n\n")
        }
        stringBuilder.append("    //endregion Fields-------------------------------------------------------------\n")
        return stringBuilder.toString()
    }
}
