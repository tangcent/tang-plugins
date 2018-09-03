package com.itangcent.tang.common.concurrent

import java.util.concurrent.TimeUnit

interface CountLatch {

    /**
     * 等待计数全部释放
     */
    @Throws(InterruptedException::class)
    fun waitFor()

    /**
     * 尝试等待计数全部释放
     */
    @Throws(InterruptedException::class)
    fun waitFor(msTimeout: Long): Boolean


    /**
     * 计数-1
     */
    fun down()

    /**
     * 计数+1
     */
    fun up()

    fun count(): Int
}