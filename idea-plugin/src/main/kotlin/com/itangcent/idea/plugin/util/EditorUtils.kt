package com.itangcent.idea.plugin.util

import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Editor

/**
 * @author tangming
 */
object EditorUtils {
    fun caretMoveToOffset(editor: Editor, offset: Int) {
        val caretModel = editor.caretModel
        caretModel.currentCaret.moveToOffset(offset)
    }

    fun currentOffset(editor: Editor): Int {
        val caretModel = editor.caretModel
        return caretModel.currentCaret.offset
    }
}
