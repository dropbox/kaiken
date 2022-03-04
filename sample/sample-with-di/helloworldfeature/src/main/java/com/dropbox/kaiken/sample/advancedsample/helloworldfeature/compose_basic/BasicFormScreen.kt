package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun BasicFormScreen(
    model: BasicPresenter.Model,
    handleAnswer: (BasicPresenter.AnswerQuestion) -> Unit,
) {
    Text("Hello ${model.userName}")
}