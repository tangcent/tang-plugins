package com.itangcent.idea.plugin.setting

import com.google.inject.Inject
import com.itangcent.idea.plugin.logger.Logger
import com.itangcent.tang.common.utils.GsonUtils
import com.itangcent.tang.common.utils.SystemUtils
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import kotlin.streams.toList

class DefaultSettingManager : SettingManager {

    @Inject
    private val logger: Logger? = null

    private var settingRepository: SettingRepository? = null

    private val repositoryFile: File
        @Synchronized get() {

            var home = SystemUtils.userHome
            if (home.endsWith("/")) {
                home = home.substring(0, home.length - 1)
            }
            val repositoryFile = "$home/.tm/tm.settings"
            val file = File(repositoryFile)

            if (!file.exists()) {
                try {
                    FileUtils.forceMkdirParent(file)
                    if (!file.createNewFile()) {
                        logger!!.error("error to create new setting file")
                    }
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }

            }
            return file
        }

    private val repository: SettingRepository
        get() {
            if (settingRepository == null) {
                init()
            }

            if (settingRepository == null) {
                settingRepository = SettingRepository()
                settingRepository!!.gitSettings = ArrayList()
            }
            return settingRepository as SettingRepository
        }

    override val gitSettings: Array<GitSetting>?
        get() = repository.gitSettings?.toTypedArray()

    @Synchronized
    private fun init() {
        if (settingRepository == null) {
            try {
                val str = FileUtils.readFileToString(repositoryFile, Charset.defaultCharset())
                settingRepository = GsonUtils.fromJson(str, SettingRepository::class)
            } catch (e: Exception) {
                logger!!.error("error init settingRepository:" + ExceptionUtils.getStackTrace(e))
            }

        }
    }

    private fun saveRepository(settingRepository: SettingRepository) {
        try {
            FileUtils.write(repositoryFile, GsonUtils.toJson(settingRepository), Charset.defaultCharset())
        } catch (e: IOException) {
            logger!!.error("error save settingRepository:" + ExceptionUtils.getStackTrace(e))
        }

    }

    override fun getGitSetting(host: String?): GitSetting? {
        val settingRepository = repository
        val gitSettings = settingRepository.gitSettings
        return if (CollectionUtils.isEmpty(gitSettings)) {
            null
        } else gitSettings!!
                .stream()
                .filter { gitSetting -> gitSetting.host == host }
                .findAny()
                .orElse(null)
    }

    override fun saveGitSetting(gitSetting: GitSetting) {

        if (StringUtils.isBlank(gitSetting.host)) {
            return
        }
        val settingRepository = repository
        var gitSettings: MutableList<GitSetting>? = settingRepository.gitSettings as MutableList<GitSetting>?
        if (CollectionUtils.isEmpty(gitSettings)) {
            gitSettings = ArrayList()
        } else {
            gitSettings = gitSettings!!
                    .stream()
                    .filter { gs -> gs.host != gitSetting.host }
                    .toList() as MutableList<GitSetting>?
        }
        if (StringUtils.isNotBlank(gitSetting.privateToken)) {
            gitSettings!!.add(gitSetting)
        }
        settingRepository.gitSettings = gitSettings
        saveRepository(settingRepository)
    }
}
