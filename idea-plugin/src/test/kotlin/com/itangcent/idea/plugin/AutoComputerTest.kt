package com.itangcent.idea.plugin

import com.itangcent.idea.plugin.extend.rx.AutoComputer
import com.itangcent.idea.plugin.extend.rx.consistent
import com.itangcent.idea.plugin.extend.rx.from
import com.itangcent.tang.common.utils.GsonUtils
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class AutoComputerTest {

    private var a: String? = null

    private var b: String? = null;

    private var c: Int? = null;

    private var d: Long? = null;

    private var son: AutoComputerTest? = null

    fun test() {
        val autoComputer = AutoComputer()

        autoComputer.bind(this::a)
                .with(this::b)
                .eval { b -> "$b->a" }

        autoComputer.bind(this::b)
                .with(this::c)
                .eval { c -> "$c->b" }

        autoComputer.bind<Long>(this, "son.d")
                .with(this::a)
                .eval { a -> a?.length?.toLong() }

        autoComputer.bind<Int>(this, "son.c")
                .with(this::a)
                .eval { a -> a?.length?.toInt() }

        autoComputer.bind<AutoComputerTest>(this, "son")
                .with(this::d)
                .eval { AutoComputerTest() }

        autoComputer.bind<String>(this, "son.a")
                .from(this::a)

        println("1---->" + GsonUtils.toJson(this))

        autoComputer.value(this::c, 1)
        println("2---->" + GsonUtils.toJson(this))

        this.son = AutoComputerTest()
        autoComputer.value(this::c, 22)
        println("3---->" + GsonUtils.toJson(this))

        autoComputer.value(this::son, AutoComputerTest())
        println("4---->" + GsonUtils.toJson(this))

        autoComputer.value(this::d, 1)
        println("5---->" + GsonUtils.toJson(this))

        autoComputer.value(this::a, "aa")
        println("6---->" + GsonUtils.toJson(this))

        autoComputer.value(this, "this.son.a", "bbb")
        println("7---->" + GsonUtils.toJson(this))

//        println(this::class.memberProperties.first().safeGet(this))
        var a1: KMutableProperty0<String?> = this::a
        var a11: KMutableProperty0<String?> = this::a
        var a2: KProperty1<out AutoComputerTest, Any?> = this::class.memberProperties.first { p -> p.name == "a" }
        println(this::a)
        println(this::class.memberProperties.first { p -> p.name == "a" })

        val action1: () -> Unit = { println("action1") }
        val action2: () -> Unit = { println("action1") }
        var action3: () -> Unit = { println("action3") }
        println(action1 == action2)
    }
}

fun main(args: Array<String>) {
    val autoComputerTest = AutoComputerTest()
    autoComputerTest.test()

}