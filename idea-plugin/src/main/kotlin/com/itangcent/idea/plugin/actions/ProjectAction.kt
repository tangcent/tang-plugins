package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.itangcent.common.shell.DefaultShellUtils
import com.itangcent.common.shell.ShellUtils
import com.itangcent.idea.plugin.auth.AuthProvider
import com.itangcent.idea.plugin.auth.DialogAuthProvider
import com.itangcent.idea.plugin.dialog.ProjectDialog
import com.itangcent.idea.plugin.git.*
import com.itangcent.idea.plugin.project.ProjectInfoCollector
import com.itangcent.intellij.actions.KotlinAnAction
import com.itangcent.intellij.context.ActionContext
import com.itangcent.intellij.extend.guice.singleton
import com.itangcent.intellij.extend.guice.with
import com.itangcent.intellij.setting.DefaultSettingManager
import com.itangcent.intellij.setting.SettingManager
import com.itangcent.intellij.util.UIUtils

class ProjectAction : KotlinAnAction("Project") {


    override fun onBuildActionContext(builder: ActionContext.ActionContextBuilder) {
        super.onBuildActionContext(builder)

        builder.bind(SettingManager::class) { it.with(DefaultSettingManager::class).singleton() }
        builder.bind(AuthProvider::class) { it.with(DialogAuthProvider::class).singleton() }
        builder.bind(ReleaseMessageInput::class) { it.with(DialogReleaseMessageInput::class).singleton() }
        builder.bind(ShellUtils::class) { it.with(DefaultShellUtils::class).singleton() }
        builder.bind(GitUtils::class) { it.singleton() }
        builder.bind(GitRelease::class) { it.with(DefaultGitRelease::class).singleton() }

    }

    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {

        val basePath = project!!.basePath

        val projectDialog = ProjectDialog()
        val projectInfoCollector = actionContext.instance(ProjectInfoCollector::class)
        projectInfoCollector.collect(projectDialog, basePath!!)

        UIUtils.show(projectDialog)
    }
}
