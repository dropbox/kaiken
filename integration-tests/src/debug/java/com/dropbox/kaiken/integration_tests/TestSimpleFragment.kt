package com.dropbox.kaiken.integration_tests

import androidx.fragment.app.Fragment
import com.dropbox.kaiken.annotation.AutoInjectable
import com.dropbox.kaiken.annotation.Injectable
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import dagger.Component
import javax.inject.Inject


interface Deps{
   fun getString()="String"
}
//@AutoInjectable(dependency = Deps::class)
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