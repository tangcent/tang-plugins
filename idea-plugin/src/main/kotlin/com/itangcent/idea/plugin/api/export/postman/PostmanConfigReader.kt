package com.itangcent.idea.plugin.api.export.postman

import com.itangcent.idea.plugin.config.AutoSearchConfigReader
import com.itangcent.idea.plugin.extend.guice.PostConstruct
import java.util.*

class PostmanConfigReader : AutoSearchConfigReader() {

    @PostConstruct
    fun init() {
        loadConfigInfo()
    }

    override fun configFileNames(): List<String> {
        return Arrays.asList(AutoSearchConfigReader.POSTMAN_CONFIG_FILE)
    }
}
