package com.itangcent.idea.plugin.clipboard

import com.google.inject.ImplementedBy

@ImplementedBy(DefaultClipboardManager::class)
interface ClipboardManager {

    /**
     * 没有content
     */
    fun getData(): Array<ClipboardData>

    fun getData(id: String): ClipboardData?

    fun getContent(id: String): String?

    fun saveData(clipboardData: ClipboardData)

    fun deleteData(id: String)
}