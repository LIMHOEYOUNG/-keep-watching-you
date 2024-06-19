package com.example.drowsy_pro

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// FCM 처리
class FCMservice : FirebaseMessagingService() {

    private val TAG = "drowsylog"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "fcm token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val titleback: String? = message.data["title"]
        val bodyback: String? = message.data["body"]
        val clickAction: String? = message.data["click_action"]
        val id: String? = message.data["id"]
        val lati: String? = message.data["lati"]
        val longi: String? = message.data["longi"]
        val time: String? = message.data["time"]

        Log.d(TAG, "Message data payload: ${message.data}")

        when (clickAction) {
            "com.example.drowsy_pro.drowsynotification" -> {
                Log.d(TAG, "Message data payload: 1")
                startAlarmService(titleback, bodyback)
            }
            "com.example.drowsy_pro.unperson" -> {
                Log.d(TAG, "Message data payload: 2")
                unpersonNotification(titleback, bodyback, id, lati, longi, time)
            }
            else -> {
                Log.d(TAG, "Unknown click action received")
            }
        }
    }

    private fun startAlarmService(title: String?, body: String?) {
        val serviceIntent = Intent(this, AlarmService::class.java).apply {
            putExtra("title", title)
            putExtra("body", body)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }
    private fun unpersonNotification(title: String?, body: String?,id:String?,lati:String?,longi:String?,time:String?) {
        val serviceIntent = Intent(this, unpersonService::class.java).apply {
            putExtra("title", title)
            putExtra("body", body)
            putExtra("ITEM_ID", id?.toInt())
            putExtra("UNPERSONLO", longi?.toFloat())
            putExtra("UNPERSONLA", lati?.toFloat())
            putExtra("UNPERSON_TIME", time)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }

}
