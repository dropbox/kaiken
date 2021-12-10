package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.sample_with_di.helloworldfeature.R
import com.dropbox.kaiken.scoping.AuthAwareFragment
import com.dropbox.kaiken.skeleton.scoping.AuthRequiredActivityComponent
import com.dropbox.kaiken.skeleton.scoping.AuthRequiredActivityScope
import com.dropbox.kaiken.scoping.DependencyProviderResolver
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.dropbox.kaiken.skeleton.scoping.UserScope
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Inject

fun DependencyProviderResolver.daggerInjectorAuth() =
    InjectorFactory { (resolveDependencyProvider() as AuthRequiredActivityComponent.ParentComponent).createAuthRequiredComponent() as HellowWorldAuthedManualInjector }

@ContributesTo(AuthRequiredActivityScope::class)
interface HellowWorldAuthedManualInjector :
    Injector {
    fun inject(fragment: HelloWorldFragmentAuthed)
}

class HelloWorldFragmentAuthed :
    Fragment(),
    AuthAwareFragment,
    InjectorHolder<HellowWorldAuthedManualInjector> {

    @Inject
    @SingleIn(UserScope::class)
    lateinit var helloWorldMessageProvider: HelloWorldMessageProviderUser

    @Inject
    lateinit var timeMessageProvider: TimeMessageProvider

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (finishIfInvalidAuth()) return
        locateInjector().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.helloworldfragment, null)

        val helloWorldTextView = view.findViewById<TextView>(R.id.helloWorldTextView)
        val timeTextView = view.findViewById<TextView>(R.id.timeTextView)

        helloWorldTextView.text = helloWorldMessageProvider.sayHello()
        timeTextView.text = timeMessageProvider.tellTheTime()

        return view
    }

    override fun getInjectorFactory(): InjectorFactory<HellowWorldAuthedManualInjector> =
        daggerInjectorAuth()
}
