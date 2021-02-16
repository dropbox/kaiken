//package com.dropbox.kaiken.integration_tests
//
//import androidx.fragment.app.Fragment
//import com.dropbox.kaiken.annotation.Injectable
//import com.dropbox.kaiken.runtime.InjectorFactory
//import com.dropbox.kaiken.runtime.InjectorHolder
//import javax.inject.Inject
//
//@Injectable(COMPONENT = FakeInjector::class)
//class TestInjectorHolderFragment : Fragment(), InjectorHolder<TestInjectorHolderFragmentInjector> {
//    @Inject
//    var message: String? = null
//
//    override fun getInjectorFactory() = fakeInjectorFactory()
//
//    fun testInject() {
//        inject()
//    }
//}
//
//private fun fakeInjectorFactory() = object : InjectorFactory<TestInjectorHolderFragmentInjector> {
//    override fun createInjector() = FakeInjector2()
//}
//
//class FakeInjector2 : TestInjectorHolderFragmentInjector {
//    override fun inject(fragment: TestInjectorHolderFragment?) {
//        fragment!!.message = "Hello Fragment!"
//    }
//}
