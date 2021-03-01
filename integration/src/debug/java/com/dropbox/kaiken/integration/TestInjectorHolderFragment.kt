//package com.dropbox.kaiken.integrationtests
//
//import androidx.fragment.app.Fragment
//import com.dropbox.kaiken.annotation.Injectable
//import com.dropbox.kaiken.runtime.InjectorFactory
//import com.dropbox.kaiken.runtime.InjectorHolder
//import com.dropbox.kaiken.scoping.DependencyProviderResolver
//import com.squareup.anvil.annotations.MergeComponent
//import dagger.Component
//import javax.inject.Inject
//import javax.inject.Scope
//
////@Injectable(COMPONENT = TestInjectorHolderFragment::class)
//class TestInjectorHolderFragment : Fragment(){//, InjectorHolder<TestInjectorHolderFragmentInjector> {
////    @Inject
////    lateinit var message: String
////
////    override fun getInjectorFactory() = fakeInjectorFactory()
////
////    fun testInject() {
////        inject()
////    }
//}
//
////fun fakeInjectorFactory() = object : InjectorFactory<TestInjectorHolderFragmentInjector> {
////    override fun createInjector() = FakeInjector2()
////}
////
////class FakeInjector2 : TestInjectorHolderFragmentInjector {
////    override fun inject(fragment: TestInjectorHolderFragment) {
////        fragment.message = "Hello Fragment!"
////    }
////}
////
////@Scope
////annotation class TestInjectorHolderFragmentScope
////
////class ATestInjectorHolderFragmentScope
////
////fun DependencyProviderResolver.testTestInjectorHolderFragmentComponentinjector():
////        InjectorFactory<TestInjectorHolderFragmentInjector> = object :
////        InjectorFactory<TestInjectorHolderFragmentInjector> {
////    override fun createInjector() = DaggerTestInjectorHolderFragmentComponent.factory().create(resolveDependencyProvider()) as TestInjectorHolderFragmentInjector
////}
////
////@TestInjectorHolderFragmentScope
////@MergeComponent(
////        dependencies = [Deps::class],
////        scope = ATestInjectorHolderFragmentScope::class
////)
////interface TestInjectorHolderFragmentComponent {
////    @Component.Factory
////    interface Factory {
////        fun create(dependencies: Deps): TestInjectorHolderFragmentComponent
////    }
////}