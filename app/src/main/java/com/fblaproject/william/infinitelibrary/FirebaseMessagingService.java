package com.fblaproject.william.infinitelibrary;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

/**
 * Handles Firebase notifications
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {

        long[] vib = {0,1000,1000,1000,1000}; //Vibration pattern

        //Build and display notification
        //Tries to display on lock screen but it depends on the settings of the phone

        //Declare notification intent
        PendingIntent notificationIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), 0);

        //Screen Wakeup code
        PowerManager powerManager = (PowerManager)  getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "Watchlist Book Notification");
        wakeLock.acquire();

        //Build and display notification
        Notification builder = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Infinite Library")
                .setContentText(message.getData().get("m"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setContentIntent(notificationIntent)
                .setShowWhen(true)
                .setVibrate(vib)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        //Start notification
        NotificationManagerCompat.from(getApplicationContext())
                .notify(new Random().nextInt(), builder);

        //Close wakeup
        wakeLock.release();

    }

}
