package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.itangcent.idea.plugin.config.ActionContext
import com.itangcent.idea.plugin.constant.CacheKey
import com.itangcent.tang.common.exception.ProcessCanceledException
import org.apache.commons.lang3.exception.ExceptionUtils
import javax.swing.Icon

abstract class InitAnAction : AnAction {

    constructor() : super()
    constructor(icon: Icon?) : super(icon)
    constructor(text: String?) : super(text)
    constructor(text: String?, description: String?, icon: Icon?) : super(text, description, icon)

    private val log: Logger = Logger.getInstance(this.javaClass.name)

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val actionContext = ActionContext()

        log.info("start action")

        val project = anActionEvent.getData(PlatformDataKeys.PROJECT) ?: return
        if (actionContext.lock()) {
            actionContext.cache(CacheKey.PROJECT, project)
            actionContext.runAsync {
                try {
                    actionPerformed(actionContext, project, anActionEvent)
                } catch (ex: Exception) {
                    log.info("Error:${ex.message}trace:${ExceptionUtils.getStackTrace(ex)}")
                    actionContext.runInUi {
                        Messages.showMessageDialog(project, when (ex) {
                            is ProcessCanceledException -> ex.stopMsg
                            else -> "Error at:${ex.message}trace:${ExceptionUtils.getStackTrace(ex)}"
                        }, "Error", Messages.getInformationIcon())
                    }
                }
            }
        } else {
            log.info("Found unfinished task!")
            Messages.showMessageDialog(project, "Found unfinished task! ",
                    "Error", Messages.getInformationIcon())
        }
        actionContext.waitCompleteAsync()
    }

    protected abstract fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent)
}

