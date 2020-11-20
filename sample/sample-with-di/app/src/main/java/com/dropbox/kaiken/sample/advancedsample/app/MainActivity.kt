package com.dropbox.kaiken.sample.advancedsample.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.HelloWorldFragment
import com.dropbox.kaiken.sample_with_di.app.R
import com.dropbox.kaiken.scoping.ViewingUserSelector


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity);

        val fragment = HelloWorldFragment.newInstance(
            ViewingUserSelector.fromUserId("awesome_user"))

        val trans: FragmentTransaction = supportFragmentManager.beginTransaction()
        trans.add(R.id.frag_container, fragment)
        trans.commit()
    }
}
