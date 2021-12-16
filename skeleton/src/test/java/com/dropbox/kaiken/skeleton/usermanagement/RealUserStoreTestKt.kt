package com.dropbox.kaiken.skeleton.usermanagement

import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class RealUserStoreTestKt {

    @Test
    fun `GIVEN list of users WHEN minus with self THEN remove elements`() {
        // GIVEN
        val users = setOf(RealUserStoreTest.FAKE_USER_ONE)

        // WHEN
        val actual = users.minusById(users)

        // THEN
        assertThat(actual).isEmpty()
    }

    @Test
    fun `GIVEN list of users WHEN minus empty set THEN no op`() {
        // GIVEN
        val users = setOf(RealUserStoreTest.FAKE_USER_ONE)

        // WHEN
        val actual = users.minusById(emptySet())

        // THEN
        assertThat(actual).isEqualTo(users)
    }

    @Test
    fun `GIVEN list of users WHEN minus no overlap THEN set one returned`() {
        // GIVEN
        val setOne = setOf(RealUserStoreTest.FAKE_USER_ONE)
        val setTwo = setOf(RealUserStoreTest.FAKE_USER_TWO)

        // WHEN
        val actual = setOne.minusById(setTwo)

        // THEN
        assertThat(actual).isEqualTo(setOne)
    }

    @Test
    fun `GIVEN list of users WHEN equals new list of users THEN empty set`() {
        // GIVEN
        val setOne = setOf(RealUserStoreTest.FAKE_USER_ONE)
        val setTwo = setOf(RealUserStoreTest.FAKE_USER_ONE)

        // WHEN
        val actual = setOne.minusById(setTwo)

        // THEN
        assertThat(actual).isEqualTo(emptySet<SkeletonUser>())
    }
}
