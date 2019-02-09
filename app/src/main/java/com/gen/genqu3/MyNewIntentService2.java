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

public class MyNewIntentService2 extends IntentService {
    private static int NOTIFICATION_ID =  MainActivity.notifnum2;
    String URL= "http://genqu3.000webhostapp.com/Android_Login/getalarm.php";

    JSONParser2 jsonParser=new JSONParser2();
    Intent inte;

    String notif3;

    public MyNewIntentService2() {
        super("MyNewIntentService2");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MainActivity.notifnum2++;
        NOTIFICATION_ID =  MainActivity.notifnum2;
        inte = intent;

        DateFormat df = new SimpleDateFormat("HH:mm");
        Date dateN = new Date();
        String tN = df.format(dateN);
        MyNewIntentService2.getAlarm getCompany= new MyNewIntentService2.getAlarm();
        getCompany.execute(SaveSharedPreference.getUserId(this), "2",tN);
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
                    notif3 = jArray.getJSONObject(0).getString("companyname") + " - Window " + jArray.getJSONObject(0).getString("windowid") + " - " + jArray.getJSONObject(0).getString("transacname");
                    SaveSharedPreference.setTranId(MyNewIntentService2.this,jArray.getJSONObject(0).getString("u_tranid"));

                    PendingIntent morePendingIntent = PendingIntent.getBroadcast(
                            MyNewIntentService2.this,
                            Config.REQUEST_CODE_MORE1,
                            new Intent(MyNewIntentService2.this, NotificationReceiver2.class)
                                    .putExtra(Config.KEY_INTENT_MORE, Config.REQUEST_CODE_MORE0)
                                    .putExtra("NOTIFICATION",Config.NOTIFICATION_ID2)
                                    .putExtra("UID",jArray.getJSONObject(0).getString("u_tranid")),
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

                    //Pending intent for a notification button help
                    PendingIntent helpPendingIntent = PendingIntent.getBroadcast(
                            MyNewIntentService2.this,
                            Config.REQUEST_CODE_HELP1,
                            new Intent(MyNewIntentService2.this, NotificationReceiver2.class)
                                    .putExtra(Config.KEY_INTENT_HELP, Config.REQUEST_CODE_HELP0)
                                    .putExtra("NOTIFICATION",Config.NOTIFICATION_ID2)
                                    .putExtra("UID",jArray.getJSONObject(0).getString("u_tranid")),
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    int importance = NotificationManager.IMPORTANCE_HIGH;

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        NotificationChannel mChannel = new NotificationChannel(Config.CHANNNEL_ID, Config.CHANNNEL_NAME, importance);
                        notificationManager.createNotificationChannel(mChannel);
                    }

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyNewIntentService2.this, Config.CHANNNEL_ID)
                            .setSmallIcon(R.drawable.icon3)
                            .setContentTitle(notif3)
                            .setContentText(notif3+"\n2 MINUTES BEFORE YOUR TURN!")
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .addAction(0, "Can Go", morePendingIntent)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(notif3+"\n2 MINUTES BEFORE YOUR TURN!") .setBigContentTitle(notif3))
                            .addAction(0, "Cancel Transaction", helpPendingIntent);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(MyNewIntentService2.this);
                    stackBuilder.addNextIntent(inte);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(2, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    notificationManager.notify(Config.NOTIFICATION_ID2, mBuilder.build());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
