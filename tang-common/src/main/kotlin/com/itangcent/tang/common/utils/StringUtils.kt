package com.itangcent.tang.common.utils

/**
 * @author TomNg
 */
object StringUtils {

    fun firstCharacter(charSequence: CharSequence): Int {
        for (i in 0 until charSequence.length) {
            if (charSequence[i] > ' ') {
                return i
            }
        }
        return -1
    }
}
