package com.itangcent.idea.plugin.dialog

import com.intellij.openapi.ui.Messages
import com.itangcent.idea.plugin.project.ProjectInfo
import com.itangcent.idea.plugin.project.ProjectUI
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import java.awt.event.*
import java.util.function.Function

class ProjectDialog : JDialog(), ProjectUI {
    private var contentPane: JPanel? = null
    private var buttonOK: JButton? = null
    private var buttonCancel: JButton? = null
    private var author_textField: JTextField? = null
    private var name_textField: JTextField? = null
    private var url_textField: JTextField? = null
    private var description_textArea: JTextArea? = null
    private var branch_comboBox: JComboBox<String>? = null
    private var type_comboBox: JComboBox<String>? = null

    private var applyHandle: Function<ProjectInfo, Boolean>? = null

    init {
        setContentPane(contentPane)
        isResizable = false
        isModal = true
        getRootPane().defaultButton = buttonOK

        buttonOK!!.addActionListener { onOK() }

        buttonCancel!!.addActionListener { onCancel() }

        // call onCancel() when cross is clicked
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                onCancel()
            }
        })

        // call onCancel() on ESCAPE
        contentPane!!.registerKeyboardAction({ onCancel() }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)

        setLocationRelativeTo(owner)
    }

    private fun onOK() {
        try {
            if (submit()) {
                Messages.showMessageDialog(this, "Apply success",
                        "Error", Messages.getErrorIcon())
                dispose()
            } else {
                Messages.showMessageDialog(this, "Apply failed",
                        "Error", Messages.getErrorIcon())
            }
        } catch (e: IllegalArgumentException) {
            Messages.showMessageDialog(this, StringUtils.defaultIfEmpty<String>(e.message, "Apply failed"),
                    "Error", Messages.getErrorIcon())
        }

    }

    private fun onCancel() {
        // add your code here if necessary
        dispose()
    }

    private fun createUIComponents() {
        // TODO: place custom component creation code here
    }

    override fun setAuthor(author: String?) {
        SwingUtilities.invokeLater { author_textField!!.text = author }
    }

    override fun setTypes(types: Array<String>?) {
        SwingUtilities.invokeLater {
            val typeModel: ComboBoxModel<String> = DefaultComboBoxModel(types!!)
            type_comboBox?.setModel(typeModel)
        }
    }

    override fun setUrl(url: String?) {
        SwingUtilities.invokeLater { url_textField!!.text = url }

    }

    override fun setBranches(branches: Array<String>?) {
        SwingUtilities.invokeLater {
            val branchModel = DefaultComboBoxModel(branches!!)
            branch_comboBox!!.setModel(branchModel)
        }
    }

    override fun setDescription(description: String?) {
        SwingUtilities.invokeLater { description_textArea!!.text = description }
    }

    override fun onApply(applyHandle: Function<ProjectInfo, Boolean>?) {
        this.applyHandle = applyHandle
    }

    private fun submit(): Boolean {
        if (applyHandle != null) {
            val projectInfo = ProjectInfo()

            projectInfo.author = author_textField!!.text

            projectInfo.branch = branch_comboBox!!.selectedItem.toString()

            projectInfo.description = description_textArea!!.text

            projectInfo.name = name_textField!!.text

            projectInfo.type = type_comboBox!!.selectedItem.toString()

            projectInfo.url = url_textField!!.text

            return applyHandle!!.apply(projectInfo)
        }
        return false
    }

    override fun setProjectName(name: String?) {
        SwingUtilities.invokeLater { name_textField!!.text = name }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val dialog = ProjectDialog()
            dialog.pack()
            dialog.isVisible = true
            System.exit(0)
        }
    }
}
