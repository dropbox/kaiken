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
    handleName: (BasicPresenter.AnswerName) -> Unit,
    handleFlavor: (BasicPresenter.AnswerFlavor) -> Unit,
    handleColor: (BasicPresenter.AnswerColor) -> Unit,
) {
    Column {
        when(model.page) {
            BasicPresenter.Page.NAME -> NameQuestion(model.userName, handleName)
            BasicPresenter.Page.FLAVOR -> FlavorQuestion(model.flavor, handleFlavor)
            BasicPresenter.Page.COLOR -> ColorQuestion(model.color, handleColor)
            BasicPresenter.Page.SUMMARY -> Summary(model)
        }
    }
}

@Composable
fun NameQuestion(
    defaultAnswer: String,
    handleName: (BasicPresenter.AnswerName) -> Unit,
) {
    var answer by remember { mutableStateOf(defaultAnswer) }

    Text(text = "What's your name?")

    TextField(value = answer, onValueChange = { answer = it })

    Button(onClick = { handleName(BasicPresenter.AnswerName(answer)) }) {
        Text(text = "Submit")
    }
}

@Composable
fun FlavorQuestion(
    defaultAnswer: String,
    handleFlavor: (BasicPresenter.AnswerFlavor) -> Unit,
) {
    var answer by remember { mutableStateOf(defaultAnswer) }

    Text(text = "What's your favorite ice cream flavor?")

    TextField(value = answer, onValueChange = { answer = it })

    Button(onClick = { handleFlavor(BasicPresenter.AnswerFlavor(answer)) }) {
        Text(text = "Submit")
    }
}

@Composable
fun ColorQuestion(
    defaultAnswer: String,
    handleColor: (BasicPresenter.AnswerColor) -> Unit,
) {
    var answer by remember { mutableStateOf(defaultAnswer) }

    Text(text = "What's your favorite color?")

    TextField(value = answer, onValueChange = { answer = it })

    Button(onClick = { handleColor(BasicPresenter.AnswerColor(answer)) }) {
        Text(text = "Submit")
    }
}

@Composable
fun Summary(model: BasicPresenter.Model) {
    Text(text = "Your name is: ${model.userName}")
    Text(text = "Your favorite ice cream flavor is: ${model.flavor}")
    Text(text = "Your favorite color is: ${model.color}")
}