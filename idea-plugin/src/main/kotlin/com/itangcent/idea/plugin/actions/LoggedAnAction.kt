package com.itangcent.idea.plugin.actions

import com.google.inject.Inject
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.itangcent.idea.plugin.config.ActionContext
import com.itangcent.idea.plugin.constant.CacheKey
import com.itangcent.idea.plugin.logger.Logger
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import javax.swing.Icon

abstract class LoggedAnAction : AnAction {

    constructor() : super()
    constructor(icon: Icon?) : super(icon)
    constructor(text: String?) : super(text)
    constructor(text: String?, description: String?, icon: Icon?) : super(text, description, icon)

    protected val logger: Logger = ActionContext.local()

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val actionContext = ActionContext()

        val project = anActionEvent.getData(PlatformDataKeys.PROJECT) ?: return

        if (actionContext.lock()) {
            actionContext.cache(CacheKey.PROJECT, project)
            actionContext.runAsync {
                try {
                    val startTime = System.currentTimeMillis()
                    actionContext.cache(CacheKey.STARTTIME, startTime)
                    actionPerformed(actionContext, project, anActionEvent)
                } catch (ex: Exception) {
                    actionContext.runInUi {
                        Messages.showMessageDialog(project, "Error at:" + ex.message
                                + "trace:" + ExceptionUtils.getStackTrace(ex),
                                "Error", Messages.getInformationIcon())
                    }
                }
            }
        } else {
            Messages.showMessageDialog(project, "Found unfinished task! ",
                    "Error", Messages.getInformationIcon())
        }
        actionContext.waitCompleteAsync()
    }

    protected abstract fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent)

    protected fun success(prefix: String, msg: String?) {
        end("SUCCESS", prefix, msg)
    }

    protected fun failed(prefix: String, msg: String?) {
        end("FAILED", prefix, msg)
    }

    private fun end(status: String, prefix: String, msg: String?) {
        val actionContext = ActionContext.getContext()!!

        val startTime = actionContext.getCache<Long>(CacheKey.STARTTIME)
        val elapse = System.currentTimeMillis() - startTime!!

        var reportMsg = "$prefix $status"
        if (StringUtils.isNotBlank(msg)) {
            reportMsg += "\t" + msg
        }
        logger.info(reportMsg)
        logger.info(String.format("ELAPSE [ %.3f s ]", elapse / 1000.0))
    }
}
