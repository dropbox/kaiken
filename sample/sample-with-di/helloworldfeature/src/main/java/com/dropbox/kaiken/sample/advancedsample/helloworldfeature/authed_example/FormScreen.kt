package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dropbox.common.inject.AuthRequiredScreenScope
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.BasePresenter
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.Presenter
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import com.zachklipp.compose.backstack.Backstack
import javax.inject.Inject

abstract class FormPresenter : Presenter<FormPresenter.Event, FormPresenter.Model, FormPresenter.Effect>(Model(listOf(Page.NAME))) {
    sealed interface Event
    data class AnswerName(val answer: String) : Event
    data class AnswerQuest(val answer: String) : Event
    data class AnswerVelocity(val answer: String) : Event
    object PopBackstack : Event

    data class Model(
        val backstack: List<Page>,
        val name: String = "",
        val quest: String = "",
        val airspeedVelocity: String = ""
    )

    enum class Page {
        NAME,
        QUEST,
        VELOCITY,
        CONFIRM_ANSWERS,
    }

    sealed interface Effect
    object ListSuccessfulSnackbar : Effect
}

@SingleIn(AuthRequiredScreenScope::class)
@ContributesMultibinding(
    AuthRequiredScreenScope::class,
    boundType = BasePresenter::class
)
class RealFormPresenter @Inject constructor() : FormPresenter() {
    override suspend fun eventHandler(event: Event) {
        when (event) {
            is AnswerName -> model = model.copy(name = event.answer, backstack = model.backstack + Page.QUEST)
            is AnswerQuest -> model = model.copy(quest = event.answer, backstack = model.backstack + Page.VELOCITY)
            is AnswerVelocity -> model = model.copy(airspeedVelocity = event.answer, backstack = model.backstack + Page.CONFIRM_ANSWERS)
            is PopBackstack -> model = model.copy(backstack = model.backstack.dropLast(1))
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun FormScreen(
    model: FormPresenter.Model,
    handleEvent: (FormPresenter.Event) -> Boolean,
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(12.dp)
        ) {
            Text(
                "A wizard's wizard",
                style = MaterialTheme.typography.h4,
            )

            if (model.backstack.size > 1) {
                BackHandler { handleEvent(FormPresenter.PopBackstack) }
            }

            Backstack(model.backstack) {
                when (it) {
                    FormPresenter.Page.NAME -> NameQuestionPage(handleEvent)
                    FormPresenter.Page.QUEST -> QuestQuestionPage(handleEvent)
                    FormPresenter.Page.VELOCITY -> VelocityQuestionPage(handleEvent)
                    FormPresenter.Page.CONFIRM_ANSWERS -> ConfirmPage(model)
                }
            }
        }
    }
}

@Composable
fun NameQuestionPage(
    handleEvent: (FormPresenter.Event) -> Boolean,
) {
    QuestionTemplate(
        question = "What is your name?",
        buttonText = "Submit",
    ) {
        handleEvent(FormPresenter.AnswerName(it))
    }
}

@Composable
fun QuestQuestionPage(
    handleEvent: (FormPresenter.Event) -> Boolean,
) {
    QuestionTemplate(
        question = "What is your quest?",
        buttonText = "Submit",
    ) {
        handleEvent(FormPresenter.AnswerQuest(it))
    }
}

@Composable
fun VelocityQuestionPage(
    handleEvent: (FormPresenter.Event) -> Boolean,
) {
    QuestionTemplate(
        question = "What is the airspeed velocity of an unladen swallow?",
        buttonText = "Submit",
    ) {
        handleEvent(FormPresenter.AnswerVelocity(it))
    }
}

@Composable
fun ConfirmPage(model: FormPresenter.Model) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Name: ${model.name}")
        Text("Quest: ${model.quest}")
        Text("Airspeed Velocity: ${model.airspeedVelocity}")
    }
}

@Composable
fun QuestionTemplate(
    question: String,
    buttonText: String,
    handleEvent: (String) -> Boolean,
) {
    var answer by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(question)

        TextField(
            value = answer,
            onValueChange = { answer = it }
        )

        Button(
            modifier = Modifier.padding(0.dp, 12.dp),
            onClick = { handleEvent(answer) },
        ) {
            Text(buttonText)
        }
    }
}

@Preview
@Composable
fun ConfirmPagePreview() {
    ConfirmPage(
        FormPresenter.Model(
            mutableListOf(FormPresenter.Page.CONFIRM_ANSWERS),
            "John Doe",
            "To find the holy grail",
            "I don't know"
        )
    )
}
