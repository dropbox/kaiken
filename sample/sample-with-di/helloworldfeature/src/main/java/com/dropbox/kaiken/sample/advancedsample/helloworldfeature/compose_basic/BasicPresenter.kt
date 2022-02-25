package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.Presenter

abstract class BasicPresenter : Presenter<BasicPresenter.Event, BasicPresenter.Model, BasicPresenter.Effect>(Model(true)) {
    sealed interface Event

    data class Model(
        val loading: Boolean,
        val userName: String = "Julio"
    )

    sealed interface Effect
}