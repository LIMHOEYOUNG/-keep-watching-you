package com.example.drowsy_pro

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class AlarmService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val TAG = "drowsylog"
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // 알람 소리 재생
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm4)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        val title = intent.getStringExtra("title") ?: "기본 제목"
        val body = intent.getStringExtra("body") ?: "기본 내용"
        // Foreground Service 시작 알림
        val notification = createNotification(title,body)
        startForeground(1, notification)
        return START_STICKY
    }
    // Foreground Service 알림 생성
    private fun createNotification(title:String,body:String): Notification {
        // 알림 채널 ID 설정
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("my_service_channel", "My Service Channel")
        } else {
            ""
        }
        // 알림 클릭 시 실행될 인텐트 설정
        val notificationIntent = Intent(this, drowsynotification::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.home_icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }
    //채널 생성 (푸시메시지 처리에 필요)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
    //알림 종료
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}
