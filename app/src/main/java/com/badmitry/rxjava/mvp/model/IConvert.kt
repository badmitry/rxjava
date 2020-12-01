package com.badmitry.rxjava.mvp.model

import java.io.FileOutputStream

interface IConvert {
    fun convert(outStream: FileOutputStream): Boolean
}