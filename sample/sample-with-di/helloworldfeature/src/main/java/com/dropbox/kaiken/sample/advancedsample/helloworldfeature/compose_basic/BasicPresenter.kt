package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import com.dropbox.common.inject.AuthRequiredScreenScope
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.BasePresenter
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.Presenter
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

abstract class BasicPresenter : Presenter<BasicPresenter.Event, BasicPresenter.Model, BasicPresenter.Effect>(Model()) {
    sealed interface Event
    data class AnswerQuestion(val answer: String): Event

    data class Model(
        val userName: String = "",
    )

    sealed interface Effect
}

@SingleIn(AuthRequiredScreenScope::class)
@ContributesMultibinding(
    AuthRequiredScreenScope::class,
    boundType = BasePresenter::class
)
class RealBasicPresenter @Inject constructor() : BasicPresenter() {
    override suspend fun eventHandler(event: Event) {
        when (event) {
            is AnswerQuestion -> model = model.copy(userName = event.answer)
        }
    }
}