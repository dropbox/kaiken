package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import com.dropbox.common.inject.AuthRequiredScreenScope
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.BasePresenter
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.Presenter
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

abstract class BasicPresenter : Presenter<BasicPresenter.Event, BasicPresenter.Model, BasicPresenter.Effect>(Model()) {
    sealed interface Event
    data class AnswerName(val answer: String): Event
    data class AnswerFlavor(val answer: String): Event
    data class AnswerColor(val answer: String): Event

    data class Model(
        val userName: String = "",
        val flavor: String = "",
        val color: String = "",
        val page: Page = Page.NAME,
    )

    enum class Page { NAME, FLAVOR, COLOR, SUMMARY }

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
            is AnswerName -> model = model.copy(userName = event.answer, page = Page.FLAVOR)
            is AnswerFlavor -> model = model.copy(flavor = event.answer, page = Page.COLOR)
            is AnswerColor -> model = model.copy(color = event.answer, page = Page.SUMMARY)
        }
    }
}