package com.itangcent.idea.plugin.setting

interface SettingManager {

    val tokenSettings: Array<TokenSetting>?

    fun getSetting(host: String?): TokenSetting?

    fun saveGitSetting(tokenSetting: TokenSetting)
}
