package com.gen.genqu3;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

public class ConfirmTransac extends AppCompatActivity {

    Button t_back, t_confirm;
    TextView t_name,t_time,t_esti,t_arriv,t_date;
    Spinner spinner;
    String dateNow, timeNow;

    String URL= "http://192.168.1.100/Android_Login/confirmtransaction.php";
    String URL2= "http://192.168.1.100/Android_Login/addtransaction.php";
    String URL3= "http://192.168.1.100/Android_Login/updatetoken.php";

    JSONParser2 jsonParser=new JSONParser2();

    String cn, transacname, starttime, endtime, estimatedtime, transacid;

    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_transac);

        Intent intent = getIntent();

        transacname = intent.getStringExtra("TRANSACNAME");
        starttime = intent.getStringExtra("STARTTIME");
        endtime = intent.getStringExtra("ENDTIME");
        estimatedtime = intent.getStringExtra("ESTIMATEDTIME");
        transacid = intent.getStringExtra("TRANSACID");
        cn = intent.getStringExtra("COMPANYNAME");

        t_name = (TextView) findViewById(R.id.t_name);

        t_time = (TextView) findViewById(R.id.t_time);
        t_esti = (TextView) findViewById(R.id.t_esti);

        t_back = (Button) findViewById(R.id.t_back);
        t_confirm = (Button) findViewById(R.id.t_confirm);

        t_name.setText(transacname);
        t_time.setText("TIME: "+starttime+" - "+endtime);
        t_esti.setText("ESTIMATED TIME: "+estimatedtime+" Minute/s");

        t_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        progress = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progress.setTitle("Loading");
        progress.setMessage("Please wait..");
        progress.setCancelable(false);

        progress.show();

        ConfirmTransac.GetTran getCompany= new ConfirmTransac.GetTran();
        getCompany.execute(starttime,endtime,estimatedtime,transacid);
    }

    private class GetTran extends AsyncTask<String, String, JSONArray> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected JSONArray doInBackground(String... args) {

            String starttime = args[0];
            String endtime = args[1];
            String estitime = args[2];
            String transacid = args[3];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("starttime", starttime));
            params.add(new BasicNameValuePair("endtime", endtime));
            params.add(new BasicNameValuePair("estitime", estitime));
            params.add(new BasicNameValuePair("transacid", transacid));

            JSONArray json = jsonParser.makeHttpRequest(URL,params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {
            try {
                Intent intent = getIntent();
                final String transacid = intent.getStringExtra("TRANSACID");
                t_date = (TextView) findViewById(R.id.t_date);

                final String date = jArray.getJSONObject(0).getString("esti_date");
                dateNow = jArray.getJSONObject(0).getString("date_now");
                timeNow = jArray.getJSONObject(0).getString("time_now");
                t_date.setText("DATE: "+date);

                spinner = (Spinner) findViewById(R.id.spinner);
                List<String> list = new ArrayList<String>();

                for(int i = 0; i < jArray.length(); i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    list.add(json_data.getString("estistart"));
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ConfirmTransac.this, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);

                MainActivity.date_notif = date;


                progress.dismiss();

                t_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String arriv = String.valueOf(spinner.getSelectedItem());
                        MainActivity.time_notif = arriv;

                        progress.show();
                        ConfirmTransac.AddUserTran getCompany= new ConfirmTransac.AddUserTran();
                        getCompany.execute(MainActivity.userid,transacid,"Pending",date,arriv);
                    }
                });

            } catch (JSONException e) {
                progress.dismiss();
                e.printStackTrace();
            }

        }
    }

    private class AddUserTran extends AsyncTask<String, String, JSONArray> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected JSONArray doInBackground(String... args) {

            String userid = args[0];
            String transacid = args[1];
            String status = args[2];
            String date = args[3];
            String arriv = args[4];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userid", userid));
            params.add(new BasicNameValuePair("transacid", transacid));
            params.add(new BasicNameValuePair("status", status));
            params.add(new BasicNameValuePair("date", date));
            params.add(new BasicNameValuePair("start", arriv));

            JSONArray json = jsonParser.makeHttpRequest(URL2,params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {
            try {
                if(jArray.getJSONObject(0).getString("result").equals("success")){

                    int day = Integer.parseInt(MainActivity.date_notif.substring(8,10));
                    int month = Integer.parseInt(MainActivity.date_notif.substring(5,7));
                    int year =Integer.parseInt(MainActivity.date_notif.substring(0,4));
                    int hour = Integer.parseInt(MainActivity.time_notif.substring(0,2));
                    int min = Integer.parseInt(MainActivity.time_notif.substring(3,5));

                    long difference = 0;

                    if(dateNow.equals(MainActivity.date_notif)){
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                        Date date1 = format.parse(timeNow);
                        Date date2 = format.parse(MainActivity.time_notif);
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

                            intents[MainActivity.notifnum] = new Intent(ConfirmTransac.this, AlarmReceiver.class);
                            intents[MainActivity.notifnum].putExtra("ID",0);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(ConfirmTransac.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                            alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                            MainActivity.notifnum++;

                            c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            c.clear();
                            c.set(year, (month-1), day, hour, min - 2,0);

                            alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                            intents= new Intent[MainActivity.notifnum+1];

                            intents[MainActivity.notifnum] = new Intent(ConfirmTransac.this, AlarmReceiver2.class);
                            intents[MainActivity.notifnum].putExtra("ID",3);
                            pendingIntent = PendingIntent.getBroadcast(ConfirmTransac.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                            alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                            MainActivity.notifnum++;

                            c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            c.clear();
                            c.set(year, (month-1), day, hour, min ,0);

                            alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                            intents= new Intent[MainActivity.notifnum+1];

                            intents[MainActivity.notifnum] = new Intent(ConfirmTransac.this, AlarmReceiver3.class);
                            intents[MainActivity.notifnum].putExtra("ID",4);
                            pendingIntent = PendingIntent.getBroadcast(ConfirmTransac.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
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

                            intents[MainActivity.notifnum] = new Intent(ConfirmTransac.this, AlarmReceiver2.class);
                            intents[MainActivity.notifnum].putExtra("ID",3);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(ConfirmTransac.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                            alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                            MainActivity.notifnum++;

                            c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            c.clear();
                            c.set(year, (month-1), day, hour, min ,0);

                            alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                            intents= new Intent[MainActivity.notifnum+1];

                            intents[MainActivity.notifnum] = new Intent(ConfirmTransac.this, AlarmReceiver3.class);
                            intents[MainActivity.notifnum].putExtra("ID",4);
                            pendingIntent = PendingIntent.getBroadcast(ConfirmTransac.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                            alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                        }
                        else{
                            MainActivity.notifnum++;

                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            c.clear();
                            c.set(year, (month-1), day, hour, min ,0);

                            AlarmManager [] alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                            Intent intents[]= new Intent[MainActivity.notifnum+1];

                            intents[MainActivity.notifnum] = new Intent(ConfirmTransac.this, AlarmReceiver3.class);
                            intents[MainActivity.notifnum].putExtra("ID",4);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(ConfirmTransac.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                            alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
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

                        intents[MainActivity.notifnum] = new Intent(ConfirmTransac.this, AlarmReceiver.class);
                        intents[MainActivity.notifnum].putExtra("ID",0);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(ConfirmTransac.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                        alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                        MainActivity.notifnum++;

                        c = Calendar.getInstance();
                        c.setTimeInMillis(System.currentTimeMillis());
                        c.clear();
                        c.set(year, (month-1), day, hour, min - 2,0);

                        alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                        intents= new Intent[MainActivity.notifnum+1];

                        intents[MainActivity.notifnum] = new Intent(ConfirmTransac.this, AlarmReceiver2.class);
                        intents[MainActivity.notifnum].putExtra("ID",3);
                        pendingIntent = PendingIntent.getBroadcast(ConfirmTransac.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                        alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                        MainActivity.notifnum++;

                        c = Calendar.getInstance();
                        c.setTimeInMillis(System.currentTimeMillis());
                        c.clear();
                        c.set(year, (month-1), day, hour, min ,0);

                        alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                        intents= new Intent[MainActivity.notifnum+1];

                        intents[MainActivity.notifnum] = new Intent(ConfirmTransac.this, AlarmReceiver3.class);
                        intents[MainActivity.notifnum].putExtra("ID",4);
                        pendingIntent = PendingIntent.getBroadcast(ConfirmTransac.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                        alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                    }

                    storeNotifNumInPref(MainActivity.notifnum);

                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Transaction Added Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(ConfirmTransac.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else if(jArray.getJSONObject(0).getString("result").equals("error2")){
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "OOOPS! Someone beat you to it :(", Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(ConfirmTransac.this, ConfirmTransac.class);
                    intent.putExtra("TRANSACID",transacid);
                    intent.putExtra("TRANSACNAME",transacname);
                    intent.putExtra("STARTTIME",starttime);
                    intent.putExtra("ENDTIME",endtime);
                    intent.putExtra("ESTIMATEDTIME",estimatedtime);
                    intent.putExtra("COMPANYNAME",cn);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Transaction Failed", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                progress.dismiss();
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    private void storeNotifNumInPref(int num) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("NotifNum", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("notifnum", num);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.main_menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.logout:
                SharedPreferences.Editor editor = SaveSharedPreference.getSharedPreferences(ConfirmTransac.this).edit();
                editor.clear();
                editor.commit();
                ConfirmTransac.EditToken getCompany= new ConfirmTransac.EditToken();
                getCompany.execute(MainActivity.userid,"");
                if(MainActivity.notifnum!=0){
                    for(int i=1;i<=MainActivity.notifnum;i++){
                        AlarmManager [] alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                        Intent intents[] = new Intent[MainActivity.notifnum+1];

                        intents[MainActivity.notifnum] = new Intent(ConfirmTransac.this, AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(ConfirmTransac.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                        alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManagers[MainActivity.notifnum].cancel(pendingIntent);

                        intents[MainActivity.notifnum] = new Intent(ConfirmTransac.this, AlarmReceiver2.class);
                        pendingIntent = PendingIntent.getBroadcast(ConfirmTransac.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                        alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManagers[MainActivity.notifnum].cancel(pendingIntent);

                        intents[MainActivity.notifnum] = new Intent(ConfirmTransac.this, AlarmReceiver3.class);
                        pendingIntent = PendingIntent.getBroadcast(ConfirmTransac.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                        alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManagers[MainActivity.notifnum].cancel(pendingIntent);
                    }
                }
                Intent intent = new Intent(ConfirmTransac.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.profile:
                Intent intent1 = new Intent(ConfirmTransac.this, ProfileActivity.class);
                startActivity(intent1);
                return true;
            case R.id.settings:
                Intent intent2 = new Intent(this, SettingActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
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

            JSONArray json = jsonParser.makeHttpRequest(URL3, params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {

        }

    }



}
