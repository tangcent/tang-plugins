package com.itangcent.idea.plugin.clipboard

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ThrottleClipboardManager(private var clipboardManager: ClipboardManager) : ClipboardManager {

    var index = AtomicInteger(0)

    //缓存作为过滤器
    private var filterCache: Cache<String, Int> = CacheBuilder
            .newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .build<String, Int>()

    /**
     * 设置过滤时间
     */
    fun throttle(duration: Long, unit: TimeUnit) {
        val newFilterCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(duration, unit)
                .build<String, Int>()
        newFilterCache.putAll(filterCache.asMap())
        filterCache = newFilterCache
    }

    override fun getData(): Array<ClipboardData> {
        return clipboardManager.getData()
    }

    override fun getData(id: String): ClipboardData? {
        return clipboardManager.getData(id)
    }

    override fun getContent(id: String): String? {
        return clipboardManager.getContent(id)
    }

    override fun saveData(clipboardData: ClipboardData) {
        var hashCode = ""
        if (clipboardData.title != null) {
            hashCode = "${clipboardData.title!!.length}_${clipboardData.title!!.hashCode()}_"
        }
        if (clipboardData.content != null) {
            hashCode += "${clipboardData.content!!.length}_${clipboardData.content!!.hashCode()}"
        }
        val index = this.index.getAndIncrement()
        if (index == filterCache.get(hashCode) { index }) {
            clipboardManager.saveData(clipboardData)
        }
    }

    override fun deleteData(id: String) {
        clipboardManager.deleteData(id)
    }
}