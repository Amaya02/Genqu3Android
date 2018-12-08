package com.gen.genqu3;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

public class ConfirmTransac extends AppCompatActivity {

    Button t_back, t_confirm;
    TextView t_name,t_time,t_esti,t_arriv,t_date;

    String URL= "http://192.168.43.43/Android_Login/confirmtransaction.php";
    String URL2= "http://192.168.43.43/Android_Login/addtransaction.php";

    JSONParser2 jsonParser=new JSONParser2();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_transac);

        Intent intent = getIntent();

        String transacname = intent.getStringExtra("TRANSACNAME");
        String starttime = intent.getStringExtra("STARTTIME");
        String endtime = intent.getStringExtra("ENDTIME");
        String estimatedtime = intent.getStringExtra("ESTIMATEDTIME");

        t_name = (TextView) findViewById(R.id.t_name);

        t_time = (TextView) findViewById(R.id.t_time);
        t_esti = (TextView) findViewById(R.id.t_esti);

        t_back = (Button) findViewById(R.id.t_back);
        t_confirm = (Button) findViewById(R.id.t_confirm);

        t_name.setText(transacname);
        t_time.setText("TIME: "+starttime+" - "+endtime);
        t_esti.setText("ESTIMATED TIME: "+estimatedtime+" Minutes");

        t_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ConfirmTransac.GetTran getCompany= new ConfirmTransac.GetTran();
        getCompany.execute(starttime,endtime,estimatedtime);
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
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("starttime", starttime));
            params.add(new BasicNameValuePair("endtime", endtime));
            params.add(new BasicNameValuePair("estitime", estitime));

            JSONArray json = jsonParser.makeHttpRequest(URL,params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {
            try {
                Intent intent = getIntent();
                final String transacid = intent.getStringExtra("TRANSACID");
                t_date = (TextView) findViewById(R.id.t_date);
                t_arriv = (TextView) findViewById(R.id.t_arriv);

                final String date = jArray.getJSONObject(0).getString("esti_date");
                final String arriv = jArray.getJSONObject(0).getString("estistart");
                final String end = jArray.getJSONObject(0).getString("estiend");
                t_date.setText("DATE: "+date);
                t_arriv.setText("ARRIVE AT: "+arriv);

                MainActivity.date_notif = date;
                MainActivity.time_notif = arriv;

                t_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConfirmTransac.AddUserTran getCompany= new ConfirmTransac.AddUserTran();
                        getCompany.execute(MainActivity.userid,transacid,"Pending",date,arriv,end);
                    }
                });

            } catch (JSONException e) {
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
            String end = args[5];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userid", userid));
            params.add(new BasicNameValuePair("transacid", transacid));
            params.add(new BasicNameValuePair("status", status));
            params.add(new BasicNameValuePair("date", date));
            params.add(new BasicNameValuePair("start", arriv));
            params.add(new BasicNameValuePair("end", end));

            JSONArray json = jsonParser.makeHttpRequest(URL2,params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {
            try {
                if(!jArray.getJSONObject(0).getString("result").equals("error")){

                    int day = Integer.parseInt(MainActivity.date_notif.substring(8,10));
                    int month = Integer.parseInt(MainActivity.date_notif.substring(5,7));
                    int year =Integer.parseInt(MainActivity.date_notif.substring(0,4));
                    int hour = Integer.parseInt(MainActivity.time_notif.substring(0,2));
                    int min = Integer.parseInt(MainActivity.time_notif.substring(3,5));

                    MainActivity.notifnum++;

                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(System.currentTimeMillis());
                    c.clear();
                    c.set(year, (month-1), day, hour, min);

                    AlarmManager [] alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                    Intent intents[] = new Intent[MainActivity.notifnum+1];

                    intents[MainActivity.notifnum] = new Intent(ConfirmTransac.this, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(ConfirmTransac.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                    alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                    Toast.makeText(getApplicationContext(), "Transaction Added Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(ConfirmTransac.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Transaction Failed", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
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
                Intent intent = new Intent(ConfirmTransac.this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.profile:
                Intent intent1 = new Intent(ConfirmTransac.this, ProfileActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }



}
