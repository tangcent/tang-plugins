package com.itangcent.idea.plugin.context

import com.itangcent.idea.plugin.extend.guice.KotlinModule
import com.itangcent.idea.plugin.extend.guice.singleton
import com.itangcent.idea.plugin.extend.guice.with
import com.itangcent.idea.plugin.logger.ConsoleRunnerLogger
import com.itangcent.idea.plugin.logger.Logger

@Deprecated("不再使用")
open class BasicPluginModule : KotlinModule() {
    override fun configure() {
        super.configure()
        bind(Logger::class).with(ConsoleRunnerLogger::class).singleton()
    }
}

