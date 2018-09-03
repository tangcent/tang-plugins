package com.itangcent.idea.plugin.git

import com.google.inject.ImplementedBy

@ImplementedBy(DefaultGitRelease::class)
interface GitRelease {
    fun release(basePath: String)
}
