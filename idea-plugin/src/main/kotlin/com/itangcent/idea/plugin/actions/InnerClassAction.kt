package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.itangcent.intellij.actions.KotlinAnAction
import com.itangcent.intellij.context.ActionContext
import com.itangcent.intellij.util.ActionUtils
import com.itangcent.intellij.util.DocumentUtils
import com.itangcent.intellij.util.EditorUtils
import org.apache.commons.lang.StringUtils

/**
 * Created by TomNg on 2017/2/16.
 */
class InnerClassAction : KotlinAnAction("Inner Class") {
    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {
        actionContext.runInSwingUI {
            val className = Messages.showInputDialog(project, "Input Class Name", "New Inner Class", Messages.getQuestionIcon())
                    ?: return@runInSwingUI
            actionContext.runInWriteUI {
                try {
                    val editor = anActionEvent.getData(PlatformDataKeys.EDITOR)
                    val document: Document
                    if (editor != null) {
                        document = editor.document
                        val lineCount = document.lineCount

                        //region build inner class--------------------------------------
                        for (line in lineCount - 1 downTo -1 + 1) {
                            val lineText = DocumentUtils.getLineText(document, line)
                            if (StringUtils.isBlank(lineText)) {
                                continue
                            }
                            if (lineText.trim { it <= ' ' }.endsWith("}")) {
                                val insertIndex = document.getLineEndOffset(line) - 1
                                document.insertString(insertIndex, createClass(className))
                                break
                            }
                        }
                        //endregion build inner class--------------------------------------

                        ActionUtils.format(anActionEvent)

                        //region Move caret to inner class------------------------------
                        val newLineCount = document.lineCount
                        for (line in newLineCount - 1 downTo 1) {
                            if (DocumentUtils.isRootStart(DocumentUtils.getLineText(document, line))) {
                                EditorUtils.caretMoveToOffset(editor, document.getLineEndOffset(line))
                                break
                            }
                        }
                        //endregion Move caret to inner class------------------------------
                    }
                    //endregion 委托actionContext在UI线程执行-------------------------------------
                } catch (ignored: Exception) {
                }
            }
        }
    }

    private fun createClass(className: String?): String {
        return "\n\tpublic class $className {\n\t}\n"
    }
}
