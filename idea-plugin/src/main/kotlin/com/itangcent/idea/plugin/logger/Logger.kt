package com.itangcent.idea.plugin.logger

public interface Logger {
    fun log(msg: String) {
        log(null, msg)
    }

    fun log(level: String?, msg: String)

    fun info(msg: String) {
        log("INFO", msg)
    }

    fun error(msg: String) {
        log("ERROR", msg)
    }
}
