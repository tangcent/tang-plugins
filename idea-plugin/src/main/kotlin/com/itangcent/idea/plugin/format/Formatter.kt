package com.itangcent.idea.plugin.format

import com.intellij.openapi.editor.Document

/**
 * Created by TomNg on 2017/2/16.
 */
interface Formatter {
    fun format(document: Document, line: Int)
}
