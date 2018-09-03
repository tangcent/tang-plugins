package com.itangcent.idea.plugin.format

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.itangcent.tang.common.utils.StringUtils
import java.util.*

/**
 * Created by TomNg on 2017/2/16.
 */
class RegionFormatter : Formatter {
    private val padSize: Int

    constructor(padSize: Int = 50) {
        this.padSize = padSize
        this.regionStack = Stack()
    }

    private val regionStack: Stack<String>

    override fun format(document: Document, line: Int) {
        try {
            val start = document.getLineStartOffset(line)
            val end = document.getLineEndOffset(line)
            val lineText = document.getText(TextRange.create(start, end))
            var trimText = lineText.trim { it <= ' ' }
            if (trimText.startsWith("//region")) {
                val firstCharacter = StringUtils.firstCharacter(lineText)
                trimText = org.apache.commons.lang.StringUtils.rightPad(trimText, padSize - firstCharacter, "-")
                document.replaceString(start + firstCharacter, end, trimText)
                regionStack.add(trimText.substring(8))
            } else if (trimText.startsWith("//endregion")) {
                val realStart = start + StringUtils.firstCharacter(lineText)
                document.replaceString(realStart, end, "//endregion" + regionStack.pop())
            }
        } catch (ignored: Exception) {
        }

    }
}
