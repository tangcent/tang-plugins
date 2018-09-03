package com.itangcent.idea.plugin.dialog

import com.google.common.collect.ImmutableMap
import com.itangcent.idea.plugin.auth.AuthProvider
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.function.Function
import javax.swing.*

class AuthDialog : MappedJDialog() {
    private var contentPane: JPanel? = null
    private var buttonOK: JButton? = null
    private var buttonCancel: JButton? = null
    private var userNameField: JTextField? = null
    private var passwordField: JPasswordField? = null

    init {
        setContentPane(contentPane)
        isResizable = false
        isModal = true
        setLocationRelativeTo(owner)
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


    override fun onResult(resultHandle: Function<Map<String, String>?, Boolean>) {
        this.resultHandle = resultHandle
    }

    override fun collectInfo(): Map<String, String> {
        return ImmutableMap.of(
                AuthProvider.USERNAME, userNameField!!.text,
                AuthProvider.PASSWORD, String(passwordField!!.password)
        )
    }
}
