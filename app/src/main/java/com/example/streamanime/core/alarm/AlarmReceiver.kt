package com.example.streamanime.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.streamanime.domain.repository.AnimeServicesRepository
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var remoteRepo: AnimeServicesRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        runBlocking {
            val a = remoteRepo.pingServer()
        }
    }
}