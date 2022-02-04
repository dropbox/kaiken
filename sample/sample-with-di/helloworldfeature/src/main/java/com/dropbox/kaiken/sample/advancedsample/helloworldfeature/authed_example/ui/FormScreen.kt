package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.Favorite
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.FavoritesRepository
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.Film
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example.repository.GhibliRepository
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import com.zachklipp.compose.backstack.Backstack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

abstract class FormPresenter : Presenter<FormPresenter.Event, FormPresenter.Model, FormPresenter.Effect>(Model(listOf(Page.NAME))) {
    sealed interface Event
    data class AnswerName(val answer: String) : Event
    data class AnswerQuest(val answer: String) : Event
    data class AnswerVelocity(val answer: String) : Event
    data class AddFilm(val title: String) : Event
    data class AddFavorite(val id: String) : Event
    object LoadFilms : Event
    object PopBackstack : Event
    object SkipFilms : Event

    data class Model(
        val backstack: List<Page>,
        val name: String = "",
        val quest: String = "",
        val airspeedVelocity: String = "",
        val films: List<Film> = listOf(),
    )

    enum class Page {
        NAME,
        QUEST,
        VELOCITY,
        FILMS,
        CONFIRM_ANSWERS,
    }

    sealed interface Effect
    data class ShowSnackbar(val message: String, val action: String): Effect
}

@SingleIn(AuthRequiredScreenScope::class)
@ContributesMultibinding(
    AuthRequiredScreenScope::class,
    boundType = BasePresenter::class
)
class RealFormPresenter @Inject constructor(
    val ghibliRepository: GhibliRepository,
    val favoritesRepository: FavoritesRepository
) : FormPresenter() {
    override suspend fun eventHandler(event: Event) {
        when (event) {
            is AnswerName -> model = model.copy(name = event.answer, backstack = model.backstack + Page.QUEST)
            is AnswerQuest -> model = model.copy(quest = event.answer, backstack = model.backstack + Page.VELOCITY)
            is AnswerVelocity -> model = model.copy(airspeedVelocity = event.answer, backstack = model.backstack + Page.FILMS)
            is PopBackstack -> model = model.copy(backstack = model.backstack.dropLast(1))
            is AddFilm -> addFilm(event.title)
            is AddFavorite -> addFavorite(event.id)
            PopBackstack -> model = model.copy(backstack = model.backstack.dropLast(1))
            LoadFilms -> loadFilms()
            SkipFilms -> model = model.copy(backstack = model.backstack + Page.CONFIRM_ANSWERS)
        }
    }

    private fun loadFilms() {
        CoroutineScope(Dispatchers.IO).launch {
            ghibliRepository.fetchFilms().collect {
                model = model.copy(films = it)
            }
        }
    }

    private suspend fun addFilm(title: String) = withContext(Dispatchers.IO) {
        ghibliRepository.addFilm(Film("123", title, "", ""))
        CoroutineScope(Dispatchers.Main).launch {
            emitEffect(ShowSnackbar("New film added!", "OK"))
        }
    }

    private suspend fun addFavorite(id: String) = withContext(Dispatchers.IO) {
        favoritesRepository.addFavorite(Favorite(id))
        CoroutineScope(Dispatchers.Main).launch {
            emitEffect(ShowSnackbar("Favorite added!", "OK"))
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
                    FormPresenter.Page.NAME -> NameQuestionPage(model.name, handleEvent)
                    FormPresenter.Page.QUEST -> QuestQuestionPage(model.quest, handleEvent)
                    FormPresenter.Page.VELOCITY -> VelocityQuestionPage(model.airspeedVelocity, handleEvent)
                    FormPresenter.Page.FILMS -> FilmsPage(model.films, handleEvent)
                    FormPresenter.Page.CONFIRM_ANSWERS -> ConfirmPage(model)
                }
            }
        }
    }
}

@Composable
fun NameQuestionPage(
    name: String,
    handleEvent: (FormPresenter.Event) -> Boolean,
) {
    QuestionTemplate(
        question = "What is your name?",
        buttonText = "Submit",
        defaultValue = name,
    ) {
        handleEvent(FormPresenter.AnswerName(it))
    }
}

@Composable
fun QuestQuestionPage(
    quest: String,
    handleEvent: (FormPresenter.Event) -> Boolean,
) {
    QuestionTemplate(
        question = "What is your quest?",
        buttonText = "Submit",
        defaultValue = quest,
    ) {
        handleEvent(FormPresenter.AnswerQuest(it))
    }
}

@Composable
fun VelocityQuestionPage(
    velocity: String,
    handleEvent: (FormPresenter.Event) -> Boolean,
) {
    QuestionTemplate(
        question = "What is the airspeed velocity of an unladen swallow?",
        buttonText = "Submit",
        defaultValue = velocity,
    ) {
        handleEvent(FormPresenter.AnswerVelocity(it))
    }
}

@Composable
fun FilmsPage(
    films: List<Film>,
    handleEvent: (FormPresenter.Event) -> Boolean,
) {
    if (films.isEmpty()) {
        CircularProgressIndicator()
        handleEvent(FormPresenter.LoadFilms)
    } else {
        var newFilmTitle by remember { mutableStateOf("") }

        Column {
            LazyColumn(Modifier.weight(1f)) {
                films.forEach {
                    item { FilmRow(it, handleEvent) }
                }
            }

            TextField(value = newFilmTitle, onValueChange = { newFilmTitle = it })

            Row(
                modifier = Modifier.padding(0.dp, 24.dp, 0.dp, 48.dp),
            ) {
                Button(
                    modifier = Modifier.padding(24.dp, 0.dp),
                    onClick = { handleEvent(FormPresenter.AddFilm(newFilmTitle)) },
                ) {
                    Text("Add Film")
                }

                Button(
                    onClick = { handleEvent(FormPresenter.SkipFilms) },
                ) {
                    Text("Skip")
                }
            }
        }
    }
}

@Composable
fun FilmRow(
    film: Film,
    handleEvent: (FormPresenter.Event) -> Boolean
) {
    val textModifier = Modifier.padding(12.dp, 0.dp)

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)) {
        IconButton(
            onClick = { handleEvent(FormPresenter.AddFavorite(film.id)) },
        ) {
            Icon(Icons.Filled.Add, "Add Favorite")
        }
        Column {
            Text(modifier = textModifier, text = film.title)
            Row {
                Text(modifier = textModifier, text = film.releaseDate)
                Text(modifier = textModifier, text = film.description)

            }
        }

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
    defaultValue: String,
    handleEvent: (String) -> Boolean,
) {
    var answer by remember { mutableStateOf(defaultValue) }
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
