package com.itangcent.idea.plugin.fields


import com.itangcent.tang.common.utils.GsonUtils
import java.util.*

/**
 * Map简化操作类
 */
class KV<K, V> : LinkedHashMap<K, V>() {

    operator fun set(key: K, value: V): KV<K, V> {
        super.put(key, value)
        return this
    }

    fun set(map: Map<K, V>): KV<K, V> {
        super.putAll(map)
        return this
    }

    fun set(KV: KV<K, V>): KV<K, V> {
        super.putAll(KV)
        return this
    }

    fun delete(key: K): KV<K, V> {
        super.remove(key)
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAs(key: K): T {
        return get(key) as T
    }

    fun getStr(key: K): String? {
        return get(key)?.toString()
    }

    fun getInt(key: K): Int? {
        return get(key) as Int
    }

    fun getLong(key: K): Long? {
        return get(key) as Long
    }

    fun getBoolean(key: K): Boolean? {
        return get(key) as Boolean
    }

    fun getFloat(key: K): Float? {
        return get(key) as Float
    }

    /**
     * key 存在，并且 value 不为 null
     */
    fun notNull(key: K): Boolean {
        return get(key) != null
    }

    /**
     * key 不存在，或者 key 存在但 value 为null
     */
    fun isNull(key: K): Boolean {
        return get(key) == null
    }

    /**
     * key 存在，并且 value 为 true，则返回 true
     */
    fun isTrue(key: K): Boolean {
        val value = get(key)
        return value is Boolean && value as Boolean == true
    }

    /**
     * key 存在，并且 value 为 false，则返回 true
     */
    fun isFalse(key: K): Boolean {
        val value = get(key)
        return value is Boolean && value as Boolean == false
    }

    fun toJson(): String {
        return GsonUtils.toJson(this)
    }

    fun toPrettyJson(): String {
        return GsonUtils.prettyJson(this)
    }

    override fun equals(other: Any?): Boolean {
        return other is KV<*, *> && super.equals(other)
    }

    override fun clone(): KV<K, V> {
        return create<K, V>().set(this)
    }

    companion object {

        fun <K, V> by(key: K, value: V): KV<K, V> {
            val kv: KV<K, V> = KV()
            return kv.set(key, value)
        }

        fun <K, V> create(): KV<K, V> {
            return KV()
        }
    }


}