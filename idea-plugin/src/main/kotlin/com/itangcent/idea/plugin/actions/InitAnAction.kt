package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.itangcent.idea.plugin.constant.CacheKey
import com.itangcent.idea.plugin.context.ActionContext
import com.itangcent.idea.plugin.extend.guice.singleton
import com.itangcent.idea.plugin.extend.guice.with
import com.itangcent.idea.plugin.logger.ConsoleRunnerLogger
import com.itangcent.tang.common.concurrent.ValueHolder
import com.itangcent.tang.common.exception.ProcessCanceledException
import org.apache.commons.lang3.exception.ExceptionUtils
import javax.swing.Icon

abstract class InitAnAction : AnAction {

    constructor() : super()
    constructor(icon: Icon?) : super(icon)
    constructor(text: String?) : super(text)
    constructor(text: String?, description: String?, icon: Icon?) : super(text, description, icon)

    private val log: Logger = Logger.getInstance(this.javaClass.name)

    open protected fun onBuildActionContext(builder: ActionContext.ActionContextBuilder) {
//        builder.addModule(BasicPluginModule())

        builder.bind(com.itangcent.idea.plugin.logger.Logger::class) { it.with(ConsoleRunnerLogger::class).singleton() }
    }

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val project = anActionEvent.getData(PlatformDataKeys.PROJECT) ?: return

        val actionContextBuilder = ActionContext.builder()
        onBuildActionContext(actionContextBuilder)
        actionContextBuilder.bindInstance(project)
        actionContextBuilder.bindInstance(anActionEvent)
        val actionContext = actionContextBuilder.build()
        actionContext.init(this)

        log.info("start action")

        if (actionContext.lock()) {
            actionContext.cache(CacheKey.PROJECT, project)
            actionContext.registerSupplier(DataKey::class, { key ->
                val valueHolder: ValueHolder<Any> = ValueHolder()
                actionContext.runInReadUi {
                    valueHolder.data = anActionEvent.getData(key)
                }
                return@registerSupplier valueHolder.data
            })
            actionContext.runAsync {
                try {
                    actionPerformed(actionContext, project, anActionEvent)
                } catch (ex: Exception) {
                    log.info("Error:${ex.message}trace:${ExceptionUtils.getStackTrace(ex)}")
                    actionContext.runInWriteUi {
                        Messages.showMessageDialog(project, when (ex) {
                            is ProcessCanceledException -> ex.stopMsg
                            else -> "Error at:${ex.message}trace:${ExceptionUtils.getStackTrace(ex)}"
                        }, "Error", Messages.getInformationIcon())
                    }
                }
            }
        } else {
            log.info("Found unfinished task!")
            actionContext.runInWriteUi {
                Messages.showMessageDialog(project, "Found unfinished task! ",
                        "Error", Messages.getInformationIcon())
            }
        }
        actionContext.waitCompleteAsync()
    }

    protected abstract fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent)
}

