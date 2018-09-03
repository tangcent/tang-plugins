package com.itangcent.idea.plugin.setting

import com.google.inject.ImplementedBy

@ImplementedBy(DefaultSettingManager::class)
interface SettingManager {

    val gitSettings: Array<GitSetting>?

    fun getGitSetting(host: String?): GitSetting?

    fun saveGitSetting(gitSetting: GitSetting)
}
