package com.itangcent.idea.plugin

import com.itangcent.common.utils.GsonUtils
import java.util.*

fun main(args: Array<String>) {
    val map = HashMap<String, String>()
    for (i in 0..10) {
        map[i.toString()] = i.toString()
        println("$i = [${i.toString().hashCode()}]")

    }
    println(GsonUtils.toJson(map))
}
