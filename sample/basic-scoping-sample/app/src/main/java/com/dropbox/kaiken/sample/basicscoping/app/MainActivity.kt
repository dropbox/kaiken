package com.dropbox.kaiken.sample.basicscoping.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.dropbox.kaiken.basic_scoping_sample.app.R
import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.HelloWorldFragment
import com.dropbox.kaiken.skeleton.scoping.ViewingUserSelector

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity)

        val fragment = HelloWorldFragment.newInstance(
            ViewingUserSelector.fromUserId("awesome_user")
        )

        val trans: FragmentTransaction = supportFragmentManager.beginTransaction()
        trans.add(R.id.frag_container, fragment)
        trans.commit()
    }
}
