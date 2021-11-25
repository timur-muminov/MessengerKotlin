package com.messengerkotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.messengerkotlin.core.enums.Status

class MainActivity : AppCompatActivity() {

    private val auth = App.instance.authenticationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navGraph = navHostFragment.navController.navInflater.inflate(R.navigation.nav_graph)

        val destination = if (auth.currentUserId != null) R.id.fragmentUsers else R.id.fragmentAuth
        navGraph.startDestination = destination
        navHostFragment.navController.graph = navGraph
    }

    override fun onResume() {
        super.onResume()
        auth.currentUserId?.let {
            App.instance.userStatusRepository.setStatus(
                it, Status.ONLINE
            )
        }
    }

    override fun onPause() {
        super.onPause()
        auth.currentUserId?.let {
            App.instance.userStatusRepository.setStatus(
                it, Status.OFFLINE
            )
        }
    }
}