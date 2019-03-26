package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.itangcent.idea.plugin.clipboard.ClipboardData
import com.itangcent.idea.plugin.clipboard.ThrottleClipboardManager
import com.itangcent.idea.plugin.context.ActionContext
import com.itangcent.idea.plugin.logger.Logger
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils

class SaveToClipboardAction : InitAnAction("Save to clipboard") {

    private val clipboardManager: ThrottleClipboardManager = ThrottleClipboardManager(ActionContext.local())

    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {

        actionContext.runInReadUi {
            val editor = anActionEvent.getData(PlatformDataKeys.EDITOR)

            if (editor != null) {

                val logger: Logger? = actionContext.instance(Logger::class)

                val clipboardData = ClipboardData()

                try {
                    val selectedText = editor.selectionModel.selectedText
                    if (StringUtils.isNoneEmpty(selectedText)) {
                        //保存当前选中的内容
                        clipboardData.content = selectedText
                    } else {
                        //保存当前文档
                        val path = StringUtils.substringBetween(editor.document.toString(), "[", "]")
                        clipboardData.title = StringUtils.substringAfterLast(path, "/")
                        clipboardData.content = editor.document.text
                    }
                    clipboardManager.saveData(clipboardData)
                } catch (e: Exception) {
                    logger!!.error("error save selected text:" + ExceptionUtils.getStackTrace(e))
                }
            }
        }
    }
}
