package com.itangcent.idea.plugin.git

import com.google.inject.Inject
import com.itangcent.idea.plugin.dialog.ReleaseDialog
import com.itangcent.idea.plugin.logger.Logger
import com.itangcent.idea.plugin.util.UIUtils
import com.itangcent.tang.common.function.ResultHolder
import org.apache.commons.lang.StringUtils
import java.util.function.Function

class DialogReleaseMessageInput : ReleaseMessageInput {

    @Inject
    private val logger: Logger? = null

    override//        ReleaseIIDialog releaseDialog = UIUtils.create(ReleaseIIDialog.class);
    val releaseMessage: Map<String, String>
        get() {
            val resultHolder = ResultHolder<Map<String, String>>()
            val releaseDialog = ReleaseDialog()
            releaseDialog.onResult(Function { authInfo ->
                if (authInfo != null) {
                    if (StringUtils.isBlank(authInfo.get(ReleaseMessageInput.MESSAGE))) {
                        return@Function false
                    }
                    if (StringUtils.isBlank(authInfo.get(ReleaseMessageInput.RELEASENOTES))) {
                        return@Function false
                    }
                }
                resultHolder.setResultVal(authInfo)
                true
            })
            UIUtils.show(releaseDialog)

            return resultHolder.getResultVal()!!
        }
}
