package com.itangcent.idea.plugin.config

import com.google.inject.Singleton
import com.itangcent.idea.plugin.auth.AuthProvider
import com.itangcent.idea.plugin.auth.DialogAuthProvider
import com.itangcent.idea.plugin.extend.guice.KotlinModule
import com.itangcent.idea.plugin.extend.guice.at
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
        bind(JavaParser::class).at(Singleton::class)
        bind(Logger::class).with(ConsoleRunnerLogger::class).at(Singleton::class)
        bind(AuthProvider::class).with(DialogAuthProvider::class).at(Singleton::class)
        bind(ReleaseMessageInput::class.java).with(DialogReleaseMessageInput::class).at(Singleton::class)
        bind(ShellUtils::class).with(DefaultShellUtils::class).at(Singleton::class)
        bind(GitUtils::class).at(Singleton::class)
        bind(GitRelease::class).with(DefaultGitRelease::class).at(Singleton::class)
    }
}

