package com.itangcent.idea.plugin.git

interface ReleaseMessageInput {

    val releaseMessage: Map<String, String>?

    companion object {

        val MESSAGE = "Message"
        val RELEASENOTES = "ReleaseNotes"
    }
}
