package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

/**
 *
 * @author TomNg
 */
class CopyRightAction : AnAction("CopyRight") {
    override fun actionPerformed(anActionEvent: AnActionEvent) {

        val project = anActionEvent.getData(PlatformDataKeys.PROJECT)
        Messages.showMessageDialog(project, "Copyright © 2018,Tangming. All Rights Reserved.", "CopyRight", Messages.getInformationIcon())
    }
}
