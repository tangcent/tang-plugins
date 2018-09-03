package com.itangcent.idea.plugin.auth

import com.itangcent.idea.plugin.dialog.AuthDialog
import com.itangcent.idea.plugin.util.UIUtils
import com.itangcent.tang.common.function.ResultHolder
import org.apache.commons.lang.StringUtils
import java.util.function.Function

class DialogAuthProvider : AuthProvider {

    override val userInfo: Map<String, String>?
        get() {
            val resultHolder = ResultHolder<Map<String, String>>()
            val authDialog = AuthDialog()
            authDialog.onResult(Function { authInfo ->
                if (authInfo != null) {
                    if (StringUtils.isBlank(authInfo[AuthProvider.USERNAME])) {
                        return@Function false
                    }
                    if (StringUtils.isBlank(authInfo[AuthProvider.PASSWORD])) {
                        return@Function false
                    }
                }
                resultHolder.setResultVal(authInfo)
                true
            })
            UIUtils.show(authDialog)

            return resultHolder.getResultVal()
        }
}
