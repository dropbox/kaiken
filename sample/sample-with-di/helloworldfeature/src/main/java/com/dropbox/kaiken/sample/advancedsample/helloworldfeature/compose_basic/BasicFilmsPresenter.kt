package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import com.dropbox.common.inject.AuthRequiredScreenScope
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.BasePresenter
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.Presenter
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

abstract class BasicFilmsPresenter : Presenter<BasicFilmsPresenter.Event, BasicFilmsPresenter.Model, BasicFilmsPresenter.Effect>(Model()) {
    sealed interface Event

    data class Model(
        val loading: Boolean = true
    )

    sealed interface Effect
}

@SingleIn(AuthRequiredScreenScope::class)
@ContributesMultibinding(
    AuthRequiredScreenScope::class,
    boundType = BasePresenter::class
)
class RealBasicFilmsPresenter @Inject constructor() : BasicFilmsPresenter() {
    override suspend fun eventHandler(event: Event) {
        TODO("Not yet implemented")
    }
}