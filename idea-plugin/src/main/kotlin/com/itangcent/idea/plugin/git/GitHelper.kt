package com.itangcent.idea.plugin.git

import com.google.inject.Inject
import com.itangcent.idea.plugin.logger.Logger
import com.itangcent.tang.common.utils.GsonUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.models.Commit
import org.gitlab4j.api.models.Project
import org.gitlab4j.api.models.User
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Pattern

class GitHelper(val projectHost: String) {
    private var gitLabApi: GitLabApi? = null

    var gitlabProject: Project? = null
        private set

    var gitHost: String? = null
        private set

    private var namespace: String? = null

    var projectName: String? = null
        private set

    val branches: Array<String>?
        get() {
            return try {
                gitlabProject?.let { project ->
                    gitLabApi!!.repositoryApi.getBranches(project.id)
                            ?.stream()
                            ?.map { branch -> branch.name }
                            ?.toArray { i -> Array(i) { "" } }
                }
            } catch (e: Throwable) {
                null
            }

        }

    val userInfo: User?
        get() {
            return try {
                gitLabApi!!.userApi.currentUser
            } catch (e: IOException) {
                null
            }

        }

    @Inject
    private val logger: Logger? = null

    init {
        formatHost(projectHost)
    }

    private fun formatHost(gitHost: String) {

        val matcher = Pattern.compile("(https?://.*?)/(.*?)/(.*?).git").matcher(gitHost)
        if (matcher.matches()) {
            this.gitHost = matcher.group(1)
            this.namespace = matcher.group(2)
            this.projectName = matcher.group(3)
        } else {
            this.gitHost = gitHost
            try {
                val url = URL(gitHost)
                this.gitHost = url.protocol + "://" + url.host
            } catch (e: MalformedURLException) {
                logger!!.info("error to parse git host:" + ExceptionUtils.getStackTrace(e))
            }

        }
    }

    fun connect(privateToken: String): Boolean {
//        logger.info("gitHost:$gitHost,privateToken:$privateToken")
//        gitLabApi = GitlabAPI.connect(gitHost, privateToken, TokenType.PRIVATE_TOKEN)
//        return checkApi()


        try {
            gitLabApi = GitLabApi(gitHost!!, privateToken)
            if (checkApi()) {
                logger!!.info("current version:V4")
                return true
            }
        } catch (e: Throwable) {
        }

        try {
            gitLabApi = GitLabApi(GitLabApi.ApiVersion.V3, gitHost!!, privateToken)
            if (checkApi()) {
                logger!!.info("current version:V3")
                return true
            }
        } catch (e: Throwable) {
        }

        return false
    }

    fun connect(usrName: String, passwd: String): Boolean {
        return when {
            connectV4(usrName, passwd) -> true
            connectV3(usrName, passwd) -> true
            else -> false
        }
    }

    fun connectV3(usrName: String, passwd: String): Boolean {
        try {
            gitLabApi = GitLabApi.oauth2Login(gitHost!!, usrName, passwd as CharSequence)
        } catch (e: IOException) {
            return false
        }

        return checkApi()
    }

    fun connectV4(usrName: String, passwd: String): Boolean {
        try {
            gitLabApi = GitLabApi.oauth2Login(GitLabApi.ApiVersion.V3, gitHost!!, usrName, passwd as CharSequence,
                    null, null, false)
        } catch (e: IOException) {
            return false
        }

        return checkApi()
    }

    private fun checkApi(): Boolean {
        if (gitLabApi == null) {
            return false
        }
        if (namespace != null) {
            try {
//                logger.info("namespace:$namespace,projectName:$projectName")
                gitlabProject = gitLabApi!!.projectApi.getProject(namespace!!, this.projectName!!)
                if (gitlabProject != null) {
                    return true
                }
            } catch (e: Throwable) {
            }
        }

        try {
            val projects = gitLabApi!!.projectApi.projects
            gitlabProject = projects
                    .stream()
                    .filter { it.webUrl == projectHost }
                    .findAny()
                    .orElse(null)
            if (gitlabProject == null) {
                return false
            } else {
                logger!!.info("read project success:" + GsonUtils.toJson(gitlabProject))
                return true
            }
        } catch (e: Throwable) {
            return false
        }

    }

    fun getLastHead(branch: String): Commit? {
        try {

            return (gitLabApi!!.commitsApi.getCommit(gitlabProject!!.getId(), branch))
                    ?: //or throw a exception
                    //                GitlabBranch gitlabBranch = gitLabApi.getBranch(gitlabProject, branch);
                    //                return gitlabBranch.getCommit().getId();
                    return null

        } catch (e: Exception) {
            return null
        }

    }

    fun getHeaders(branch: String, page: Int, perPage: Int): List<Commit>? {
        return try {
            gitLabApi!!.commitsApi.getCommits(gitlabProject!!.getId(), branch, null, null, page, perPage).toList()
        } catch (e: Exception) {
            null
        }

    }

    fun createBranch(head: String, branch: String): Boolean {
        return try {
            gitLabApi!!.repositoryApi.createBranch(this.gitlabProject!!.id, branch, head)
            true
        } catch (e: Exception) {
            false
        }

    }

    fun protectBranch(branch: String): Boolean {
        try {
            gitLabApi!!.protectedBranchesApi.protectBranch(this.gitlabProject!!.id, branch)
            return true
        } catch (e: Exception) {
            return false
        }

    }

    fun deleteBranch(branch: String) {
        try {
            gitLabApi!!.repositoryApi.deleteBranch(gitlabProject!!.id, branch)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

}
