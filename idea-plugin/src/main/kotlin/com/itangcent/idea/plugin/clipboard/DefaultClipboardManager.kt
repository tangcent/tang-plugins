package com.itangcent.idea.plugin.clipboard

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.inject.Inject
import com.itangcent.common.utils.GsonUtils
import com.itangcent.common.utils.IDUtils
import com.itangcent.common.utils.SystemUtils
import com.itangcent.intellij.logger.Logger
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit

class DefaultClipboardManager : ClipboardManager {


    @Inject
    private val logger: Logger? = null

    private var cacheIndexes: ClipboardIndexes? = null

    /**
     * 目录文件
     */
    private val indexesFile: File
        @Synchronized get() {

            var home = SystemUtils.userHome
            if (home.endsWith("/")) {
                home = home.substring(0, home.length - 1)
            }
            val repositoryFile = "$home/.tm/cp.indexes"
            val file = File(repositoryFile)

            if (!file.exists()) {
                try {
                    FileUtils.forceMkdirParent(file)
                    if (!file.createNewFile()) {
                        logger!!.error("error to create new setting file")
                    }
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }

            }
            return file
        }

    private val indexes: ClipboardIndexes
        get() {
            if (cacheIndexes == null) {
                init()
            }

            if (cacheIndexes == null) {
                cacheIndexes = ClipboardIndexes()
                cacheIndexes!!.indexes = ArrayList()
            }
            return cacheIndexes as ClipboardIndexes
        }

    private val contentCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build<String, String>(CacheLoader.from { id -> getContentFromFile(id!!) })

    @Synchronized
    private fun init() {
        if (cacheIndexes == null) {
            try {
                val str = FileUtils.readFileToString(indexesFile, Charset.defaultCharset())
                cacheIndexes = GsonUtils.fromJson(str, ClipboardIndexes::class)
            } catch (e: Exception) {
                logger!!.error("error init settingRepository:" + ExceptionUtils.getStackTrace(e))
            }
        }
    }

    override fun getData(): Array<ClipboardData> {
        return indexes.indexes!!.toTypedArray()
    }

    override fun getData(id: String): ClipboardData? {

        val data = indexes.indexes!!
                .firstOrNull { clipboardData: ClipboardData? -> clipboardData!!.id == id } ?: return null
        val clipboardData = ClipboardData()
        clipboardData.id = id
        clipboardData.title = data.title
        clipboardData.content = contentCache.getUnchecked(id)
        return clipboardData
    }

    private fun getContentFile(id: String): File {

        var home = SystemUtils.userHome
        if (home.endsWith("/")) {
            home = home.substring(0, home.length - 1)
        }
        val repositoryFile = "$home/.tm/cp/$id"
        val file = File(repositoryFile)

        if (!file.exists()) {
            try {
                org.apache.commons.io.FileUtils.forceMkdirParent(file)
                if (!file.createNewFile()) {
                    logger!!.error("error to create new setting file")
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

        }
        return file
    }

    override fun getContent(id: String): String? {
        return contentCache.getUnchecked(id)
    }

    private fun getContentFromFile(id: String): String? {
        return com.itangcent.common.utils.FileUtils.read(getContentFile(id))
    }

    override fun saveData(clipboardData: ClipboardData) {

        val savedClipboardData = ClipboardData()

        savedClipboardData.id = clipboardData.id
        savedClipboardData.title = clipboardData.title
        savedClipboardData.content = clipboardData.content

        //title为空时，尝试取content首行,限制30个字符
        if (org.apache.commons.lang3.StringUtils.isBlank(savedClipboardData.title)) {
            savedClipboardData.title = StringUtils.substringBefore(savedClipboardData.content!!.trim(), "\n").trim()
            if (savedClipboardData.title!!.length > 30) {
                savedClipboardData.title = savedClipboardData.title!!.substring(0, 30)
            }
        }

        //新增数据
        if (savedClipboardData.id == null) {
            addIndexes(savedClipboardData)
            saveContent(savedClipboardData)
            return
        }

        //id无效/旧数据丢失 ，按新增数据处理
        val oldClipboardData: ClipboardData? = getData(savedClipboardData.id!!)
        if (oldClipboardData == null) {
            addIndexes(savedClipboardData)
            saveContent(savedClipboardData)
            return
        }


        if (!oldClipboardData.title.equals(savedClipboardData.title)) {
            oldClipboardData.title = savedClipboardData.title
            this.indexes.indexes = this.indexes.indexes!!
                    .map { cd ->
                        cd.content = ""//清除content
                        if (cd.id.equals(savedClipboardData.id)) {
                            cd.title = savedClipboardData.title//修改当前title
                        }
                        cd
                    }
                    .sortedBy { data -> data.title }

            updateIndexesFile()
        }
        if (!oldClipboardData.content.equals(savedClipboardData.content)) {
            contentCache.invalidate(savedClipboardData.id!!)
            saveContent(savedClipboardData)
        }
    }

    private fun addIndexes(clipboardData: ClipboardData) {
        val clipboardDatas = this.indexes.indexes!!.toMutableList()
        val newClipboardData = ClipboardData()
        clipboardData.id = IDUtils.UUID()
        newClipboardData.id = clipboardData.id
        newClipboardData.title = clipboardData.title
        clipboardDatas.add(newClipboardData)
        this.indexes.indexes = clipboardDatas.sortedBy { data -> data.title }
        updateIndexesFile()
    }

    private fun updateIndexesFile() {
        com.itangcent.common.utils.FileUtils.write(indexesFile, GsonUtils.toJson(this.indexes))
    }

    private fun saveContent(clipboardData: ClipboardData) {
        com.itangcent.common.utils.FileUtils.write(getContentFile(clipboardData.id!!), clipboardData.content!!)
    }

    fun saveContent(id: String, content: String) {
        com.itangcent.common.utils.FileUtils.write(getContentFile(id), content)
    }

    override fun deleteData(id: String) {
        //update indexes
        val clipboardDatas = this.indexes.indexes!!.toMutableList()
                .filter { clipboardData -> clipboardData.id != id }
        this.indexes.indexes = clipboardDatas
        updateIndexesFile()

        com.itangcent.common.utils.FileUtils.remove(getContentFile(id))
    }

}