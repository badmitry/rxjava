package com.badmitry.rxjava.mvp.presenter

import com.badmitry.rxjava.mvp.model.IFileJpgChecker
import com.badmitry.rxjava.mvp.model.IConvert
import com.badmitry.rxjava.mvp.view.IMainView
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MainPresenter(
    private val mainScheduler: Scheduler,
    private val pngFileFileJpgChecker: IFileJpgChecker
) : MvpPresenter<IMainView>() {
    fun requestPerm() {
        viewState.requestPerm()
    }

    fun checkPermission(
        grantResults: IntArray,
        permissionDenied: Int
    ) {
        grantResults.forEach {
            if (it == permissionDenied) {
                viewState.requestPerm()
                return
            }
        }
    }

    fun checkFile(resultCode: Int, filePath: String?) {
        if (resultCode == -1) {
            filePath?.let { it ->
                if (pngFileFileJpgChecker.checkFile(it)) {
                    viewState.setText(it)
                    viewState.showConvertBtn(true)
                } else {
                    viewState.setText("You selected the wrong file format")
                    viewState.showConvertBtn(false)
                }
            }
        }
    }

    fun convertImg(androidConverter: IConvert) {
        viewState.showConvertBtn(false)
        viewState.showProgressBar(true)
        progressProcessSimulator().observeOn(mainScheduler, false, 1).subscribe({
            Thread.sleep(100)
            viewState.setProgressBar(it / 1000000)
        }, {
            it.printStackTrace()
        })
        convertImgObserver(androidConverter).observeOn(mainScheduler).subscribe({
            if (it) {
                viewState.setText("Conversion is done")
                viewState.showProgressBar(false)
                viewState.setProgressBar(0)
            } else {
                viewState.setText("Conversion fail")
                viewState.showProgressBar(false)
                viewState.setProgressBar(0)
            }
        }, {
            it.printStackTrace()
            viewState.showProgressBar(false)
            viewState.setProgressBar(0)
        })
    }

    private fun progressProcessSimulator() =
        Flowable.range(0, 99000000).subscribeOn(Schedulers.computation()).onBackpressureDrop()

    private fun convertImgObserver(androidConverter: IConvert) = Single.create<Boolean> { emitter ->
        val dt = Calendar.getInstance()
        val time = dt.time
        val convertedImage = File("/storage/emulated/0/Download/$time.png")
        val outStream = FileOutputStream(convertedImage)
        androidConverter.convert(outStream)
        if (androidConverter.convert(outStream)) {
            emitter.onSuccess(true)
        } else {
            emitter.onSuccess(false)
        }
        outStream.flush()
        outStream.close()
    }.subscribeOn(Schedulers.computation())
}