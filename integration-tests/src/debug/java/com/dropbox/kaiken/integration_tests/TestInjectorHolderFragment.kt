package com.dropbox.kaiken.integration_tests

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dropbox.kaiken.annotation.Injectable
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import javax.inject.Inject

@Injectable
class TestInjectorHolderFragment : Fragment(), InjectorHolder<TestInjectorHolderFragmentInjector> {
    @Inject
    lateinit var message: String

    override fun getInjectorFactory() = fakeInjectorFactory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        message = ""
    }

    fun testInject() {
        inject()
    }
}

private fun fakeInjectorFactory() = object : InjectorFactory<TestInjectorHolderFragmentInjector> {
    override fun createInjector() = FakeInjector2()
}

class FakeInjector2 : TestInjectorHolderFragmentInjector {
    override fun inject(fragment: TestInjectorHolderFragment?) {
        fragment!!.message = "Hello Fragment!"
    }
}
