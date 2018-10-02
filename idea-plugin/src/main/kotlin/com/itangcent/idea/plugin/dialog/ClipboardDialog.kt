package com.itangcent.idea.plugin.dialog

import com.google.inject.Inject
import com.itangcent.idea.plugin.clipboard.ClipboardData
import com.itangcent.idea.plugin.clipboard.ClipboardManager
import com.itangcent.idea.plugin.config.ActionContext
import com.itangcent.idea.plugin.extend.guice.PostConstruct
import com.itangcent.idea.plugin.extend.rx.AutoComputer
import com.itangcent.idea.plugin.extend.rx.from
import com.itangcent.idea.plugin.extend.rx.option
import com.itangcent.idea.plugin.logger.Logger
import org.apache.commons.lang3.exception.ExceptionUtils
import org.jdesktop.swingx.prompt.PromptSupport
import java.awt.EventQueue
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import javax.swing.*
import kotlin.streams.toList

class ClipboardDialog : JDialog() {
    private var contentPane: JPanel? = null

    private var title_list: JList<*>? = null
    private var title_textField: JTextField? = null
    private var content_textArea: JTextArea? = null
    private var content_scrollPane: JScrollPane? = null

    private var save_button: JButton? = null
    private var remove_button: JButton? = null
    private var datas: MutableList<ClipboardData>? = null

    private var selectedClipboardData: ClipboardData? = null

    private val editedClipboardData: ClipboardData = ClipboardData()

//    private val rxSwingBinders: RxSwingBinders by lazy { RxSwingBinders() }

    private val autoComputer: AutoComputer = AutoComputer()

    @Inject
    val clipboardManager: ClipboardManager? = null

    @Inject
    val logger: Logger? = null

    @Inject
    val actionContext: ActionContext? = null

    init {

        setContentPane(contentPane)
        isModal = false

        PromptSupport.setPrompt("title...", title_textField);
        PromptSupport.setPrompt("content...", content_textArea);
        PromptSupport.setFocusBehavior(PromptSupport.FocusBehavior.HIGHLIGHT_PROMPT, title_textField);
//        PromptSupport.setFocusBehavior(PromptSupport.FocusBehavior.HIGHLIGHT_PROMPT, content_textArea);

        // call onCancel() when cross is clicked
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                onCancel()
            }
        })

        // call onCancel() on ESCAPE
        contentPane!!.registerKeyboardAction({ onCancel() }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)

//        title_list!!.addMouseListener(object : MouseAdapter() {
//            override fun mouseClicked(e: MouseEvent?) {
//                //已选项的下标
//                title_list?.selectedIndex?.let { selectData(it) }
//            }
//        })

        save_button!!.addActionListener { saveData() }
        remove_button!!.addActionListener { removeData() }

        this.addWindowFocusListener(object : WindowFocusListener {
            override fun windowLostFocus(e: WindowEvent?) {
                onCancel()
            }

            override fun windowGainedFocus(e: WindowEvent?) {
            }
        })

        setLocationRelativeTo(owner)

    }

    @PostConstruct
    fun postConstruct() {
        actionContext!!.hold()

//        autoComputer.listenOn { EventQueue.invokeLater { it() } }

        //title/content不为空时,save_button可用
        autoComputer.bindEnable(save_button!!)
                .with(title_textField!!)
                .with(content_textArea!!)
                .eval { title, content ->
                    (title!!.length + content!!.length) != 0
                }

        //根据标题列被选中的标婷更新selectedClipboardData
        autoComputer.bind(this::selectedClipboardData)
                .withIndex(title_list!!)
                .eval {
                    when (it) {
                        null, -1, datas!!.size -> null
                        else -> this.datas!![it]
                    }
                }

        //selectedClipboardData不为空时,remove_button可用
        autoComputer.bindEnable(remove_button!!)
                .with(this::selectedClipboardData)
                .eval { it != null }

        //selectedClipboardData不为空时,title_textField为selectedClipboardData的title
        autoComputer.bind(title_textField!!).from<String?>(this, "this.selectedClipboardData.title")

        //selectedClipboardData不为空时,title_textField为selectedClipboardData的title
        autoComputer.bind(content_textArea!!)
                .with<String?>(this, "this.selectedClipboardData.id")
                .option { it.map { id -> clipboardManager!!.getContent(id)!! }.orElse("") }

        autoComputer.bind(this.editedClipboardData::content).from(content_textArea!!)
        autoComputer.bind(this.editedClipboardData::title).from(title_textField!!)
        autoComputer.bind(this.editedClipboardData::id).from(this, "this.selectedClipboardData.id")
////
//        autoComputer.bind<String>(this, "this.editedClipboardData.content").from(content_textArea!!)
//        autoComputer.bind<String>(this, "this.editedClipboardData.title").from(title_textField!!)
//        autoComputer.bind<String>(this, "this.editedClipboardData.id").from<String?>(this, "this.selectedClipboardData.id")


//        rxSwingBinders.onChange(title_textField)
//                .mergeWith(rxSwingBinders.onChange(content_textArea))
//                .map { title_textField!!.document.length + content_textArea!!.document.length }
//                .mergeWith(rxSwingBinders.onChange(title_list).map { title_textField!!.document.length + content_textArea!!.document.length })
//                .map { length -> length > 0 }
//                .subscribe { enable -> save_button!!.isEnabled = enable }
//
//        rxSwingBinders.onChange(title_list)
//                .map { title_list?.selectedIndex }
//                .subscribe { selectedIndex -> selectedIndex?.let { selectData(it) } }

        refreshTitles()

    }

    private fun refreshTitles() {
        this.datas = clipboardManager!!.getData().toMutableList()
        val datas = this.datas!!.stream()
                .map { clipboardData -> titleFormat(clipboardData.title) }
                .toList()
                .toMutableList()
        datas.add("-new-")
        val listModel = DefaultComboBoxModel(datas.toTypedArray())
        EventQueue.invokeLater {
            title_list?.model = listModel
            title_list?.selectedIndex = 0
        }
//        selectData(0)
    }
//
//    private fun selectData(index: Int) {
//
//        val maxSelectionIndex = datas!!.size
//        if (index == -1 || index == maxSelectionIndex) {
//            selectedClipboardData = null
//
//            title_textField!!.text = ""
//            content_textArea!!.text = ""
//
//        } else {
//            selectedClipboardData = this.datas!![index]
//
//            title_textField!!.text = selectedClipboardData!!.title
//            content_textArea!!.text = clipboardManager!!.getContent(selectedClipboardData!!.id!!)
//
//        }
//    }

    private fun saveData() {
        try {
            clipboardManager!!.saveData(editedClipboardData)
            refreshTitles()
        } catch (e: Exception) {
            logger!!.error("error saveData:" + ExceptionUtils.getStackTrace(e))
        }
    }

    private fun removeData() {
        try {
            clipboardManager!!.deleteData(selectedClipboardData!!.id!!)
            refreshTitles()
        } catch (e: Exception) {
            logger!!.error("error removeData:" + ExceptionUtils.getStackTrace(e))
        }
    }

    private fun onCancel() {
//        rxSwingBinders.dispose()

        actionContext!!.unHold()
        // add your code here if necessary
        dispose()
    }

    companion object {

        private val titleFormat: (String?) -> String = { s ->
            when {
                s == null -> ""
                s.length > 15 -> s.substring(0, 15) + ".."
                else -> s
            }
        }
    }
}