package com.example.newsapp

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

/**
 * Broadcast reciver for notification purposes
 */
class BroadcastNotify : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        /**
         * Load the picture to the notification asynchronously
         */
        Picasso.get().load(intent?.extras?.getString("urlImg")).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                val intent2 = Intent(context, Loginpageactivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                // creat the pending intent  from after user clicked on notification
                val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent2, 0)

                val builder = NotificationCompat.Builder(context, "id_news")
                    .setSmallIcon(R.drawable.novost_io)
                    .setContentTitle(intent?.extras?.getString("title"))
                    .setContentText("You have got a news about " + intent?.extras?.getString("topic"))
                    .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)


                var manager = NotificationManagerCompat.from(context)
                manager.notify(200, builder.build())

            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.i("messageBroadcast", "failed to upload")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.i("messageBroadcast", "not uploaded yet")
            }


        }


        )
    }
}

