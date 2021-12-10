package com.dropbox.kaiken.sample.basicscoping.helloworldfeatue

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dropbox.kaiken.basic_scoping_sample.helloworldfeature.R
import com.dropbox.kaiken.scoping.AuthOptionalFragment
import com.dropbox.kaiken.scoping.ViewingUserSelector
import com.dropbox.kaiken.scoping.putViewingUserSelector

class HelloWorldFragment : Fragment(), AuthOptionalFragment {

    private lateinit var helloWorldMessageProvider: HelloWorldMessageProvider
    private lateinit var timeMessageProvider: TimeMessageProvider

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (finishIfInvalidAuth()) {
            return
        }

        val dependencies: HelloWorldDependencies = resolveDependencyProvider()

        helloWorldMessageProvider = dependencies.helloWorldMessageProvider
        timeMessageProvider = dependencies.timeMessageProvider
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
