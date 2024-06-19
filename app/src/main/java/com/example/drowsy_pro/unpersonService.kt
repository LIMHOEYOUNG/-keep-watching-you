package com.example.drowsy_pro

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class unpersonService : Service() {
    private val TAG = "drowsylog"
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val title = intent.getStringExtra("title") ?: "기본 제목"
        val body = intent.getStringExtra("body") ?: "기본 내용"
        val unpersontime = intent.getStringExtra("UNPERSON_TIME") ?: "2024"
        val unpersonla = intent.getFloatExtra("UNPERSONLA", 0F)
        val unpersonlo = intent.getFloatExtra("UNPERSONLO", 0F)
        val unpersonid = intent.getIntExtra("ITEM_ID", 0)

        // Foreground Service 시작 알림
        val notification = createNotification(title, body, unpersontime, unpersonla, unpersonlo, unpersonid)
        startForeground(1, notification)
        return START_STICKY
    }

    // Foreground Service 알림 생성
    private fun createNotification(title: String, body: String, time: String, la: Float, lo: Float, id: Int): Notification {
        // 알림 채널 ID 설정
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("my_service_channel_$id", "My Service Channel $id")
        } else {
            ""
        }

        // 알림 클릭 시 실행될 인텐트 설정
        val notificationIntent = Intent(this, unperson::class.java).apply {
            putExtra("ITEM_ID", id)
            putExtra("UNPERSONLO", lo)
            putExtra("UNPERSONLA", la)
            putExtra("UNPERSON_TIME", formatDateTime(time))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        // 알림 소리 URI 설정
        val alarmSound: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.alarm3)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.drowzy_icon)
            .setContentIntent(pendingIntent)
            .setSound(alarmSound)
            .setVibrate(longArrayOf(0, 1000)) // 진동 패턴 수정
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
    }

    //채널 생성 ("Data" 메시지 처리에 필요)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val alarmSound: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.alarm3)
        chan.setSound(alarmSound, Notification.AUDIO_ATTRIBUTES_DEFAULT)
        chan.enableVibration(true)
        chan.vibrationPattern = longArrayOf(0, 1000) // 진동 패턴 수정
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    //시간 문자 정리
    fun formatDateTime(dateTime: String): String {
        // 밀리초 포함 포맷
        val inputFormatWithMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormatWithMillis.timeZone = TimeZone.getTimeZone("UTC")

        // 밀리초 미포함 포맷
        val inputFormatWithoutMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputFormatWithoutMillis.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()

        // 날짜 파싱 시도 (밀리초 포함)
        try {
            val date = inputFormatWithMillis.parse(dateTime)
            return outputFormat.format(date)
        } catch (e: ParseException) {
            try {
                val date = inputFormatWithoutMillis.parse(dateTime)
                return outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
                return dateTime
            }
        }
    }
}
