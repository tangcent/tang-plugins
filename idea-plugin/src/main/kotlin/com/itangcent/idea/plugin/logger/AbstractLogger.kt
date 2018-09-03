package com.itangcent.idea.plugin.logger

import com.itangcent.idea.plugin.util.Utils

abstract class AbstractLogger : Logger {

    protected abstract fun processLog(logData: String?)

    override fun log(level: String?, msg: String) {
        try {
            var formatMsg: String?

            if (org.apache.commons.lang3.StringUtils.isEmpty(level)) {
                formatMsg = msg + Utils.newLine()
            } else {
                formatMsg = "[" + level + "]\t" + msg + Utils.newLine()
            }
            processLog(formatMsg)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }
}
