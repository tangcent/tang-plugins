package com.itangcent.idea.plugin.extend.rx

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer
import java.awt.event.ActionEvent
import javax.swing.AbstractButton
import javax.swing.JList
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.ListSelectionEvent
import javax.swing.text.JTextComponent


fun Disposable.join(disposableContainer: DisposableContainer) {
    disposableContainer.add(this)
}

class RxSwingBinders : Disposable {
//    fun Observable<ActionEvent>.from(button: AbstractButton): Observable<ActionEvent> {
//        return RxSwingBinders.onClick(button)
//    }

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    override fun dispose() {
        compositeDisposable.dispose()
    }

    override fun isDisposed(): Boolean {
        return compositeDisposable.isDisposed()
    }


    fun onClick(button: AbstractButton): Observable<ActionEvent> {
        return Observable.create<ActionEvent> { emitter ->
            button.addActionListener { e ->
                emitter.onNext(e)
            }
        }.doOnSubscribe { compositeDisposable::add }
    }

    fun onChange(jTextComponent: JTextComponent?): Observable<DocumentEvent> {
        return Observable.create<DocumentEvent> { emitter ->
            jTextComponent?.document?.addDocumentListener(object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent) {
                    emitter.onNext(e)
                }

                override fun removeUpdate(e: DocumentEvent) {
                    emitter.onNext(e)
                }

                override fun changedUpdate(e: DocumentEvent) {
                    emitter.onNext(e)
                }
            })
        }.doOnSubscribe { compositeDisposable::add };
    }

    fun <T> onChange(jList: JList<T>?): Observable<ListSelectionEvent> {
        return Observable.create<ListSelectionEvent> { emitter ->
            jList?.selectionModel?.addListSelectionListener { e ->
                emitter.onNext(e)
            }
        }.doOnSubscribe { compositeDisposable::add };
    }

}