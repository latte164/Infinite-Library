package com.fblaproject.william.infinitelibrary;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import com.evernote.android.job.Job;
import java.util.Random;

/**
 * Runs when the job is executed. Shows notification
 */

public class OverdueBookJob extends Job {

    static final String JOB_TAG = "overdue_job";
    static long[] vib = {0,1000,1000,1000,1000}; //Vibration pattern

    @NonNull
    @Override
    protected Result onRunJob(Params params) {

        //Build and display notification
        //Tries to display on lock screen but it depends on the settings of the phone

        //Declare notification intent
        PendingIntent notificationIntent = PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), MainActivity.class), 0);

        //Screen Wakeup code
        PowerManager powerManager = (PowerManager)  getContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "Overdue Book Notification");
        wakeLock.acquire();

        //Build and display notification
        Notification builder = new NotificationCompat.Builder(getContext())
                .setContentTitle("Infinite Library")
                .setContentText("Overdue Book Warning: One of your books is due soon; open the app the check which one!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setContentIntent(notificationIntent)
                .setShowWhen(true)
                .setVibrate(vib)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        //Start notification
        NotificationManagerCompat.from(getContext())
                .notify(new Random().nextInt(), builder);

        //Close wakeup
        wakeLock.release();

        return Result.SUCCESS;
    }
}
