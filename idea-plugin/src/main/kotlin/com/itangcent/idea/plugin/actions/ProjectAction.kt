package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.itangcent.idea.plugin.config.ActionContext
import com.itangcent.idea.plugin.dialog.ProjectDialog
import com.itangcent.idea.plugin.project.ProjectInfoCollector
import com.itangcent.idea.plugin.util.UIUtils


class ProjectAction : InitAnAction("Project") {

    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {

        val basePath = project!!.basePath

        val projectDialog = ProjectDialog()
        val projectInfoCollector = actionContext.instance(ProjectInfoCollector::class)
        projectInfoCollector.collect(projectDialog, basePath!!)

        UIUtils.show(projectDialog)


    }
}
