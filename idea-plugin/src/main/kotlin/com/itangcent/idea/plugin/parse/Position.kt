package com.itangcent.idea.plugin.parse

class Position private constructor(val start: Long, val end: Long) {

    fun contain(offset: Long): Boolean {
        return offset in start..end
    }

    companion object {

        fun of(start: Long, end: Long): Position {
            return Position(start, end)
        }
    }
}

