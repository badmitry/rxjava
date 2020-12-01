package com.badmitry.rxjava.ui

import android.graphics.Bitmap
import com.badmitry.rxjava.mvp.model.IConvert
import java.io.FileOutputStream

class AndroidConverter (private val bmp: Bitmap): IConvert {
    override fun convert(outStream: FileOutputStream): Boolean {
        return bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream)
    }
}