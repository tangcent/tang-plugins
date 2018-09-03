package com.itangcent.idea.plugin.config

import com.google.inject.Guice
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.itangcent.idea.plugin.constant.CacheKey
import com.itangcent.idea.plugin.extend.guice.KotlinModule
import com.itangcent.idea.plugin.extend.guice.instance
import com.itangcent.tang.common.concurrent.AQSCountLatch
import com.itangcent.tang.common.concurrent.CountLatch
import com.itangcent.tang.common.utils.IDUtils
import com.itangcent.tang.common.utils.ThreadPoolUtils
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KClass

/**
 * Action上下文
 * 包含一个guice的injector，来管理所有生成的实例
 * 包含一个CountLatch，来维持子进程状态
 */
class ActionContext {

    private val cache = HashMap<String, Any>()

    private val lock = ReentrantLock()

    private val id = IDUtils.shortUUID()

    @Volatile
    private var locked = false

    private var countLatch: CountLatch = AQSCountLatch()

    private var executorService: ExecutorService = ThreadPoolUtils.createPool(5, ActionContext::class.java)

    //使用guice管理当前上下文实例生命周期与依赖
    private var injector = Guice.createInjector(PluginModule(), ContextModule(this))!!

    class ContextModule(var context: ActionContext) : KotlinModule() {
        override fun configure() {
            bindInstance(context)
        }
    }

    //region cache--------------------------------------------------------------
    fun cache(name: String, bean: Any) {
        lock.withLock { cache.put(cachePrefix + name, bean) }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getCache(name: String): T? {
        return lock.withLock { cache[cachePrefix + name] as T? }
    }
    //endregion cache--------------------------------------------------------------

    //region event--------------------------------------------------------------
    @Suppress("UNCHECKED_CAST")
    fun on(name: String, event: Runnable) {
        lock.withLock {
            val key = eventPrefix + name
            val oldEvent: Runnable? = cache[key] as Runnable?
            if (oldEvent == null) {
                cache[key] = event
            } else {
                cache[key] = Runnable {
                    oldEvent.run()
                    event.run()
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun call(name: String) {
        lock.withLock {
            val event = cache[eventPrefix + name] as Runnable?
            event?.run()
        }
    }
    //endregion event--------------------------------------------------------------

    //region lock and run----------------------------------------------------------------

    //锁住缓存
    fun lock(): Boolean = lock.withLock {
        return if (locked) {
            false
        } else {
            locked = true
            ActionContext.setContext(this)
            true
        }
    }

    fun hold() {
        countLatch.down()
    }

    fun unHold() {
        countLatch.up()
    }

    fun runAsync(runnable: Runnable) {
        countLatch.down()
        executorService.submit {
            try {
                ActionContext.setContext(this)
                runnable.run()
            } finally {
                ActionContext.clearContext()
                countLatch.up()
            }
        }
    }

    fun runAsync(runnable: () -> Unit) {
        countLatch.down()
        executorService.submit {
            try {
                ActionContext.setContext(this)
                runnable()
            } finally {
                ActionContext.clearContext()
                countLatch.up()
            }
        }
    }

    /**
     * todo:判断当前线程是否为UI线程
     */
    fun runInUi(runnable: Runnable) {
        val project = this.getCache<Project>(CacheKey.PROJECT)
        countLatch.down()
        WriteCommandAction.runWriteCommandAction(project) {
            try {
                ActionContext.setContext(this)
                runnable.run()
            } finally {
                ActionContext.clearContext()
                countLatch.up()
            }
        }
    }

    /**
     * todo:判断当前线程是否为UI线程
     */
    fun runInUi(runnable: () -> Unit) {
        val project = this.getCache<Project>(CacheKey.PROJECT)
        countLatch.down()
        WriteCommandAction.runWriteCommandAction(project) {
            try {
                ActionContext.setContext(this)
                runnable()
            } finally {
                ActionContext.clearContext()
                countLatch.up()
            }
        }
    }

    fun runInReadUi(runnable: () -> Unit) {
        countLatch.down()
        ReadAction.run<Throwable> {
            try {
                ActionContext.setContext(this)
                runnable()
            } finally {
                ActionContext.clearContext()
                countLatch.up()
            }
        }
    }

    /**
     * 等待完成
     * warning:调用waitComplete*方法将清除当前线程绑定的ActionContext
     * todo:如果在UI线程上等待，可能导致@{runInUi}启动的任务无法完成，造成假死
     * @see ActionContext.waitCompleteAsync
     */
    @Deprecated("不可调用，直到UI线程能够得到有效的处理")
    fun waitComplete() {
        ActionContext.clearContext()
        this.countLatch.waitFor()
        this.call(CacheKey.ONCOMPLETED)
        lock.withLock {
            //            this.cache.keys.removeIf { key -> key.startsWith(eventPrefix) }
            this.cache.clear()
            locked = false
        }
    }

    /**
     * 主线程完成,在异步线程上等待完成
     * warning:调用waitComplete*方法将清除当前线程绑定的ActionContext
     * @see ActionContext.waitComplete
     */
    fun waitCompleteAsync() {
        ActionContext.clearContext()
        executorService.submit {
            this.countLatch.waitFor()
            this.call(CacheKey.ONCOMPLETED)
            lock.withLock {
                //            this.cache.keys.removeIf { key -> key.startsWith(eventPrefix) }
                this.cache.clear()
                locked = false
            }
        }
    }
    //endregion lock and run----------------------------------------------------------------

    //region content object-----------------------------------------------------
    fun <T : Any> instance(kClass: KClass<T>): T {
        return this.injector.instance(kClass)
    }

    fun <T : Any> instance(init: () -> T): T {
        val obj: T = init()
        this.injector.injectMembers(obj)
        return obj
    }

    fun <T : Any> init(obj: T): T {
        this.injector.injectMembers(obj)
        return obj
    }


    //endregion content object-----------------------------------------------------

    //region equals|hashCode|toString-------------------------------------------
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ActionContext

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "ActionContext('$id')"
    }
    //endregion equals|hashCode|toString-------------------------------------------

    companion object {
        private const val cachePrefix = "cache_"
        private const val eventPrefix = "event_"

        private var contextHandle: ThreadLocal<ActionContext> = ThreadLocal()

        /**
         * 获得当前线程上下文
         */
        public fun getContext(): ActionContext? {
            return contextHandle.get()
        }

        private fun setContext(actionContext: ActionContext) {
            contextHandle.set(actionContext)
        }

        private fun clearContext() {
            contextHandle.remove()
        }

        /**
         * 声明一个本地代理对象，它将在使用时从使用它的线程中获取上下文中的此类型的相应对象
         */
        public inline fun <reified T : Any> local() = ThreadLocalContextBeanProxies.instance(T::class)

        public fun <T : Any> instance(clazz: KClass<T>): T {
            return ThreadLocalContextBeanProxies.instance(clazz)
        }
    }
}
