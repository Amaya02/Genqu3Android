package com.gen.genqu3;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BroadcastManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String yourDate = MainActivity.date_notif;
            String yourHour = MainActivity.time_notif;
            Date d = new Date();
            DateFormat date = new SimpleDateFormat("yyyy/MM/dd");
            DateFormat hour = new SimpleDateFormat("HH:mm:ss");
            if (date.equals(yourDate) && hour.equals(yourHour)) {
                Intent it = new Intent(context, MainActivity.class);
                createNotification(context, it, "new mensage", "body!", "this is a mensage");
            }
        } catch (Exception e) {
            Log.i("date", "error == " + e.getMessage());
        }
    }


    public void createNotification(Context context, Intent intent, CharSequence ticker, CharSequence title, CharSequence descricao) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent p = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker(ticker);
        builder.setContentTitle(title);
        builder.setContentText(descricao);
        builder.setSmallIcon(R.drawable.icon3);
        builder.setContentIntent(p);
        Notification n = builder.build();
        //create the notification
        n.vibrate = new long[]{150, 300, 150, 400};
        n.flags = Notification.FLAG_AUTO_CANCEL;
        nm.notify(R.drawable.icon3, n);
        //create a vibration
        try {

            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(context, som);
            toque.play();
        } catch (Exception e) {
        }
    }
}
