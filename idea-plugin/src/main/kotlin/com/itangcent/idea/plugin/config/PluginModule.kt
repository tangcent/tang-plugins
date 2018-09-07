package com.itangcent.idea.plugin.config

import com.itangcent.idea.plugin.auth.AuthProvider
import com.itangcent.idea.plugin.auth.DialogAuthProvider
import com.itangcent.idea.plugin.extend.guice.KotlinModule
import com.itangcent.idea.plugin.extend.guice.singleton
import com.itangcent.idea.plugin.extend.guice.with
import com.itangcent.idea.plugin.git.*
import com.itangcent.idea.plugin.logger.ConsoleRunnerLogger
import com.itangcent.idea.plugin.logger.Logger
import com.itangcent.idea.plugin.parse.JavaParser
import com.itangcent.tang.common.shell.DefaultShellUtils
import com.itangcent.tang.common.shell.ShellUtils

class PluginModule : KotlinModule() {
    override fun configure() {
        super.configure()
        bind(JavaParser::class).singleton()
        bind(Logger::class).with(ConsoleRunnerLogger::class).singleton()
        bind(AuthProvider::class).with(DialogAuthProvider::class).singleton()
        bind(ReleaseMessageInput::class.java).with(DialogReleaseMessageInput::class).singleton()
        bind(ShellUtils::class).with(DefaultShellUtils::class).singleton()
        bind(GitUtils::class).singleton()
        bind(GitRelease::class).with(DefaultGitRelease::class).singleton()
    }
}

