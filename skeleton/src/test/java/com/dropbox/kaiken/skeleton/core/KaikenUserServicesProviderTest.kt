package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.skeleton.usermanagement.UsersEvent
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
@Suppress("DEPRECATION")
class KaikenUserServicesProviderTest {
    private val userEvents = MutableStateFlow(UsersEvent(emptySet()))

    @SuppressWarnings
    private var coroutineScope = TestCoroutineScope()
    private var appServices: AppServices = DummyAppServices()

    private var userFactory: (AppServices, SkeletonUser) -> UserServices = { _, _ ->
        TODO("Not yet implemented")
    }

    private fun createKaikenUserServicesProviderTest(): KaikenUserServicesProvider = KaikenUserServicesProvider(appServices, userFactory, userEvents, coroutineScope = coroutineScope)

    @Test
    @SuppressWarnings
    fun `GIVEN kaiken user services WHEN no users and provide services THEN return null`() = coroutineScope.runBlockingTest {
        // GIVEN
        val servicesProvider = createKaikenUserServicesProviderTest()

        // WHEN
        val actual = servicesProvider.provideUserServices("1234")

        // THEN
        assertThat(actual).isNull()
    }

    @Test
    @SuppressWarnings
    fun `GIVEN kaiken user services WHEN user provided and user requested THEN create and return user`() = coroutineScope.runBlockingTest {
        // GIVEN
        var userFactoryCounter = 0
        val fakeUserServices = DummyUserServices()
        userFactory = { appServices, skeletonUser ->
            assertThat(appServices).isEqualTo(this@KaikenUserServicesProviderTest.appServices)
            assertThat(skeletonUser).isEqualTo(FAKE_USER)
            userFactoryCounter += 1
            fakeUserServices
        }
        userEvents.emit(UsersEvent(setOf(FAKE_USER), setOf(FAKE_USER), emptySet()))
        val servicesProvider = createKaikenUserServicesProviderTest()

        // WHEN
        val actual = servicesProvider.provideUserServices(FAKE_ID)

        // THEN
        assertThat(actual).isEqualTo(fakeUserServices)
        assertThat(userFactoryCounter).isEqualTo(1)
    }

    @Test
    @SuppressWarnings
    fun `GIVEN kaiken user services WHEN user removed and none exist THEN return null`() = coroutineScope.runBlockingTest {
        // GIVEN
        userEvents.emit(UsersEvent(emptySet(), emptySet(), setOf(FAKE_USER)))
        val servicesProvider = createKaikenUserServicesProviderTest()

        // WHEN
        val actual = servicesProvider.provideUserServices(FAKE_ID)

        // THEN
        assertThat(actual).isNull()
    }

    @Test
    @SuppressWarnings
    fun `GIVEN kaiken user services WHEN user is removed THEN teardown and return null user services`() = coroutineScope.runBlockingTest {
        // GIVEN
        val fakeUserServices = DummyUserServices()
        userFactory = { appServices, skeletonUser ->
            assertThat(appServices).isEqualTo(this@KaikenUserServicesProviderTest.appServices)
            assertThat(skeletonUser).isEqualTo(FAKE_USER)
            fakeUserServices
        }

        val servicesProvider = createKaikenUserServicesProviderTest()

        // WHEN
        userEvents.emit(UsersEvent(setOf(FAKE_USER), setOf(FAKE_USER), emptySet()))
        userEvents.emit(UsersEvent(emptySet(), emptySet(), setOf(FAKE_USER)))

        // THEN
        assertThat(servicesProvider.provideUserServices(FAKE_ID)).isNull()
    }

    companion object {
        private const val FAKE_ID = "fakeIdOne"
        private const val FAKE_TOKEN = "fakeToken"

        private val FAKE_USER = SkeletonUser(FAKE_ID, SkeletonOauth2(FAKE_TOKEN))
    }
}

class DummyAppServices : AppServices

open class DummyUserServices : KaikenUserServices
