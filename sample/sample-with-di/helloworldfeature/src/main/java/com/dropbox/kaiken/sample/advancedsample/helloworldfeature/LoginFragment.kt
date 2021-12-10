package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.sample_with_di.helloworldfeature.R
import com.dropbox.kaiken.skeleton.scoping.AuthAwareFragment
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalActivityComponent
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalActivityScope
import com.dropbox.kaiken.skeleton.scoping.DependencyProviderResolver
import com.dropbox.kaiken.skeleton.usermanagement.UserManager
import com.dropbox.kaiken.skeleton.usermanagement.auth.UserInput
import com.squareup.anvil.annotations.ContributesTo
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

fun DependencyProviderResolver.daggerInjector() =
    InjectorFactory { (resolveDependencyProvider() as AuthOptionalActivityComponent.ParentComponent).createAuthOptionalComponent() as HellowWorldManualInjector }

@ContributesTo(AuthOptionalActivityScope::class)
interface HellowWorldManualInjector : Injector {
    fun inject(fragment: LoginFragment)
}

class LoginFragment : Fragment(), AuthAwareFragment, InjectorHolder<HellowWorldManualInjector> {
    override fun getInjectorFactory(): InjectorFactory<HellowWorldManualInjector> = daggerInjector()

    @Inject
    lateinit var helloWorldMessageProvider: HelloWorldMessageProvider

    @Inject
    lateinit var timeMessageProvider: TimeMessageProvider

    @Inject
    lateinit var intentFactory: @JvmSuppressWildcards (Context, String) -> Intent

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var userFlow: @JvmSuppressWildcards MutableSharedFlow<UserInput>

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
        val view = inflater.inflate(R.layout.login_fragment, null)

        val username = view.findViewById<TextView>(R.id.username)

        view.findViewById<Button>(R.id.button).setOnClickListener {
            val newActiveUserId = username.text.toString()
            MainScope().launch {
                userFlow.emit(UserInput(newActiveUserId, "Bart Simpson"))
                view.context.startActivity(intentFactory(view.context, newActiveUserId))
            }
        }

        return view
    }
}