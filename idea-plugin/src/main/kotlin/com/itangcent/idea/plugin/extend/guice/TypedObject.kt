package com.itangcent.idea.plugin.extend.guice

import kotlin.reflect.KClass

interface TypedObject<T : Any> {
    fun getType(): KClass<T>

}