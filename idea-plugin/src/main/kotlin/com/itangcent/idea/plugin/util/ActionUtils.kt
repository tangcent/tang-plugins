package com.itangcent.idea.plugin.util

import com.intellij.ide.projectView.impl.nodes.ClassTreeNode
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.itangcent.idea.plugin.context.ActionContext
import com.itangcent.idea.plugin.constant.CacheKey
import org.apache.commons.lang.StringUtils

/**
 * Created by TomNg on 2017/2/16.
 */
object ActionUtils {

    fun findCurrentPath(): String? {
        val psiFile = ActionContext.getContext()!!.achieve<PsiFile>(CommonDataKeys.PSI_FILE)
        if (psiFile != null) return findCurrentPath(psiFile)

        val navigatable = ActionContext.getContext()!!.achieve<Navigatable>(CommonDataKeys.NAVIGATABLE)
        if (navigatable != null && navigatable is PsiDirectory) {//select dir
            return findCurrentPath(navigatable)
        }
        val navigatables = ActionContext.getContext()!!.achieve<Array<Navigatable>>(CommonDataKeys.NAVIGATABLE_ARRAY)
        if (navigatables != null) {//select mult dir
            for (node in navigatables) {
                when (navigatable) {
                    is PsiDirectory -> {//select dir
                        return findCurrentPath(navigatable)
                    }
                    is ClassTreeNode -> {
                        return findCurrentPath(navigatable.psiClass.containingFile)
                    }
                    is PsiDirectoryNode -> {
                        return navigatable.element?.value?.let { findCurrentPath(it) }
                    }
                }
            }
        }

        val project = ActionContext.getContext()!!.getCache<Project>(CacheKey.PROJECT)
        if (project != null) {
            return project.basePath
        }

        return null
    }

    fun findCurrentPath(psiFile: PsiFile): String? {
        val dir = psiFile.parent
        return dir?.let { findCurrentPath(it) } + "/" + psiFile.name
    }

    fun findCurrentPath(psiDirectory: PsiDirectory): String? {
        var dirPath = psiDirectory.toString()
        if (dirPath.contains(':')) {
            dirPath = StringUtils.substringAfter(dirPath, ":")
        }
        return dirPath
    }

    fun findCurrentClass(): PsiClass? {
        val actionContext = ActionContext.getContext()!!
        val editor = actionContext.achieve<Editor>(CommonDataKeys.EDITOR) ?: return null
        val psiFile = actionContext.achieve<PsiFile>(CommonDataKeys.PSI_FILE) ?: return null
        var referenceAt = psiFile.findElementAt(editor.caretModel.offset)
        var cls: PsiClass? = null
        try {
            cls = PsiTreeUtil.getContextOfType<PsiElement>(referenceAt, PsiClass::class.java) as PsiClass?
        } catch (e: Exception) {
            //ignore
        }
        if (cls == null) {
            val document = editor.document
            referenceAt = psiFile.findElementAt(DocumentUtils.getInsertIndex(document))
            try {
                cls = PsiTreeUtil.getContextOfType<PsiElement>(referenceAt, PsiClass::class.java) as PsiClass?
            } catch (e: Exception) {
            }
        }
        return cls
    }

    fun format(anActionEvent: AnActionEvent) {
        doAction(anActionEvent, "ReformatCode")
    }

    fun optimize(anActionEvent: AnActionEvent) {
        doAction(anActionEvent, "OptimizeImports")
    }

    fun doAction(anActionEvent: AnActionEvent, action: String) {
        try {
            anActionEvent.actionManager.getAction(action).actionPerformed(anActionEvent)
        } catch (ignored: Exception) {
        }

    }

}
