package com.dropbox.kaiken.integration

import androidx.fragment.app.Fragment
import dagger.Component
import javax.inject.Inject
import javax.inject.Singleton


interface Deps{
   fun getString()="String"
}
class TestSimpleFragment : Fragment(){
        //, InjectorHolder<TestSimpeFragmentInjector> {
    @Inject
    lateinit var message: String

    fun testInject() {
//        inject()
    }

//    override fun getInjectorFactory() = locateInjector()
}

@Component(dependencies = [Deps::class])
interface TestInjectorHolderActivityComponent2 {
    @Component.Factory
    interface Factory {
        fun create(Deps: Deps):TestInjectorHolderActivityComponent2
    }
}