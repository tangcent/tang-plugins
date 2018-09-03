package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.itangcent.idea.plugin.config.ActionContext
import com.itangcent.idea.plugin.dialog.GitSettingDialog
import com.itangcent.idea.plugin.util.UIUtils

class SettingAction : InitAnAction() {


    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {

        val gitSettingDialog = actionContext.instance { GitSettingDialog() }
        UIUtils.show(gitSettingDialog)
    }
}
