package com.itangcent.idea.plugin.auth

interface AuthProvider {
    val userInfo: Map<String, String>?

    companion object {
        const val USERNAME = "userName"
        const val PASSWORD = "password"
    }
}
