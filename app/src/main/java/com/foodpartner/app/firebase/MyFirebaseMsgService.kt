package com.foodpartner.app.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.foodpartner.app.R
import com.foodpartner.app.view.activity.HomeActivity
import com.foodpartner.app.view.activity.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMsgService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Send token to your server if needed
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Handle notification payload
        message.notification?.let {
            val title = it.title ?: "New Notification"
            val body = it.body ?: "You have a new message"
            showNotification(title, body)
        }
    }

    private fun getCustomDesign(title: String, message: String): RemoteViews {
        val remoteViews = RemoteViews(packageName, R.layout.fb_notification)
        remoteViews.setTextViewText(R.id.appName, title)
        remoteViews.setTextViewText(R.id.notData, message)
        return remoteViews
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "notification_channel_v2" // change ID to force new channel
        val notificationId = System.currentTimeMillis().toInt()

        // Intent for notification tap action
        val intent = Intent(this, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Notification builder
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_logo)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setContentTitle(title) // ensure proper heads-up support
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCustomContentView(getCustomDesign(title, message))

        // Notification Manager
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification channel setup (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "App Notifications",
                NotificationManager.IMPORTANCE_HIGH // must be HIGH for heads-up
            ).apply {
                description = "Notification channel for app alerts"
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

            notificationManager.createNotificationChannel(channel)
        }

        // Show notification
        notificationManager.notify(notificationId, builder.build())
    }
}
