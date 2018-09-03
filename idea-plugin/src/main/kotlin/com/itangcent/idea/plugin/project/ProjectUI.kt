package com.itangcent.idea.plugin.project

import java.util.function.Function

interface ProjectUI {
    fun setProjectName(name: String?)

    fun setAuthor(author: String?)

    fun setTypes(types: Array<String>?)

    fun setUrl(url: String?)

    fun setBranches(branches: Array<String>?)

    fun setDescription(description: String?)

    fun onApply(applyHandle: Function<ProjectInfo, Boolean>?)
}
