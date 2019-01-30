package com.gen.genqu3;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class NotificationReceiver2 extends BroadcastReceiver {

    String URL= "http://192.168.43.43/Android_Login/updatemessage.php";
    String URL2= "http://192.168.43.43/Android_Login/expired.php";

    JSONParser2 jsonParser=new JSONParser2();

    Context con;
    Intent inte;
    int notif_id;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        con = context;
        inte = intent;
        notif_id = intent.getIntExtra("NOTIFICATION",-1);
        String uid = intent.getStringExtra("UID");

        if (intent.getIntExtra(Config.KEY_INTENT_HELP, -1) == Config.REQUEST_CODE_HELP0) {
            Toast.makeText(context, "Your transaction has been CANCELED!", Toast.LENGTH_LONG).show();
            Log.e("API1234","Can't Go Yet");
            Log.e("API1235",""+notif_id);
            Log.e("API1235",""+uid);
            NotificationReceiver2.updateMes getCompany= new NotificationReceiver2.updateMes();
            getCompany.execute(uid,"Cannot Go");

            NotificationReceiver2.updateStat getCompany1= new NotificationReceiver2.updateStat();
            getCompany1.execute(uid,"Expired");
        }
        else{
            Toast.makeText(context, "Message Sent.. Thank you!", Toast.LENGTH_LONG).show();
            Log.e("API1234","Can Go");
            Log.e("API1235",""+notif_id);
            Log.e("API1235",""+uid);
            NotificationReceiver2.updateMes getCompany= new NotificationReceiver2.updateMes();
            getCompany.execute(uid,"Can Go");
        }
    }

    private class updateMes extends AsyncTask<String, String, JSONArray> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected JSONArray doInBackground(String... args) {

            String id = args[0];
            String message = args[1];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("message", message));

            JSONArray json = jsonParser.makeHttpRequest(URL, params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {
            NotificationManager notif = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
            notif.cancel(notif_id);
        }

    }

    private class updateStat extends AsyncTask<String, String, JSONArray> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected JSONArray doInBackground(String... args) {

            String id = args[0];
            String status = args[1];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("status",status));

            JSONArray json = jsonParser.makeHttpRequest(URL2, params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {

        }

    }
}
