package com.itangcent.idea.plugin.util

object FieldUtils {

    fun buildFiledName(fieldName: String): String {
        val stringBuilder = StringBuilder(fieldName.length)

        for (ch in fieldName.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                stringBuilder.append("_")
                stringBuilder.append(ch)
            } else {
                stringBuilder.append(Character.toUpperCase(ch))
            }
        }
        return stringBuilder.toString()
    }
}
