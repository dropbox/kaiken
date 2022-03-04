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
    handleAnswer: (BasicPresenter.AnswerQuestion) -> Unit,
) {
    Column {
        Text(text = "What's your name?")

        QuestionForm(
            defaultAnswer = model.userName,
            handleAnswer = handleAnswer,
        )

        Text(text = "Your name is: ${model.userName}")
    }
}

@Composable
fun QuestionForm(
    defaultAnswer: String,
    handleAnswer: (BasicPresenter.AnswerQuestion) -> Unit,
) {
    var answer by remember { mutableStateOf(defaultAnswer) }

    TextField(value = answer, onValueChange = { answer = it })

    Button(onClick = { handleAnswer(BasicPresenter.AnswerQuestion(answer)) }) {
        Text(text = "Submit")
    }
}