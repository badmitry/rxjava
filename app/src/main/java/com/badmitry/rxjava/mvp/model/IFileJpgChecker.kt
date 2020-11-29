package com.badmitry.rxjava.mvp.model

interface IFileJpgChecker {
    fun checkFile(url: String): Boolean
}