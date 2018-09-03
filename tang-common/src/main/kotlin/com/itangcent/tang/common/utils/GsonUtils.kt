package com.itangcent.tang.common.utils

import com.google.gson.Gson
import kotlin.reflect.KClass

/**
 *
 * @author TomNg
 */
object GsonUtils {
    private val gson = Gson()

    fun toJson(any: Any?): String {
        return gson.toJson(any)
    }

    fun <T> fromJson(json: String, cls: Class<T>): T {
        return gson.fromJson(json, cls)
    }

    fun <T : Any> fromJson(json: String, cls: KClass<T>): T {
        return gson.fromJson(json, cls.java)
    }
}
