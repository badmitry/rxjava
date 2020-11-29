package com.badmitry.rxjava.mvp.view

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface IMainView : MvpView {
    fun requestPerm()
    fun showConvertBtn(b: Boolean)
    fun setText(text: String)
    @StateStrategyType(SkipStrategy::class)
    fun setProgressBar(i: Int)
    fun showProgressBar(b: Boolean)
}