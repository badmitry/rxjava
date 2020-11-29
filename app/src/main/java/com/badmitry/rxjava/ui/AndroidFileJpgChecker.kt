package com.badmitry.rxjava.ui

import android.webkit.MimeTypeMap
import com.badmitry.rxjava.mvp.model.IFileJpgChecker

class AndroidFileJpgChecker : IFileJpgChecker {

    private fun getMimeType(url: String): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }

    override fun checkFile(url: String): Boolean {
        val mimeType = getMimeType(url)
        return mimeType.equals("image/jpeg")
    }
}