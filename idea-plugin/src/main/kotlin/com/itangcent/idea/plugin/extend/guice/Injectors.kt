package com.itangcent.idea.plugin.extend.guice

import com.google.inject.*
import com.google.inject.binder.LinkedBindingBuilder
import com.google.inject.binder.ScopedBindingBuilder
import kotlin.reflect.KClass

/**
 * Returns the appropriate instance for the given injection type; equivalent to {@code
 * getProvider(type).get()}. When feasible, avoid using this method, in favor of having Guice
 * inject your dependencies ahead of time.
 *
 * @throws ConfigurationException if this injector cannot find or create the provider.
 * @throws ProvisionException if there was a runtime failure while providing an instance.
 */
fun <T : Any> Injector.instance(kClass: KClass<T>): T {
    return this.getInstance(kClass.java)
}

/**
 * See the EDSL examples at [com.google.inject.Binder].
 *
 * @see com.google.inject.binder.ScopedBindingBuilder.in
 */
fun ScopedBindingBuilder.at(scopeAnnotation: KClass<out Annotation>) {
    return this.`in`(scopeAnnotation.java)
}

/** See the EDSL examples at [com.google.inject.Binder].  */
fun <T : Any> LinkedBindingBuilder<T>.with(implementation: KClass<out T>): ScopedBindingBuilder {
    return this.to(implementation.java)
}

