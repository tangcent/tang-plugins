package com.itangcent.tang.common.utils

import java.io.Closeable

object ExecuteUtils {

    fun closeIfPossible(vararg closeables: Closeable?) {
        for (closeable in closeables) {
            closeable?.close()
        }
    }

}
