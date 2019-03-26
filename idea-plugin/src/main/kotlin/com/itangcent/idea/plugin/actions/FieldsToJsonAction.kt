package com.itangcent.idea.plugin.actions

import com.google.inject.Inject
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.itangcent.idea.plugin.context.ActionContext
import com.itangcent.idea.plugin.extend.guice.singleton
import com.itangcent.idea.plugin.fields.FieldJsonGenerator
import com.itangcent.idea.plugin.logger.Logger
import com.itangcent.idea.plugin.psi.PsiClassHelper
import com.itangcent.idea.plugin.psi.TmTypeHelper
import com.itangcent.idea.plugin.util.ActionUtils
import com.itangcent.idea.plugin.util.ToolUtils
import org.apache.commons.lang3.exception.ExceptionUtils

/**
 * @author TomNg
 */
class FieldsToJsonAction : InitAnAction("To Json") {

    @Inject
    private val logger: Logger? = null

    override fun onBuildActionContext(builder: ActionContext.ActionContextBuilder) {
        super.onBuildActionContext(builder)

        builder.bind(PsiClassHelper::class) { it.singleton() }
        builder.bind(TmTypeHelper::class) { it.singleton() }
    }

    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {

        try {
            val editor = anActionEvent.getData(PlatformDataKeys.EDITOR)
            if (editor != null) {
                val fieldJsonGenerator = FieldJsonGenerator()
                //region 委托actionContext在UI线程执行---------------------------------
                actionContext.runInWriteUi {
                    val generateFieldJson = fieldJsonGenerator.generateFieldJson()
                    ToolUtils.copy2Clipboard(generateFieldJson)
                    logger!!.log("\n$generateFieldJson\n")
                }
                //endregion 委托actionContext在UI线程执行---------------------------------
            } else {
                ActionUtils.format(anActionEvent)
            }
        } catch (e: Exception) {
            logger!!.error("To json failed:" + ExceptionUtils.getStackTrace(e))
        }
    }
}
