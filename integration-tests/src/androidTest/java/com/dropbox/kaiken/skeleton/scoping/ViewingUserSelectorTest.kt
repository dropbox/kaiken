package com.dropbox.kaiken.skeleton.scoping

import android.content.Intent
import android.os.Bundle
import com.dropbox.kaiken.scoping.ViewingUserSelector
import com.dropbox.kaiken.scoping.getViewingUserSelector
import com.dropbox.kaiken.scoping.hasViewingUserSelector
import com.dropbox.kaiken.scoping.putViewingUserSelector
import com.dropbox.kaiken.scoping.requireViewingUserSelector
import com.google.common.truth.Truth.assertThat
import org.junit.Test

private const val USER_ID = "12345"

class ViewingUserSelectorTest {
    private val expectedUserSelector = ViewingUserSelector.fromUserId(USER_ID)

    @Test
    fun given_EmptyBundle_WHEN_hasGetViewingUserSelector_THEN_returns_false() {
        // GIVEN
        val bundle = Bundle()

        // WHEN
        val hasResult = bundle.hasViewingUserSelector()
        val getResult = bundle.getViewingUserSelector()

        // THEN
        assertThat(hasResult).isFalse()
        assertThat(getResult).isNull()
    }

    @Test(expected = IllegalArgumentException::class)
    fun given_EmptyBundle_WHEN_requireViewingUserSelector_THEN_throws() {
        // GIVEN
        val bundle = Bundle()

        // WHEN
        bundle.requireViewingUserSelector()

        // THEN
        // throws
    }

    @Test
    fun givenNonEmptyBundle_WHEN_hasGetViewingUserSelector_THEN_returns_true_notnull() {
        // GIVEN
        val bundle = Bundle().apply {
            putViewingUserSelector(expectedUserSelector)
        }

        // WHEN
        val hasResult = bundle.hasViewingUserSelector()
        val getResult = bundle.getViewingUserSelector()

        // THEN
        assertThat(hasResult).isTrue()
        assertThat(getResult).isNotNull()
        assertThat(getResult!!).isEqualTo(expectedUserSelector)
    }

    @Test
    fun givenEmptyIntent_WHEN_hasGetViewingUserSelector_THEN_returns_false_null() {
        // GIVEN
        val intent = Intent()

        // WHEN
        val hasResult = intent.hasViewingUserSelector()
        val getResult = intent.getViewingUserSelector()

        // THEN
        assertThat(hasResult).isFalse()
        assertThat(getResult).isNull()
    }

    @Test(expected = IllegalArgumentException::class)
    fun givenEmptyIntent_WHEN_requireViewingUserSelector_THEN_throws() {
        // GIVEN
        val intent = Intent()

        // WHEN
        intent.requireViewingUserSelector()

        // THEN
        // throws
    }

    @Test
    fun givenNonEmptyIntent_WHEN_hasGetViewingUserSelector_THEN_returns_true_notnull() {
        // GIVEN
        val intent = Intent().apply {
            putViewingUserSelector(expectedUserSelector)
        }

        // WHEN
        val hasResult = intent.hasViewingUserSelector()
        val getResult = intent.getViewingUserSelector()

        // THEN
        assertThat(hasResult).isTrue()
        assertThat(getResult).isNotNull()
        assertThat(getResult!!).isEqualTo(expectedUserSelector)
    }
}
