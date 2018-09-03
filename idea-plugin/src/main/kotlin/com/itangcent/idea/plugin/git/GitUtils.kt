package com.itangcent.idea.plugin.git

import com.google.inject.Inject
import com.itangcent.idea.plugin.logger.Logger
import com.itangcent.tang.common.shell.ShellUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

class GitUtils {
    //endregion shells-------------------------------------------------------------

    @Inject
    private val shellUtils: ShellUtils? = null

    @Inject
    private val logger: Logger? = null

    private val get_files_hasUrl = arrayOf("config", "FETCH_HEAD")

    //region getGitUrl----------------------------------------------------------
    fun getGitUrl(bashPath: String): String? {
        val shs = preShells(bashPath)
        shs.add(remote_v)
        val commandResult = shellUtils!!.execCommand(shs, false)
        val msg = commandResult.msg

        var url = getUrl(msg)

        if (StringUtils.isBlank(url)) {
            val gitDir = findGitDir(bashPath)
            if (StringUtils.isBlank(gitDir)) {
                return null
            }
            url = findUrlInGitDir(gitDir!!)
            if (StringUtils.isNotBlank(url)) {
                if (!url!!.endsWith(GIT_SUFFIX)) {
                    url += GIT_SUFFIX
                }
            }
        }

        return url
    }

    private fun findUrlInGitDir(gitDir: String): String? {
        var url: String? = null
        for (fileName in get_files_hasUrl) {
            url = findUrlInFile("$gitDir/$fileName")
            if (StringUtils.isNotBlank(url)) {
                break
            }
        }
        return url
    }

    private fun findUrlInFile(file: String): String? {
        return try {
            val str = FileUtils.readFileToString(File(file), Charset.defaultCharset())
            getUrl(str)
        } catch (e: IOException) {
            null
        }

    }

    private fun findGitDir(bashPath: String): String? {
        var dir: File? = File(bashPath)
        if (!dir!!.isDirectory) {
            dir = dir.parentFile
        }
        while (dir != null) {
            if (hasGitDir(dir.path))
                return bashPath + GIT_DIR
            dir = dir.parentFile
        }
        return null
    }

    private fun hasGitDir(bashPath: String): Boolean {
        val gitDirPath = bashPath + GIT_DIR
        val file = File(gitDirPath)
        return file.exists() && file.isDirectory
    }

    private fun getUrl(str: String): String? {

        if (StringUtils.isBlank(str)) {
            return null
        }

        //获取完整的域名
        val httpPattern = Pattern.compile("\\s(https?\\S*)\\s", Pattern.CASE_INSENSITIVE)
        val httpMatcher = httpPattern.matcher(str)
        if (httpMatcher.find()) {
            return httpMatcher.group(1)
        }

        val sshPattern = Pattern.compile("\\sgit@(\\S*):(\\S*)\\s", Pattern.CASE_INSENSITIVE)
        val sshMatcher = sshPattern.matcher(str)
        if (sshMatcher.find()) {
            val host = sshMatcher.group(1)
            val path = sshMatcher.group(2)
            return String.format("http://%s/%s", host, path)
        }
        return null
    }
    //endregion getGitUrl----------------------------------------------------------

    fun getCurrHeader(bashPath: String): String? {
        val shs = preShells(bashPath)
        shs.add(show_log)
        val commandResult = shellUtils!!.execCommand(shs, false)
        val msg = commandResult.msg

        if (StringUtils.isNotBlank(msg)) {
            //获取第一条header 即为当前分支的header
            val p = Pattern.compile("commit\\s(\\S*)\\s", Pattern.CASE_INSENSITIVE)
            val matcher = p.matcher(msg)
            if (matcher.find()) {
                return matcher.group(1)
            }
        }


        return findCurrHeaderByFiel(bashPath)
    }


    private fun findCurrHeaderByFiel(bashPath: String): String? {

        try {
            val gitDir = findGitDir(bashPath) ?: return null

            val headFile = File("$gitDir/HEAD")
            if (!headFile.exists() || !headFile.isFile) {
                return null
            }

            var branch = getBranchFromHead(gitDir)
            if (branch == null) {
                branch = getBranchFromFetchHead(gitDir)
            }
            return if (branch == null) {
                null
            } else readHeaderByBranch(gitDir, branch)

        } catch (e: Exception) {
            return null
        }

    }

    private fun getBranchFromHead(gitDir: String): String? {
        try {
            val headFile = File("$gitDir/HEAD")
            if (!headFile.exists() || !headFile.isFile) {
                return null
            }

            val headContent = FileUtils.readFileToString(headFile, Charset.defaultCharset())
            val p = Pattern.compile("refs/heads/(\\S*)", Pattern.CASE_INSENSITIVE)
            val matcher = p.matcher(headContent)
            return if (matcher.find()) {
                matcher.group(1)
            } else null
        } catch (e: IOException) {
            return null
        }

    }

    private fun getBranchFromFetchHead(gitDir: String): String? {
        try {
            val headFile = File("$gitDir/FETCH_HEAD")
            if (!headFile.exists() || !headFile.isFile) {
                return null
            }

            val headContent = FileUtils.readFileToString(headFile, Charset.defaultCharset())
            val p = Pattern.compile("branch\\s*'(\\S*)'\\s", Pattern.CASE_INSENSITIVE)
            val matcher = p.matcher(headContent)
            return if (matcher.find()) {
                matcher.group(1)
            } else null
        } catch (e: IOException) {
            return null
        }

    }

    private fun readHeaderByBranch(gitDir: String, branch: String): String? {
        val headerFile = "$gitDir/refs/heads/$branch"
        try {
            val headContent = FileUtils.readFileToString(File(headerFile), Charset.defaultCharset())
            return StringUtils.trimToNull(headContent)
        } catch (e: IOException) {
            return null
        }

    }

    private fun preShells(localPath: String): MutableList<String> {
        val shs = ArrayList<String>()
        shs.add("cd $localPath")
        return shs
    }

    companion object {

        //region shells-------------------------------------------------------------
        private val remote_v = "git remote -v"
        private val show_log = "git log"

        private val GIT_DIR = "/.git"
        private val GIT_SUFFIX = ".git"
    }
}
