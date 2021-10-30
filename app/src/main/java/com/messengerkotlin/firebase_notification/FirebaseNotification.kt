package com.messengerkotlin.firebase_notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.navigation.NavDeepLinkBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.messengerkotlin.MainActivity
import com.messengerkotlin.R

class FirebaseNotification : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val firebaseUser = FirebaseAuth.getInstance().currentUser;

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (firebaseUser != null) {
                updateToken(firebaseUser, task.result)
            }
        }
    }

    private fun updateToken(firebaseUser: FirebaseUser, refreshToken: String?) {
        FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
            .child("token")
            .setValue(refreshToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val receiverId = remoteMessage.data["receiverId"]
        val firebaseUser = FirebaseAuth.getInstance().currentUser;

        if (firebaseUser != null && receiverId == firebaseUser.uid) {
            downloadData(remoteMessage)
        }
    }

    private fun downloadData(remoteMessage: RemoteMessage) {
        val map: Map<String, String> = remoteMessage.data
        val username: String? = map["username"]
        val imageUri: String? = map["icon"]
        val message: String? = map["message"]
        val id: String? = map["userId"]

        val bundle = Bundle()
        bundle.putString("id", id)

        Glide.with(this)
            .asBitmap()
            .load(imageUri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                    sendNotification(bundle, username, message, resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }


    private fun sendNotification(
        bundle: Bundle,
        username: String?,
        message: String?,
        resource: Bitmap
    ) {
        val pendingIntent = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.fragmentChatroom)
            .setArguments(bundle)
            .createPendingIntent()

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationCompat.Builder(this, "1")
                .setSmallIcon(IconCompat.createWithBitmap(resource))
                .setAutoCancel(true)
                .setContentTitle(username)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        } else {
            TODO("VERSION.SDK_INT < M")
        }

        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel("1", "1channel", NotificationManager.IMPORTANCE_DEFAULT)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        channel.description = "description"

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)


        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(1, builder.build())
    }
}