package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.itangcent.idea.plugin.fields.FieldGenerator
import com.itangcent.intellij.actions.KotlinAnAction
import com.itangcent.intellij.context.ActionContext
import com.itangcent.intellij.extend.guice.singleton
import com.itangcent.intellij.logger.Logger
import com.itangcent.intellij.psi.PsiClassHelper
import com.itangcent.intellij.psi.TmTypeHelper
import com.itangcent.intellij.util.ActionUtils
import org.apache.commons.lang3.exception.ExceptionUtils

/**
 * @author TomNg
 */
class FieldsGenerateAction : KotlinAnAction("Generate Fields") {

    private val logger: Logger? = ActionContext.local()

    override fun onBuildActionContext(builder: ActionContext.ActionContextBuilder) {
        super.onBuildActionContext(builder)

        builder.bind(PsiClassHelper::class) { it.singleton() }
        builder.bind(TmTypeHelper::class) { it.singleton() }
    }

    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {

        val editor = anActionEvent.getData(PlatformDataKeys.EDITOR)

        if (editor != null) {
            val fieldGenerator = FieldGenerator()
            //region 委托actionContext在UI线程执行---------------------------------
            actionContext.runInWriteUI {
                try {
                    fieldGenerator.generateFields(anActionEvent)
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
