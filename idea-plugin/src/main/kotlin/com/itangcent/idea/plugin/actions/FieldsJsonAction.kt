package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.itangcent.idea.plugin.config.ActionContext
import com.itangcent.idea.plugin.fields.FieldJsonGenerator
import com.itangcent.idea.plugin.util.ActionUtils
import com.itangcent.tang.common.exception.ProcessCanceledException
import org.apache.commons.lang3.exception.ExceptionUtils

/**
 * @author TomNg
 */
class FieldsJsonAction : LoggedAnAction("To Json") {

    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {

        try {
            val editor = anActionEvent.getData(PlatformDataKeys.EDITOR)
            if (editor != null) {
                val document = editor.document
                val fieldJsonGenerator = FieldJsonGenerator()
                //region 委托actionContext在UI线程执行---------------------------------
                actionContext.runInWriteUi {
                    val generateFieldJson = fieldJsonGenerator.generateFieldJson(editor, document)
                    logger.info("\n$generateFieldJson\n")
                    success("TOJSON", "")
                }
                //endregion 委托actionContext在UI线程执行---------------------------------
            } else {
                ActionUtils.format(anActionEvent)
            }

        } catch (e: Exception) {
            if (e is ProcessCanceledException) {
                failed("TOJSON", e.stopMsg)
            } else {
                failed("TOJSON", "failed by:" + ExceptionUtils.getStackTrace(e))
            }
        }

    }
}
