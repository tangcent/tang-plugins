package com.itangcent.tang.common.concurrent

import org.apache.commons.lang3.RandomUtils

fun main(args: Array<String>) {

    val countLatch: CountLatch = AQSCountLatch();
    println("start~")
    for (i in 1..100) {
        countLatch.down()
        Thread {
            Thread.sleep(RandomUtils.nextLong(1000, 3000))
            countLatch.up()
            println("$i completed")
        }.start()
    }
    countLatch.waitFor()
    println("all completed")
}