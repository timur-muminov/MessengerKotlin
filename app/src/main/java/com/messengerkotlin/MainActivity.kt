package com.messengerkotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.messengerkotlin.core.enums.Status
import com.messengerkotlin.firebase_repository.UserStatusRepository
import com.messengerkotlin.firebase_repository.auth_manager.AuthenticationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var auth: AuthenticationManager
    @Inject lateinit var userStatusRepository: UserStatusRepository

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