package com.itangcent.idea.plugin.git

import com.google.inject.Inject
import com.itangcent.idea.plugin.auth.AuthProvider
import com.itangcent.idea.plugin.logger.Logger
import com.itangcent.idea.plugin.setting.GitSetting
import com.itangcent.idea.plugin.setting.SettingManager
import com.itangcent.tang.common.utils.DateUtils
import com.itangcent.tang.common.utils.GsonUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.GitLabApiException
import org.gitlab4j.api.models.Project
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class DefaultGitRelease : GitRelease {

    @Inject
    private val logger: Logger? = null

    @Inject
    private val authProvider: AuthProvider? = null

    @Inject
    private val releaseMessageInput: ReleaseMessageInput? = null

    @Inject
    private val settingManager: SettingManager? = null

    @Inject
    private val gitUtils: GitUtils? = null

    override fun release(basePath: String) {
        ReleaseExecutor(basePath).release()
    }

    /**
     * release_v_yyyyMMddHHmmssSSS
     *
     * @return a new version
     */
    private fun buildNewVersion(): String {
        return "release_v_" + DateUtils.format(Date(), "yyyyMMddHHmmssSSS")
    }

    private inner class ReleaseExecutor(private val basePath: String) {

        private var gitlabAPI: GitLabApi? = null

        private var gitlabProject: Project? = null

        private var gitHost: String? = null

        private var projectHost: String? = null


        fun release() {

            projectHost = gitUtils!!.getGitUrl(basePath)

            if (StringUtils.isEmpty(projectHost)) {
                logger!!.info("error to find git host")
                return
            } else {
                logger!!.info("git host:" + projectHost!!)
            }

            gitHost = formatHost(projectHost!!)
            if (!login()) {
                logger.info("login failed")
                return
            }

            addNewTag()
        }

        private fun createNewBranch() {
            val currHeader = gitUtils!!.getCurrHeader(basePath)
            logger!!.info("curr header:" + currHeader!!)
            try {
                gitlabAPI!!.repositoryApi.createBranch(gitlabProject!!.id, buildNewVersion(), currHeader)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        private fun addNewTag() {
            val currHeader = gitUtils!!.getCurrHeader(basePath)
            logger!!.info("curr header:" + currHeader!!)
            try {
                logger.info("please input release info in the opened dialog")
                val releaseMessage = releaseMessageInput!!.releaseMessage
                val message: String?
                val note: String?

                if (releaseMessage != null) {
                    message = releaseMessage[ReleaseMessageInput.MESSAGE]
                    note = releaseMessage[ReleaseMessageInput.RELEASENOTES]
                } else {
                    message = "release in " + DateUtils.formatYMD_HMS(Date())
                    note = "$message automatically"
                }
                logger.info("release with:[message:$message,note:$note]")
                gitlabAPI!!.tagsApi.createTag(gitlabProject!!.id, buildNewVersion(), currHeader, message, note)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        private fun login(): Boolean {
            val gitSetting = settingManager!!.getGitSetting(this.gitHost!!)
            if (gitSetting != null) {
                doLogin(gitSetting)
                if (checkApi()) {
                    return true
                }
            }

            for (i in 0..2) {
                if (doLogin() != 401) {
                    if (!checkApi()) {
                        continue
                    }
                    return true
                }
            }
            return false
        }

        private fun checkApi(): Boolean {
            if (gitlabAPI == null) {
                return false
            }
            try {
                val projects = gitlabAPI!!.projectApi.projects
                gitlabProject = projects
                        .stream()
                        .filter { p -> p.webUrl == projectHost }
                        .findAny()
                        .orElse(null)
                return if (gitlabProject == null) {
                    false
                } else {
                    logger!!.info("read project success:" + GsonUtils.toJson(gitlabProject))
                    true
                }
            } catch (e: Exception) {
                logger!!.error("error in check git response:" + ExceptionUtils.getStackTrace(e))
                return false
            }

        }

        private fun doLogin(gitSetting: GitSetting) {
            gitlabAPI = GitLabApi(gitHost, gitSetting.privateToken)
        }

        private fun formatHost(gitHost: String): String {

            var host = gitHost
            try {
                val url = URL(gitHost)
                host = url.protocol + "://" + url.host
            } catch (e: MalformedURLException) {
                logger!!.info("error to parse git host:" + ExceptionUtils.getStackTrace(e))
            }

            return host
        }

        private fun doLogin(): Int {
            try {
                val authInfo = authProvider!!.userInfo
                if (authInfo == null) {
                    logger!!.info("cancel login!")
                    return -1//取消登录
                }
                gitlabAPI = GitLabApi.oauth2Login(gitHost, authInfo.get(AuthProvider.USERNAME), authInfo.get(AuthProvider.USERNAME) as CharSequence)


                return 1
            } catch (e: GitLabApiException) {
                if (e.httpStatus == 401) {
                    e.printStackTrace()
                    logger!!.info("login failed!")
                    return 401//登录失败
                }
                return 0
            } catch (e: IOException) {
                logger!!.info("error to connect git:" + ExceptionUtils.getStackTrace(e))
                return 0
            }

        }
    }
}
