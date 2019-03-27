package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.itangcent.idea.plugin.context.ActionContext
import com.itangcent.idea.plugin.format.Formatter
import com.itangcent.idea.plugin.format.RegionFormatter
import com.itangcent.idea.plugin.util.ActionUtils
import java.util.*

/**
 * @author TomNg
 */
class FormatAction : InitAnAction() {
    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {

        val editor = anActionEvent.getData(PlatformDataKeys.EDITOR)

        if (editor != null) {
            val document = editor.document
            val lineCount = document.lineCount
            val formatterList = ArrayList<Formatter>()
            formatterList.add(RegionFormatter(80))
            //to use other formatters
            //region 委托actionContext在UI线程执行---------------------------------
            actionContext.runInWriteUI {
                for (line in 0 until lineCount) {
                    for (formatter in formatterList) {
                        formatter.format(document, line)
                    }
                }
                ActionUtils.format(anActionEvent)
//                ActionUtils.optimize(anActionEvent)
            }
            //endregion 委托actionContext在UI线程执行---------------------------------
        } else {
            ActionUtils.format(anActionEvent)
        }
    }
}
