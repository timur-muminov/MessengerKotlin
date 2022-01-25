package com.messengerkotlin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.messengerkotlin.core.enums.Status
import com.messengerkotlin.firebase_repository.UserStatusRepository
import com.messengerkotlin.firebase_repository.auth_manager.AuthenticationManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val auth: AuthenticationManager by inject()
    private val userStatusRepository: UserStatusRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navGraph = navHostFragment.navController.navInflater.inflate(R.navigation.nav_graph)

        val destination = if (auth.currentUserId != null) {
            R.id.fragmentUsers
        } else {
            R.id.fragmentAuth
        }
        navGraph.startDestination = destination
        navHostFragment.navController.graph = navGraph
    }

    override fun onResume() {
        super.onResume()
        auth.currentUserId?.let {
            lifecycleScope.launch {
                userStatusRepository.setStatus(it, Status.ONLINE)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        auth.currentUserId?.let {
            lifecycleScope.launch {
                userStatusRepository.setStatus(it, Status.OFFLINE)
            }
        }
    }
}