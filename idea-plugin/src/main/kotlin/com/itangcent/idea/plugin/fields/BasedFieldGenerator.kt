package com.itangcent.idea.plugin.fields

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.itangcent.idea.plugin.config.ActionContext
import com.itangcent.idea.plugin.parse.JavaParser
import com.itangcent.idea.plugin.parse.JavaTree
import com.itangcent.idea.plugin.util.EditorUtils
import com.sun.source.tree.ClassTree
import com.sun.source.tree.VariableTree
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.StringUtils
import java.io.IOException
import java.util.*
import java.util.function.Predicate
import javax.lang.model.element.Modifier

open class BasedFieldGenerator {

    protected fun buildTree(document: Document): JavaTree {
        var path = StringUtils.substringBetween(document.toString(), "[", "]")
        path = StringUtils.removeStart(path, "file://")
        val javaParser = ActionContext.getContext()!!.instance(JavaParser::class)
        return javaParser.parse(path)
    }

    @Throws(IOException::class)
    protected fun findCurrentClass(javaTree: JavaTree, editor: Editor, document: Document): ClassTree? {
        val classTrees = javaTree.find(ClassTree::class.java)
        var classTree: ClassTree? = null
        if (classTrees.size == 1) {
            classTree = classTrees[0]
        } else {
            val offset = EditorUtils.currentOffset(editor)
            val start = -1
            for (tree in classTrees) {
                val position = javaTree.getPosition(tree)
                if (position.contain(offset.toLong()) && position.start > start) {
                    classTree = tree
                }
            }
        }

        return classTree
    }

    companion object {

        var fieldModifiers: Set<Modifier> = HashSet(Arrays.asList(Modifier.PRIVATE, Modifier.PROTECTED))
        var notFieldModifiers: Set<Modifier> = HashSet(Arrays.asList(Modifier.STATIC, Modifier.FINAL))
        var filedPredict: Predicate<VariableTree> = Predicate { vt ->
            CollectionUtils.containsAny(vt.modifiers.flags,
                    BasedFieldGenerator.fieldModifiers) && !CollectionUtils.containsAny(vt.modifiers.flags,
                    BasedFieldGenerator.notFieldModifiers)
        }
    }
}
