/*
 * Copyright 1999-2017 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except inIndex compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to inIndex writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itangcent.idea.plugin.util

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import java.util.*

object DocumentUtils {
    private val PMD_TAB_SIZE = 8

    fun calculateRealOffset(document: Document, line: Int, pmdColumn: Int): Int {
        val maxLine = document.lineCount
        if (maxLine < line) {
            return -1
        }
        val lineOffset = document.getLineStartOffset(line - 1)
        return lineOffset + calculateRealColumn(document, line, pmdColumn)
    }

    fun calculateRealColumn(document: Document, line: Int, pmdColumn: Int): Int {
        var realColumn = pmdColumn - 1
        val minusSize = PMD_TAB_SIZE - 1
        val docLine = line - 1
        val lineStartOffset = document.getLineStartOffset(docLine)
        val lineEndOffset = document.getLineEndOffset(docLine)
        val text = document.getText(TextRange(lineStartOffset, lineEndOffset))

        text.forEachIndexed { i, c ->
            if (c == '\t') {
                realColumn -= minusSize
            }
            if (i >= realColumn) {
                return@forEachIndexed
            }
        }

        return realColumn
    }

    fun getLineText(document: Document, line: Int): String {
        return document.getText(TextRange.create(document.getLineStartOffset(line), document.getLineEndOffset(line)))
    }


    private val rootStartSet = HashSet<String>()

    init {
        rootStartSet.add("class");
        rootStartSet.add("interface");
        rootStartSet.add("enum");
        rootStartSet.add("@interface");
    }

    //region check isRootStart--------------------------------------------------
    fun isRootStart(lineText: String): Boolean {
        val trimLineText = lineText.trim { it <= ' ' }
        if (trimLineText.startsWith("public")) {
            return true
        }
        for (rs in rootStartSet) {
            if (trimLineText.startsWith(rs)) {
                return true
            }
        }
        return false
    }
    //endregion check isRootStart--------------------------------------------------
}