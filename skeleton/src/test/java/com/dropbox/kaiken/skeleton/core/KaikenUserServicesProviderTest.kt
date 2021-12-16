package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.AppTeardownHelper
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.scoping.UserTeardownHelper
import com.dropbox.kaiken.skeleton.usermanagement.UsersEvent
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class KaikenUserServicesProviderTest {
    private val userEvents = MutableStateFlow(UsersEvent(emptySet()))

    private var coroutineScope = TestCoroutineScope()
    private var appServices: AppServices = DummyAppServices()

    private var userFactory: (AppServices, SkeletonUser) -> UserServices = { _, _ ->
        TODO("Not yet implemented")
    }

    private fun createKaikenUserServicesProviderTest(): KaikenUserServicesProvider = KaikenUserServicesProvider(appServices, userFactory, userEvents, coroutineScope = coroutineScope)

    @Test
    fun `GIVEN kaiken user services WHEN no users and provide services THEN return null`() = runBlockingTest {
        // GIVEN
        val servicesProvider = createKaikenUserServicesProviderTest()

        // WHEN
        val actual = servicesProvider.provideUserServices("1234")

        // THEN
        assertThat(actual).isNull()
    }

    @Test
    fun `GIVEN kaiken user services WHEN user provided and user requested THEN create and return user`() = runBlockingTest {
        // GIVEN
        val fakeUserServices = DummyUserServices()
        userFactory = { appServices, skeletonUser ->
            assertThat(appServices).isEqualTo(this@KaikenUserServicesProviderTest.appServices)
            assertThat(skeletonUser).isEqualTo(FAKE_USER)
            fakeUserServices
        }
        userEvents.emit(UsersEvent(setOf(FAKE_USER), setOf(FAKE_USER), emptySet()))
        val servicesProvider = createKaikenUserServicesProviderTest()

        // WHEN
        val actual = servicesProvider.provideUserServices(FAKE_ID)

        // THEN
        assertThat(actual).isEqualTo(fakeUserServices)
    }

    @Test
    fun `GIVEN kaiken user services WHEN user removed and none exist THEN return null`() = runBlockingTest {
        // GIVEN
        userEvents.emit(UsersEvent(emptySet(), emptySet(), setOf(FAKE_USER)))
        val servicesProvider = createKaikenUserServicesProviderTest()

        // WHEN
        val actual = servicesProvider.provideUserServices(FAKE_ID)

        // THEN
        assertThat(actual).isNull()
    }

    companion object {
        private const val FAKE_ID = "fakeId"
        private const val FAKE_TOKEN = "fakeToken"

        private val FAKE_USER = SkeletonUser(FAKE_ID, SkeletonOauth2(FAKE_TOKEN))
    }
}

class DummyAppServices : AppServices {
    override fun getTeardownHelper(): AppTeardownHelper {
        TODO("Not yet implemented")
    }
}

class DummyUserServices : KaikenUserServices {
    override fun getUserTeardownHelper(): UserTeardownHelper {
        TODO("Not yet implemented")
    }
}
