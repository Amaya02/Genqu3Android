package com.gen.genqu3;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.gen.genqu3.R;
import com.gen.genqu3.Config;

public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;


        // notification icon
        final int icon = R.drawable.icon3;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext, Config.CHANNNEL_ID);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/notification");

        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                }
            }
        } else {
            showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
            playNotificationSound();
        }
    }


    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {

        String mes = SaveSharedPreference.getMes2(mContext);
        if(mes.equals("notify")){
            PendingIntent morePendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    Config.REQUEST_CODE_MORE,
                    new Intent(mContext, NotificationReceiver.class)
                            .putExtra(Config.KEY_INTENT_MORE, Config.REQUEST_CODE_MORE),
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            //Pending intent for a notification button help
            PendingIntent helpPendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    Config.REQUEST_CODE_HELP,
                    new Intent(mContext, NotificationReceiver.class)
                            .putExtra(Config.KEY_INTENT_HELP, Config.REQUEST_CODE_HELP),
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel mChannel = new NotificationChannel(Config.CHANNNEL_ID, Config.CHANNNEL_NAME, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            Notification notification;
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(false)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(R.drawable.icon3)
                    .addAction(0, "Can Go Now", morePendingIntent)
                    .addAction(0, "Cannot Go Yet", helpPendingIntent)
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message) .setBigContentTitle(title))
                    .build();

            notificationManager.notify(Config.NOTIFICATION_ID, notification);

        }
        else{
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel mChannel = new NotificationChannel(Config.CHANNNEL_ID, Config.CHANNNEL_NAME, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            Notification notification;
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(false)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(R.drawable.icon3)
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message) .setBigContentTitle(title))
                    .build();

            notificationManager.notify(Config.NOTIFICATION_ID, notification);
        }

    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {

        PendingIntent morePendingIntent = PendingIntent.getBroadcast(
                mContext,
                Config.REQUEST_CODE_MORE,
                new Intent(mContext, NotificationReceiver.class)
                        .putExtra(Config.KEY_INTENT_MORE, Config.REQUEST_CODE_MORE),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        //Pending intent for a notification button help
        PendingIntent helpPendingIntent = PendingIntent.getBroadcast(
                mContext,
                Config.REQUEST_CODE_HELP,
                new Intent(mContext, NotificationReceiver.class)
                        .putExtra(Config.KEY_INTENT_HELP, Config.REQUEST_CODE_HELP),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.icon3)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .addAction(0, "Can Go", morePendingIntent)
                .addAction(0, "Cannot Go Yet", helpPendingIntent)
                .setContentText(message)
                .setPriority(Notification.PRIORITY_MAX)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification);
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
