package com.itangcent.idea.plugin.git

import com.google.inject.Inject
import com.itangcent.idea.plugin.auth.AuthProvider
import com.itangcent.idea.plugin.logger.Logger
import com.itangcent.idea.plugin.setting.GitSetting
import com.itangcent.idea.plugin.setting.SettingManager
import org.apache.commons.lang3.exception.ExceptionUtils

class GitHelperConnector {

    @Inject
    private val logger: Logger? = null

    @Inject
    private val authProvider: AuthProvider? = null

    @Inject
    private val settingManager: SettingManager? = null

    fun connect(gitHelper: GitHelper) {
        val gitHost = gitHelper.gitHost


        val gitSetting = settingManager!!.getGitSetting(gitHost!!)
        if (gitSetting != null) {
            try {
                if (doLogin(gitHelper, gitSetting)) {
                    return
                }
            } catch (e: Throwable) {
                logger!!.error("error in doLogin:" + ExceptionUtils.getStackTrace(e))
            }
        }

        for (i in 0..2) {
            if (doLogin(gitHelper)) {
                return
            }
        }
        throw IllegalArgumentException("unable to connect git")

    }


    private fun doLogin(gitHelper: GitHelper, gitSetting: GitSetting): Boolean {
        return gitHelper.connect(gitSetting.privateToken!!)
    }

    private fun doLogin(gitHelper: GitHelper): Boolean {
        try {
            val authInfo = authProvider!!.userInfo
            if (authInfo == null) {
                logger!!.info("cancel login!")
                return false//取消登录
            }
            return gitHelper.connect(authInfo.get(AuthProvider.USERNAME)!!, authInfo.get(AuthProvider.USERNAME)!!)
        } catch (e: Exception) {
            logger!!.info("error to connect git:" + ExceptionUtils.getStackTrace(e))
            return false
        }

    }
}
