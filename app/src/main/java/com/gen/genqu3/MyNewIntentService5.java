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

public class MyNewIntentService5 extends IntentService {
    private static int NOTIFICATION_ID =  MainActivity.notifnum2;
    String URL= "http://192.168.43.43/Android_Login/getalarm.php";
    String notif3;

    JSONParser2 jsonParser=new JSONParser2();
    Intent inte;

    public MyNewIntentService5() {
        super("MyNewIntentService5");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MainActivity.notifnum2++;
        NOTIFICATION_ID =  MainActivity.notifnum2;
        inte = intent;

        DateFormat df = new SimpleDateFormat("HH:mm");
        Date dateN = new Date();
        String tN = df.format(dateN);
        MyNewIntentService5.getAlarm getCompany= new MyNewIntentService5.getAlarm();
        getCompany.execute(SaveSharedPreference.getUserId(this),"4",tN);

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
                    SaveSharedPreference.setTranId(MyNewIntentService5.this,jArray.getJSONObject(0).getString("u_tranid"));

                    PendingIntent morePendingIntent = PendingIntent.getBroadcast(
                            MyNewIntentService5.this,
                            Config.REQUEST_CODE_MORE4,
                            new Intent(MyNewIntentService5.this, NotificationReceiver2.class)
                                    .putExtra(Config.KEY_INTENT_MORE, Config.REQUEST_CODE_MORE0)
                                    .putExtra("NOTIFICATION",Config.NOTIFICATION_ID5)
                                    .putExtra("UID",jArray.getJSONObject(0).getString("u_tranid")),
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

                    //Pending intent for a notification button help
                    PendingIntent helpPendingIntent = PendingIntent.getBroadcast(
                            MyNewIntentService5.this,
                            Config.REQUEST_CODE_HELP4,
                            new Intent(MyNewIntentService5.this, NotificationReceiver2.class)
                                    .putExtra(Config.KEY_INTENT_HELP, Config.REQUEST_CODE_HELP0)
                                    .putExtra("NOTIFICATION",Config.NOTIFICATION_ID5)
                                    .putExtra("UID",jArray.getJSONObject(0).getString("u_tranid")),
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    int importance = NotificationManager.IMPORTANCE_HIGH;

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        NotificationChannel mChannel = new NotificationChannel(Config.CHANNNEL_ID, Config.CHANNNEL_NAME, importance);
                        notificationManager.createNotificationChannel(mChannel);
                    }

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyNewIntentService5.this, Config.CHANNNEL_ID)
                            .setSmallIcon(R.drawable.icon3)
                            .setContentTitle(notif3)
                            .setContentText(notif3+"\n30 MINUTES BEFORE YOUR TURN!")
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .addAction(0, "Can Go", morePendingIntent)
                            .addAction(0, "Cannot Go", helpPendingIntent);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(MyNewIntentService5.this);
                    stackBuilder.addNextIntent(inte);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(5, PendingIntent.FLAG_CANCEL_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    notificationManager.notify(Config.NOTIFICATION_ID5, mBuilder.build());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}