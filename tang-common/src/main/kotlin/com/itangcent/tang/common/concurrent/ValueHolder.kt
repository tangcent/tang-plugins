package com.itangcent.tang.common.concurrent

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ValueHolder<T> {

    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    @Volatile
    var data: Any? = INIT_DATA
        get() {
            if (field == INIT_DATA) {
                lock.withLock {
                    while (field == INIT_DATA) {
                        condition.await()
                    }
                }
            }
            return field
        }
        set(value) {
            lock.withLock {
                field = value
                condition.signalAll()
            }
        }

    companion object {
        private val INIT_DATA: Any = Object()
    }
}
