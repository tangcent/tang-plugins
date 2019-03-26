package com.itangcent.idea.plugin.config

import com.google.inject.Inject
import com.intellij.util.containers.isNullOrEmpty
import com.itangcent.idea.plugin.logger.Logger
import java.util.*

abstract class AutoSearchConfigReader : PathSearchConfigReader() {

    @Inject(optional = true)
    private val logger: Logger? = null

    abstract fun configFileNames(): List<String>

    override fun findConfigFiles(): List<String>? {
        configFileNames().forEach { configFileName ->
            val configFiles = searchConfigFiles(configFileName)
            if (configFiles.isNullOrEmpty()) {
                logger?.info("No config [$configFileName] be found")
            } else {
                return configFiles
            }
        }
        return Collections.emptyList()
    }

    companion object {
        val POSTMAN_CONFIG_FILE = ".postman.config"
    }
}
