package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.itangcent.intellij.context.ActionContext
import com.itangcent.common.utils.SystemUtils
import com.itangcent.intellij.actions.KotlinAnAction
import com.itangcent.intellij.util.ActionUtils
import com.itangcent.intellij.util.DocumentUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.time.DateFormatUtils
import java.util.*

/**
 *
 * @author TomNg
 */
class ModificationTagAction : KotlinAnAction("Modification Tag") {
    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {
        actionContext.runInSwingUI {
            val modification = Messages.showInputDialog(project, "Input Modification Tag", "New Modification Tag", Messages.getQuestionIcon())
                    ?: return@runInSwingUI
            actionContext.runAsync {
                try {
                    val editor = anActionEvent.getData(PlatformDataKeys.EDITOR) ?: return@runAsync
                    val document = editor.document
                    val lineCount = document.lineCount
                    //region 委托actionContext在UI线程执行-------------------------------------
                    actionContext.runInWriteUI {
                        var classStartLine = -1
                        for (line in 0 until lineCount) {
                            val lineText = DocumentUtils.getLineText(document, line)
                            if (StringUtils.isBlank(lineText)) {
                                continue
                            }
                            if (lineText.trim { it <= ' ' }.endsWith("*/")) {
                                val insertIndex = document.getLineStartOffset(line) - 1
                                document.insertString(insertIndex, createModification(modification))
                                break
                            }
                            if (DocumentUtils.isRootStart(lineText)) {
                                classStartLine = line
                                break
                            }
                        }
                        if (classStartLine != -1) {
                            for (line in classStartLine - 1 downTo 1) {
                                val lineText = DocumentUtils.getLineText(document, line)
                                if (lineText.startsWith("@")) {
                                    continue
                                }
                                val insertIndex = document.getLineEndOffset(line)
                                document.insertString(insertIndex, "\n/**${createModification(modification)}\n */")
                                break
                            }
                        }
                        ActionUtils.format(anActionEvent)
                    }
                    //endregion 委托actionContext在UI线程执行-------------------------------------
                } catch (ignored: Exception) {
                }
            }
        }
    }

    private fun createModification(modification: String?): String {
        var modificationTag = " [" + DateFormatUtils.format(Date(), "yyyyMMdd") + "]"
        modificationTag = StringUtils.rightPad(modificationTag, 20, '-')
        modificationTag += "[${SystemUtils.userName}]"
        modificationTag = StringUtils.rightPad(modificationTag, 40, '-')
        modificationTag += "[$modification]"
        modificationTag = StringUtils.rightPad(modificationTag, 80, '-')
        return "\n *$modificationTag"
    }
}
