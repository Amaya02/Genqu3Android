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

public class NotificationReceiver extends BroadcastReceiver {

    String URL= "http://192.168.1.100/Android_Login/updatemessage.php";

    JSONParser2 jsonParser=new JSONParser2();

    Context con;
    Intent inte;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        con = context;
        inte = intent;

        if (intent.getIntExtra(Config.KEY_INTENT_HELP, -1) == Config.REQUEST_CODE_HELP) {
            Toast.makeText(context, "Message sent.. Thank you!", Toast.LENGTH_LONG).show();
            Log.e("API1234","Can't Go Yet");
            NotificationReceiver.updateMes getCompany= new NotificationReceiver.updateMes();
            getCompany.execute(SaveSharedPreference.getTranId(context),"Cannot Go Yet");
        }
        else{
            Toast.makeText(context, "Message Sent.. Thank you!", Toast.LENGTH_LONG).show();
            Log.e("API1234","Can Go");
            NotificationReceiver.updateMes getCompany= new NotificationReceiver.updateMes();
            getCompany.execute(SaveSharedPreference.getTranId(context),"Can Go");
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
            notif.cancel(Config.NOTIFICATION_ID);
        }

    }
}
