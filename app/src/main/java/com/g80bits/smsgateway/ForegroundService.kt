package com.g80bits.smsgateway

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlin.random.Random

class ForegroundService : Service() {
    private var webSocketManager: WebSocketManager? = null
    private val idForegroundService = Random.nextInt(1, 101)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val smsService = SmsService(this)
        val url = intent?.getStringExtra(this.getString(R.string.extra_url)) ?: ""
        val id = intent?.getStringExtra(this.getString(R.string.extra_id)) ?: ""
        val stop =
            intent?.getBooleanExtra(this.getString(R.string.extra_stop_socket), false) ?: false

        if (stop && webSocketManager != null) {
            webSocketManager?.stop()
        } else {
            webSocketManager = WebSocketManager(listener = object : MessageListener {
                override fun onMessage(message: SmsModel) {
                    println(message)
                    //send sms
                    smsService.sendSms(message)
                }
            })

            val isConnected = webSocketManager?.start(url, id)
            if (isConnected == false) {
                stopSelf()
                return START_NOT_STICKY

            }
        }

        createNotification()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun createNotification() {
        val notification: Notification =
            NotificationCompat.Builder(this, this.getString(R.string.foreground_channel_id))
                .setContentTitle(this.getString(R.string.app_name))
                .setContentText(this.getString(R.string.fg_service_context_text))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build()

        startForeground(idForegroundService, notification)
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            this.getString(R.string.foreground_channel_id),
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }
}