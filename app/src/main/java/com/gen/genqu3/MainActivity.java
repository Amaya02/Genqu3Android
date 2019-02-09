package com.gen.genqu3;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    public static String userid;
    public static String date_notif;
    public static String time_notif;
    public static int notifnum =0;
    public static int notifnum2 =0;
    public static String mes_notif;

    String URL= "http://genqu3.000webhostapp.com/Android_Login/restartalarm.php";
    String URL2= "http://genqu3.000webhostapp.com/Android_Login/updatetoken.php";

    JSONParser2 jsonParser=new JSONParser2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                if(CheckNetwork.isAvail(MainActivity.this)){
                    if(SaveSharedPreference.getUserName(MainActivity.this).length()==0) {
                        Intent homeIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(homeIntent);
                        finish();
                    }
                    else{
                        notifnum =0;
                        notifnum2 =0;
                        String id = SaveSharedPreference.getUserId(MainActivity.this);
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                String token = instanceIdResult.getToken();
                                Log.d("FCM_TOKEN",token);
                                storeRegIdInPref(token);
                                MainActivity.EditToken getCompany= new MainActivity.EditToken();
                                getCompany.execute(SaveSharedPreference.getUserId(MainActivity.this),token);
                            }
                        });
                        MainActivity.RestartAlarm getCompany= new MainActivity.RestartAlarm();
                        getCompany.execute(id);
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(MainActivity.this);
                    }
                    builder.setTitle("Genqu3")
                            .setCancelable(false)
                            .setMessage("No Internet Connection!")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent= new Intent(MainActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setIcon(R.drawable.icon3)
                            .show();
                }
            }
        },SPLASH_TIME_OUT);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }

    private class EditToken extends AsyncTask<String, String, JSONArray> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected JSONArray doInBackground(String... args) {

            String id = args[0];
            String token = args[1];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", token));
            params.add(new BasicNameValuePair("id", id));

            JSONArray json = jsonParser.makeHttpRequest(URL2, params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {

        }

    }

    private class RestartAlarm extends AsyncTask<String, String, JSONArray> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected JSONArray doInBackground(String... args) {

            String id = args[0];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));

            JSONArray json = jsonParser.makeHttpRequest(URL, params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {

            try {
                if(!jArray.getJSONObject(0).getString("result").equals("empty")) {

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);

                        String dateNow = jArray.getJSONObject(0).getString("date_now");
                        String timeNow = jArray.getJSONObject(0).getString("time_now");
                        String date = json_data.getString("esti_date");
                        String time = json_data.getString("esti_start");

                        int day = Integer.parseInt(date.substring(8,10));
                        int month = Integer.parseInt(date.substring(5,7));
                        int year =Integer.parseInt(date.substring(0,4));
                        int hour = Integer.parseInt(time.substring(0,2));
                        int min = Integer.parseInt(time.substring(3,5));

                        if(dateNow.equals(date)){
                            long difference = 0;
                            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                            Date date1 = format.parse(timeNow);
                            Date date2 = format.parse(time);
                            difference = date2.getTime() - date1.getTime();
                            difference = difference/1000;

                            if(difference>3600){
                                MainActivity.notifnum++;

                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 60,0);

                                AlarmManager [] alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                Intent intents[] = new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver.class);
                                intents[MainActivity.notifnum].putExtra("ID",0);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 30,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver5.class);
                                intents[MainActivity.notifnum].putExtra("ID",3);
                                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 10,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver4.class);
                                intents[MainActivity.notifnum].putExtra("ID",3);
                                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 2,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver2.class);
                                intents[MainActivity.notifnum].putExtra("ID",3);
                                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min ,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver3.class);
                                intents[MainActivity.notifnum].putExtra("ID",4);
                                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                            }
                            else if(difference>1800){
                                MainActivity.notifnum++;

                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 30,0);

                                AlarmManager [] alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                Intent intents[] = new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver5.class);
                                intents[MainActivity.notifnum].putExtra("ID",0);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 10,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver4.class);
                                intents[MainActivity.notifnum].putExtra("ID",3);
                                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 2,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver2.class);
                                intents[MainActivity.notifnum].putExtra("ID",3);
                                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min ,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver3.class);
                                intents[MainActivity.notifnum].putExtra("ID",4);
                                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                            }
                            else if(difference>600){
                                MainActivity.notifnum++;

                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 10,0);

                                AlarmManager [] alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                Intent intents[] = new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver4.class);
                                intents[MainActivity.notifnum].putExtra("ID",0);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 2,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver2.class);
                                intents[MainActivity.notifnum].putExtra("ID",3);
                                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min ,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver3.class);
                                intents[MainActivity.notifnum].putExtra("ID",4);
                                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                            }
                            else if(difference>120){
                                MainActivity.notifnum++;

                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 2,0);

                                AlarmManager [] alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                Intent intents[] = new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver2.class);
                                intents[MainActivity.notifnum].putExtra("ID",3);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min ,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver3.class);
                                intents[MainActivity.notifnum].putExtra("ID",4);
                                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                            }
                            else{
                                if(difference>0){
                                    MainActivity.notifnum++;

                                    Calendar c = Calendar.getInstance();
                                    c.setTimeInMillis(System.currentTimeMillis());
                                    c.clear();
                                    c.set(year, (month-1), day, hour, min ,0);

                                    AlarmManager [] alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                    Intent intents[]= new Intent[MainActivity.notifnum+1];

                                    intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver3.class);
                                    intents[MainActivity.notifnum].putExtra("ID",4);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                    alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                    alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                                }
                            }
                        }
                        else{
                            MainActivity.notifnum++;

                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            c.clear();
                            c.set(year, (month-1), day, hour, min - 60,0);

                            AlarmManager [] alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                            Intent intents[] = new Intent[MainActivity.notifnum+1];

                            intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver.class);
                            intents[MainActivity.notifnum].putExtra("ID",0);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                            alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                            MainActivity.notifnum++;

                            c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            c.clear();
                            c.set(year, (month-1), day, hour, min - 30,0);

                            alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                            intents= new Intent[MainActivity.notifnum+1];

                            intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver5.class);
                            intents[MainActivity.notifnum].putExtra("ID",3);
                            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                            alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                            MainActivity.notifnum++;

                            c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            c.clear();
                            c.set(year, (month-1), day, hour, min - 10,0);

                            alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                            intents= new Intent[MainActivity.notifnum+1];

                            intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver4.class);
                            intents[MainActivity.notifnum].putExtra("ID",3);
                            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                            alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                            MainActivity.notifnum++;

                            c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            c.clear();
                            c.set(year, (month-1), day, hour, min - 2,0);

                            alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                            intents= new Intent[MainActivity.notifnum+1];

                            intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver2.class);
                            intents[MainActivity.notifnum].putExtra("ID",3);
                            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                            alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                            MainActivity.notifnum++;

                            c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            c.clear();
                            c.set(year, (month-1), day, hour, min ,0);

                            alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                            intents= new Intent[MainActivity.notifnum+1];

                            intents[MainActivity.notifnum] = new Intent(MainActivity.this, AlarmReceiver3.class);
                            intents[MainActivity.notifnum].putExtra("ID",4);
                            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                            alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

}
