package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun BasicFormScreen(
    model: BasicPresenter.Model,
    handleEvent: (BasicPresenter.Event) -> Unit,
) {
    Column {
        when(model.page) {
            BasicPresenter.Page.NAME -> NameQuestion(model.userName, handleEvent)
            BasicPresenter.Page.FLAVOR -> FlavorQuestion(model.flavor, handleEvent)
            BasicPresenter.Page.COLOR -> ColorQuestion(model.color, handleEvent)
            BasicPresenter.Page.SUMMARY -> Summary(model)
        }
    }
}

@Composable
fun NameQuestion(
    name: String,
    handleEvent: (BasicPresenter.AnswerName) -> Unit,
) {
    QuestionTemplate(
        defaultAnswer = name,
        question = "What's your favorite name?",
    ) {
        answer -> handleEvent(BasicPresenter.AnswerName(answer))
    }
}

@Composable
fun FlavorQuestion(
    flavor: String,
    handleEvent: (BasicPresenter.AnswerFlavor) -> Unit,
) {
    QuestionTemplate(
        defaultAnswer = flavor,
        question = "What's your favorite ice cream flavor?",
    ) {
        answer -> handleEvent(BasicPresenter.AnswerFlavor(answer))
    }
}

@Composable
fun ColorQuestion(
    color: String,
    handleEvent: (BasicPresenter.AnswerColor) -> Unit,
) {
    QuestionTemplate(
        defaultAnswer = color,
        question = "What's your favorite color?",
    ) {
        answer -> handleEvent(BasicPresenter.AnswerColor(answer))
    }
}

@Composable
fun QuestionTemplate(
    defaultAnswer: String,
    question: String,
    handleEvent: (String) -> Unit,
) {
    var answer by remember { mutableStateOf(defaultAnswer) }

    Text(text = question)

    TextField(value = answer, onValueChange = { answer = it })

    Button(onClick = { handleEvent(answer) }) {
        Text(text = "Submit")
    }
}

@Composable
fun Summary(model: BasicPresenter.Model) {
    Text(text = "Your name is: ${model.userName}")
    Text(text = "Your favorite ice cream flavor is: ${model.flavor}")
    Text(text = "Your favorite color is: ${model.color}")
}