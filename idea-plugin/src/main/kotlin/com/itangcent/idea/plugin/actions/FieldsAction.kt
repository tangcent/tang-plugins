package com.itangcent.idea.plugin.actions

import com.google.inject.Inject
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.itangcent.idea.plugin.config.ActionContext
import com.itangcent.idea.plugin.fields.FieldGenerator
import com.itangcent.idea.plugin.logger.Logger
import com.itangcent.idea.plugin.util.ActionUtils
import org.apache.commons.lang3.exception.ExceptionUtils

/**
 * @author TomNg
 */
class FieldsAction : InitAnAction("Generate Fields") {

    @Inject
    protected val logger: Logger? = ActionContext.local()

    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {

        val editor = anActionEvent.getData(PlatformDataKeys.EDITOR)

        if (editor != null) {
            val document = editor.document
            val fieldGenerator = FieldGenerator()
            //region 委托actionContext在UI线程执行---------------------------------
            actionContext.runInUi {
                try {
                    fieldGenerator.generateFields(editor, document)
                } catch (e: Exception) {
                    logger!!.info("error:" + ExceptionUtils.getStackTrace(e))
                }
                ActionUtils.format(anActionEvent);
                ActionUtils.optimize(anActionEvent);

            }
            //endregion 委托actionContext在UI线程执行---------------------------------
        } else {
            ActionUtils.format(anActionEvent);
        }

    }

}
