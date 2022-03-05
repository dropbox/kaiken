package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun BasicFilmsScreen(
    model: BasicFilmsPresenter.Model,
    handleEvent: (BasicFilmsPresenter.Event) -> Unit
) {
    if (model.films.isEmpty()) {
        CircularProgressIndicator()
        handleEvent(BasicFilmsPresenter.LoadFilms)
    } else {
        Text(text = "has films")
    }
}