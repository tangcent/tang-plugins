package com.itangcent.idea.plugin.project

import com.google.common.collect.ImmutableMap
import com.google.inject.Inject
import com.itangcent.idea.plugin.config.ActionContext
import com.itangcent.idea.plugin.git.GitHelper
import com.itangcent.idea.plugin.git.GitHelperConnector
import com.itangcent.idea.plugin.git.GitUtils
import com.itangcent.idea.plugin.logger.Logger
import com.itangcent.tang.common.exception.ProcessCanceledException
import com.itangcent.tang.common.files.FileFilter
import com.itangcent.tang.common.files.FileHandle
import com.itangcent.tang.common.utils.SystemUtils
import com.itangcent.tang.common.files.DefaultFileTraveler
import org.apache.commons.lang3.ObjectUtils
import java.util.*

class ProjectInfoCollector {

    @Inject
    private val gitUtils: GitUtils? = null

    @Inject
    private val logger: Logger? = null

    @Inject
    private val gitHelperConnector: GitHelperConnector? = null

    @Inject
    private val actionContext: ActionContext? = null

    fun collect(projectUI: ProjectUI, basePath: String) {

//        logger!!.info("projectUI:$projectUI,basePath:$basePath")
        val gitHost = gitUtils!!.getGitUrl(basePath) ?: throw ProcessCanceledException("Not a git project")
        val gitHelper = actionContext!!.instance { GitHelper(gitHost) }
        logger!!.info("start connect to[$gitHost] ...")
        gitHelperConnector!!.connect(gitHelper)


        val gitlabUser = gitHelper.userInfo
        if (gitlabUser != null) {
            projectUI.setAuthor(ObjectUtils.defaultIfNull(gitlabUser.username, gitlabUser.name))
        } else {
            projectUI.setAuthor(SystemUtils.userHome)

        }

        projectUI.setBranches(gitHelper.branches)

        projectUI.setUrl(gitHelper.projectHost)
        projectUI.setProjectName(gitHelper.projectName)

        //todo:读不到东西时，尝试从README.md读
        projectUI.setDescription(gitHelper.gitlabProject?.description)


        val settings = HashSet<String>()
        DefaultFileTraveler(basePath)
                .filter(FileFilter.from { file -> projectTypes.containsKey(file.name()) })
                .onFile(FileHandle.from { file -> projectTypes[file.name()]?.let { settings.add(it) } })
                .travel()
        projectUI.setTypes(settings.toTypedArray())
    }

    companion object {
        private val projectTypes = ImmutableMap.of("pom.xml", "maven", "build.gradle", "gradle")
    }
}
