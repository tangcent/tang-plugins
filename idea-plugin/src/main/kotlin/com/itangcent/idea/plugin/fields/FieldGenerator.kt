package com.itangcent.idea.plugin.fields

import com.intellij.lang.jvm.JvmModifier
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.util.containers.stream
import com.itangcent.idea.plugin.psi.PsiClassHelper.Companion.jvmFieldModifiers
import com.itangcent.idea.plugin.util.ActionUtils
import com.itangcent.idea.plugin.util.DocumentUtils
import com.itangcent.idea.plugin.util.FieldUtils
import com.itangcent.tang.common.utils.CollectionUtils
import java.io.IOException

class FieldGenerator : BasedFieldGenerator() {

    @Throws(IOException::class)
    fun generateFields(anActionEvent: AnActionEvent) {

        val currentClass = ActionUtils.findCurrentClass() ?: return

        for (field in currentClass.allFields) {
            field.modifiers
        }

        val existFields = currentClass.allFields.stream()
                .map { psiField -> psiField.name }
                .toArray()

        val fields = currentClass.allFields.stream()
                .filter { psiField -> CollectionUtils.containsAny(psiField.modifiers, jvmFieldModifiers) }
                .filter { psiField -> !psiField.hasModifier(JvmModifier.STATIC) }
                .map { psiField -> psiField.name }
                .filter { field -> !existFields.contains(FieldUtils.buildFiledName(field)) }
                .toArray()

        if (fields.isEmpty()) {
            return
        }

        val editor = anActionEvent.getData(PlatformDataKeys.EDITOR)

        val document = editor!!.document
        document.insertString(DocumentUtils.getInsertIndex(document), generateStaticFields(fields))
//        currentClass.add(OwnBufferLeafPsiElement(PlainTextTokenTypes.PLAIN_TEXT, generateStaticFields(fields)))
    }

    private fun generateStaticFields(fields: Array<Any>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\n    //region Fields-------------------------------------------------------------\n")
        for (field in fields) {

            stringBuilder.append("    public static final String ")
                    .append(FieldUtils.buildFiledName(field.toString()))
                    .append(" = \"")
                    .append(field)
                    .append("\";\n\n")
        }
        stringBuilder.append("    //endregion Fields-------------------------------------------------------------\n")
        return stringBuilder.toString()
    }

}
