package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.itangcent.idea.plugin.dialog.ClipboardDialog
import com.itangcent.intellij.actions.KotlinAnAction
import com.itangcent.intellij.context.ActionContext
import com.itangcent.intellij.util.UIUtils

class ClipboardAction : KotlinAnAction("Clipboard") {

    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {

        val clipboardDialog = actionContext.instance { ClipboardDialog() }

        UIUtils.show(clipboardDialog)
    }
}
