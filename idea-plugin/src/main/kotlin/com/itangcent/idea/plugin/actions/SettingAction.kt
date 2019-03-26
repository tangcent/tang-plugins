package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.itangcent.idea.plugin.context.ActionContext
import com.itangcent.idea.plugin.dialog.GitSettingDialog
import com.itangcent.idea.plugin.extend.guice.singleton
import com.itangcent.idea.plugin.extend.guice.with
import com.itangcent.idea.plugin.setting.DefaultSettingManager
import com.itangcent.idea.plugin.setting.SettingManager
import com.itangcent.idea.plugin.util.UIUtils

class SettingAction : InitAnAction() {

    override fun onBuildActionContext(builder: ActionContext.ActionContextBuilder) {
        super.onBuildActionContext(builder)

        builder.bind(SettingManager::class) { it.with(DefaultSettingManager::class).singleton() }
    }

    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {
        val gitSettingDialog = actionContext.instance { GitSettingDialog() }
        UIUtils.show(gitSettingDialog)
    }
}
