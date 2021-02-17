package com.dropbox.kaiken.integration_tests

import androidx.fragment.app.Fragment
import com.dropbox.kaiken.annotation.AutoInjectable
import com.dropbox.kaiken.annotation.Injectable
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.runtime.findInjector
import com.dropbox.kaiken.scoping.AuthAwareFragment
import javax.inject.Inject

@Injectable(COMPONENT = FakeInjector2::class)
class TestInjectorHolderFragment : Fragment(), InjectorHolder<TestInjectorHolderFragmentInjector> {
    @Inject
    lateinit var message: String

    override fun getInjectorFactory() = fakeInjectorFactory()

    fun testInject() {
        inject()
    }
}

 fun fakeInjectorFactory() = object : InjectorFactory<TestInjectorHolderFragmentInjector> {
    override fun createInjector() = FakeInjector2()
}

class FakeInjector2 : TestInjectorHolderFragmentInjector {
    override fun inject(fragment: TestInjectorHolderFragment) {
        fragment.message = "Hello Fragment!"
    }
}
