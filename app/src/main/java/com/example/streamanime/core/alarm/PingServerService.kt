package com.example.streamanime.core.alarm

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.example.streamanime.domain.repository.AnimeServicesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class PingServerService: Service() {

    @Inject
    lateinit var remoteRepo: AnimeServicesRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runBlocking {
            val a = remoteRepo.pingServer()
        }
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? = null
}