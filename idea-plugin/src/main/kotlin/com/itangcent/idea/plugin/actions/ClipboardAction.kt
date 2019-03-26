package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.itangcent.idea.plugin.context.ActionContext
import com.itangcent.idea.plugin.dialog.ClipboardDialog
import com.itangcent.idea.plugin.util.UIUtils

class ClipboardAction : InitAnAction("Clipboard") {

    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {

        val clipboardDialog = actionContext.instance { ClipboardDialog() }

        UIUtils.show(clipboardDialog)
    }
}
