package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun BasicFilmsScreen(model: BasicFilmsPresenter.Model) {
    if (model.loading) {
        Text(text = "loading")
    } else {
        Text(text = "not loading")
    }
}