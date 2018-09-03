package com.itangcent.idea.plugin.dialog


import com.google.common.collect.ImmutableMap
import com.itangcent.idea.plugin.git.ReleaseMessageInput
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

class ReleaseDialog : MappedJDialog() {
    private var contentPane: JPanel? = null
    private var buttonOK: JButton? = null
    private var buttonCancel: JButton? = null
    private var messageField: JTextField? = null
    private var releaseNoteField: JTextArea? = null
    private var messageLabel: JLabel? = null
    private var releasNoteLabel: JLabel? = null

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
    }

    internal override fun collectInfo(): Map<String, String> {
        return ImmutableMap.of(ReleaseMessageInput.MESSAGE, messageField!!.text,
                ReleaseMessageInput.RELEASENOTES, releaseNoteField!!.text)
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val dialog = ReleaseDialog()
            dialog.pack()
            dialog.isVisible = true
            System.exit(0)
        }
    }
}
