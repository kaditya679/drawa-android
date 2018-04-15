package com.tomclaw.drawa.draw

import com.tomclaw.drawa.core.Journal
import com.tomclaw.drawa.util.SchedulersFactory
import io.reactivex.Observable
import io.reactivex.Single

interface DrawInteractor {

    fun loadHistory(): Observable<Unit>

    fun saveHistory(): Observable<Unit>

    fun undo(): Observable<Unit>

    fun delete(): Observable<Unit>

}

class DrawInteractorImpl(private val recordId: Int,
                         private val imageProvider: ImageProvider,
                         private val journal: Journal,
                         private val history: History,
                         private val drawHostHolder: DrawHostHolder,
                         private val schedulers: SchedulersFactory) : DrawInteractor {

    private var record = journal.get(recordId)

    private var isDeleted = false

    override fun loadHistory(): Observable<Unit> {
        return resolve({
            history.load()
                    .flatMap { imageProvider.readImage(record) }
                    .map { bitmap ->
                        drawHostHolder.drawHost.applyBitmap(bitmap)
                        bitmap.recycle()
                    }
                    .toObservable()
                    .subscribeOn(schedulers.io())
        }, {
            Observable.just(Unit)
        })
    }

    override fun saveHistory(): Observable<Unit> {
        return resolve({
            history.save()
                    .flatMap {
                        imageProvider.saveImage(
                                record,
                                drawHostHolder.drawHost.bitmap
                        )
                    }
                    .map { record = it }
                    .toObservable()
                    .subscribeOn(schedulers.io())
        }, {
            Observable.just(Unit)
        })
    }

    override fun undo(): Observable<Unit> {
        return resolve({
            Single
                    .create<Unit> { emitter ->
                        history.undo()
                        emitter.onSuccess(Unit)
                    }
                    .toObservable()
        }, {
            Observable.just(Unit)
        }).subscribeOn(schedulers.signle())
    }

    override fun delete(): Observable<Unit> {
        isDeleted = true
        return history.delete()
                .flatMap { journal.delete(id = recordId) }
                .toObservable()
                .subscribeOn(schedulers.io())
    }

    private fun <T> resolve(notDeleted: () -> T, deleted: () -> T): T {
        return if (isDeleted) deleted.invoke() else notDeleted.invoke()
    }

}