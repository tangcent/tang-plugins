package com.itangcent.idea.plugin.fields

import com.itangcent.common.utils.GsonUtils
import com.itangcent.intellij.context.ActionContext
import com.itangcent.intellij.logger.Logger
import com.itangcent.intellij.psi.PsiClassHelper
import com.itangcent.intellij.util.ActionUtils
import java.io.IOException


class FieldJsonGenerator : BasedFieldGenerator() {

    private val logger: Logger = ActionContext.local()

    @Throws(IOException::class)
    fun generateFieldJson(): String {

        val currentClass = ActionUtils.findCurrentClass()
        if (currentClass == null) {
            logger.info("no class be selected!")
        }
//        val fields = classTree.members.stream()
//                .filter { VariableTree::class.java.isInstance(it) }
//                .map { node -> node as VariableTree }
//                .filter(BasedFieldGenerator.filedPredict)
//                .map { node -> node.name.toString() }
//                .toList()

        val kv = ActionContext.getContext()!!.instance(PsiClassHelper::class).getFields(currentClass)
        return GsonUtils.prettyJson(kv)

    }
}
