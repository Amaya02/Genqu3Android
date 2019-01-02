package com.gen.genqu3;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.RingtonePreference;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class MyNewIntentService2 extends IntentService {
    private static int NOTIFICATION_ID =  MainActivity.notifnum2;

    public MyNewIntentService2() {
        super("MyNewIntentService2");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MainActivity.notifnum2++;
        NOTIFICATION_ID =  MainActivity.notifnum2;

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Queue Notification");
        builder.setContentText("2 minutes before your turn!");
        builder.setSmallIcon(R.drawable.icon3);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setAutoCancel(true);
        long[] pattern = {500,500,500,500,500,500,500,500,500,500};
        builder.setVibrate(pattern);
        Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmsound);

        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, notificationCompat);
    }
}
