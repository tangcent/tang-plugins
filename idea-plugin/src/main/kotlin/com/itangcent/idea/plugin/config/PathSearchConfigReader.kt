package com.itangcent.idea.plugin.config

import com.google.inject.Inject
import com.itangcent.idea.plugin.logger.Logger
import com.itangcent.idea.plugin.util.ActionUtils
import java.io.File

abstract class PathSearchConfigReader : AbstractConfigReader() {

    @Inject(optional = true)
    private val logger: Logger? = null

    protected fun searchConfigFiles(configFileName: String): List<String>? {

        val configFiles: ArrayList<String> = ArrayList()

        var currentPath = ActionUtils.findCurrentPath()

        while (!currentPath.isNullOrBlank()) {
            val path = "$currentPath/$configFileName"

            val configFile = File(path)
            if (configFile.exists() && configFile.isFile) {
                logger?.info("find config file:$path")
                configFiles.add(path)
            }
            if (currentPath.isNullOrBlank() || !currentPath!!.contains("/")) {
                break
            }
            currentPath = currentPath!!.substringBeforeLast("/")
        }

        return configFiles
    }
}
