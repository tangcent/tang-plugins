package com.itangcent.idea.plugin.util

import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * Created by TomNg on 2017/2/16.
 */
object ActionUtils {

    fun format(anActionEvent: AnActionEvent) {
        doAction(anActionEvent, "ReformatCode")
    }

    fun optimize(anActionEvent: AnActionEvent) {
        doAction(anActionEvent, "OptimizeImports")
    }

    fun doAction(anActionEvent: AnActionEvent, action: String) {
        try {
            anActionEvent.actionManager.getAction(action).actionPerformed(anActionEvent)
        } catch (ignored: Exception) {
        }

    }

}
