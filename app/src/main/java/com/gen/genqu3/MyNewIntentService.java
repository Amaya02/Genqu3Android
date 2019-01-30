package com.gen.genqu3;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.RingtonePreference;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyNewIntentService extends IntentService {
    private static int NOTIFICATION_ID =  MainActivity.notifnum2;
    String URL= "http://192.168.1.101/Android_Login/getalarm.php";

    JSONParser2 jsonParser=new JSONParser2();

    String notif3;
    Intent inte;

    public MyNewIntentService() {
        super("MyNewIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MainActivity.notifnum2++;
        NOTIFICATION_ID =  MainActivity.notifnum2;
        inte = intent;

        DateFormat df = new SimpleDateFormat("HH:mm");
        Date dateN = new Date();
        String tN = df.format(dateN);
        MyNewIntentService.getAlarm getCompany= new MyNewIntentService.getAlarm();
        getCompany.execute(SaveSharedPreference.getUserId(this), "1",tN);
    }

    private class getAlarm extends AsyncTask<String, String, JSONArray> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected JSONArray doInBackground(String... args) {

            String id = args[0];
            String alarm = args[1];
            String tN = args[2];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("alarm", alarm));
            params.add(new BasicNameValuePair("tN", tN));

            JSONArray json = jsonParser.makeHttpRequest(URL, params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {
            try {
                if(!jArray.getJSONObject(0).getString("result").equals("empty")){
                    notif3 = jArray.getJSONObject(0).getString("companyname") + " - Window " + jArray.getJSONObject(0).getString("transacid") + " - " + jArray.getJSONObject(0).getString("transacname");

                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    int importance = NotificationManager.IMPORTANCE_HIGH;

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        NotificationChannel mChannel = new NotificationChannel(Config.CHANNNEL_ID, Config.CHANNNEL_NAME, importance);
                        notificationManager.createNotificationChannel(mChannel);
                    }

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyNewIntentService.this, Config.CHANNNEL_ID)
                            .setSmallIcon(R.drawable.icon3)
                            .setContentTitle(notif3)
                            .setContentText("1 hour before your turn!- "+notif3)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(MyNewIntentService.this);
                    stackBuilder.addNextIntent(inte);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(2, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }
                else{
                    Notification.Builder builder = new Notification.Builder(MyNewIntentService.this);
                    builder.setContentTitle("Genqu3 Notification");
                    builder.setContentText("1 hour before your turn!");
                    builder.setSmallIcon(R.drawable.icon3);
                    builder.setPriority(Notification.PRIORITY_HIGH);
                    builder.setDefaults(Notification.DEFAULT_ALL);
                    builder.setAutoCancel(true);
                    long[] pattern = {500,500,500,500,500,500,500,500,500,500};
                    builder.setVibrate(pattern);
                    Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    builder.setSound(alarmsound);

                    Intent notifyIntent = new Intent(MyNewIntentService.this, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(MyNewIntentService.this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    builder.setContentIntent(pendingIntent);
                    Notification notificationCompat = builder.build();
                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MyNewIntentService.this);
                    managerCompat.notify(NOTIFICATION_ID, notificationCompat);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
