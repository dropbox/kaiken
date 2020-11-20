package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dropbox.kaiken.annotation.Injectable
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.sample_with_di.helloworldfeature.R
import com.dropbox.kaiken.scoping.AuthOptionalFragment
import com.dropbox.kaiken.scoping.ViewingUserSelector
import com.dropbox.kaiken.scoping.putViewingUserSelector
import javax.inject.Inject

@Injectable
class HelloWorldFragment : Fragment(), AuthOptionalFragment, InjectorHolder<HelloWorldInternalComponent> {

    @Inject
    lateinit var helloWorldMessageProvider: HelloWorldMessageProvider

    @Inject
    lateinit var timeMessageProvider: TimeMessageProvider

    override fun getInjectorFactory(): InjectorFactory<HelloWorldInternalComponent> = daggerInjector()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (finishIfInvalidAuth()) {
            return
        }

        inject()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.helloworldfragment, null)

        val helloWorldTextView = view.findViewById<TextView>(R.id.helloWorldTextView)
        val timeTextView = view.findViewById<TextView>(R.id.timeTextView)

        helloWorldTextView.text = helloWorldMessageProvider.sayHello()
        timeTextView.text = timeMessageProvider.tellTheTime()

        return view
    }

    companion object {
        fun newInstance(
            viewingUserSelector: ViewingUserSelector
        ) = HelloWorldFragment().apply {
            arguments = Bundle().apply {
                putViewingUserSelector(viewingUserSelector)
            }
        }
    }
}
