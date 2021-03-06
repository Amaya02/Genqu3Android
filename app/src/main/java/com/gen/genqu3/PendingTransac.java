package com.gen.genqu3;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PendingTransac extends AppCompatActivity {

    //String URL= "http://192.168.254.2/Android_Login/getusertransaction.php";

    String URL= "http://genqu3.000webhostapp.com/Android_Login/getusertransaction.php";
    String URL2= "http://genqu3.000webhostapp.com/Android_Login/updatetoken.php";

    JSONParser2 jsonParser=new JSONParser2();

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_transac);

        progress = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progress.setTitle("Loading");
        progress.setMessage("Please wait..");
        progress.setCancelable(false);

        if(CheckNetwork.isAvail(PendingTransac.this)){
            progress.show();
            PendingTransac.GetTransac getCompany= new PendingTransac.GetTransac();
            getCompany.execute(MainActivity.userid,"pending");
        }
        else{
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(PendingTransac.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(PendingTransac.this);
            }
            builder.setTitle("Genqu3")
                    .setCancelable(false)
                    .setMessage("No Internet Connection!")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent= new Intent(PendingTransac.this, PendingTransac.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setIcon(R.drawable.icon3)
                    .show();
        }
    }

    private class GetTransac extends AsyncTask<String, String, JSONArray> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected JSONArray doInBackground(String... args) {

            String userid = args[0];
            String status = args[1];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userid", userid));
            params.add(new BasicNameValuePair("status", status));

            JSONArray json = jsonParser.makeHttpRequest(URL,params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {

            LinearLayout b[] = new LinearLayout[100];
            final String n[] = new String[100];
            final String n1[] = new String[100];
            final String c[] = new String[100];
            final String d[] = new String[100];
            final String d2[] = new String[100];
            final String s[] = new String[100];
            final String id[] = new String[100];
            final String time[] = new String[100];

            try{
                if(!jArray.getJSONObject(0).getString("result").equals("empty")) {
                    LinearLayout tv = (LinearLayout) findViewById(R.id.penlayout);

                    for (int i = 0; i < jArray.length(); i++) {
                        b[i] = new LinearLayout(PendingTransac.this);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, 0, 0, 15);
                        b[i].setLayoutParams(lp);
                        b[i].setPadding(10,10,10,10);
                        b[i].setBackgroundColor(Color.parseColor("#a4f18912"));
                        b[i].setOrientation(LinearLayout.VERTICAL);
                        tv.addView(b[i]);
                        JSONObject json_data = jArray.getJSONObject(i);

                        TextView tn = new TextView(PendingTransac.this);
                        tn.setText(json_data.getString("companyname"));
                        tn.setTextSize(20);
                        tn.setTextColor(Color.BLACK);

                        TextView tn1 = new TextView(PendingTransac.this);
                        tn1.setText("Window "+json_data.getString("windowid")+" - "+json_data.getString("transacname"));
                        tn1.setTextSize(20);
                        tn1.setTextColor(Color.BLACK);

                        TextView td = new TextView(PendingTransac.this);
                        td.setText("Generated: "+String.valueOf(json_data.getString("date_tran")));
                        td.setTextSize(15);
                        td.setTextColor(Color.WHITE);

                        TextView tst = new TextView(PendingTransac.this);
                        tst.setText("Arrive At: "+String.valueOf(json_data.getString("esti_date"))+" - "+String.valueOf(json_data.getString("esti_start")));
                        tst.setTextSize(15);
                        tst.setTextColor(Color.RED);
                        tst.setAllCaps(true);

                        TextView ts = new TextView(PendingTransac.this);
                        ts.setText("Status: "+String.valueOf(json_data.getString("status")));
                        ts.setTextSize(15);
                        ts.setTextColor(Color.WHITE);
                        ts.setAllCaps(true);

                        TextView ts1 = new TextView(PendingTransac.this);
                        ts1.setText("Click to view QR code");
                        ts1.setTextSize(10);
                        ts1.setTextColor(Color.BLACK);
                        ts1.setAllCaps(true);
                        ts1.setAllCaps(true);
                        ts1.setGravity(Gravity.RIGHT);

                        b[i].addView(tn);
                        b[i].addView(tn1);
                        b[i].addView(td);
                        b[i].addView(ts);
                        b[i].addView(tst);
                        b[i].addView(ts1);

                        n[i]=json_data.getString("transacname");
                        n1[i]=json_data.getString("windowid");
                        id[i]=json_data.getString("u_tranid");
                        c[i]=json_data.getString("companyname");
                        d[i]=String.valueOf(json_data.getString("date_tran"));
                        d2[i]=String.valueOf(json_data.getString("esti_date"));
                        s[i]=String.valueOf(json_data.getString("status"));
                        time[i]=String.valueOf(json_data.getString("esti_start"));

                        final int count = i;
                        b[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(PendingTransac.this, QRTransac.class);
                                intent.putExtra("TRANSACNAME", n[count]);
                                intent.putExtra("TRANSACID", n1[count]);
                                intent.putExtra("COMPANYNAME", c[count]);
                                intent.putExtra("DATE", d[count]);
                                intent.putExtra("DATE2", d2[count]);
                                intent.putExtra("STATUS", s[count]);
                                intent.putExtra("ID", id[count]);
                                intent.putExtra("TIME", time[count]);
                                intent.putExtra("HIDE", "PENDING");
                                startActivity(intent);
                            }
                        });
                    }
                    progress.dismiss();
                }
                else{
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "No Available Transaction", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                progress.dismiss();
                Log.e("log_tag", "Error parsing data" + e.toString()+jArray);
                Toast.makeText(getApplicationContext(), "Unable to Connect to Server", Toast.LENGTH_SHORT).show();
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
                SharedPreferences.Editor editor = SaveSharedPreference.getSharedPreferences(PendingTransac.this).edit();
                editor.clear();
                editor.commit();
                PendingTransac.EditToken getCompany= new PendingTransac.EditToken();
                getCompany.execute(MainActivity.userid,"");
                if(MainActivity.notifnum!=0){
                    for(int i=1;i<=MainActivity.notifnum;i++){
                        AlarmManager[] alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                        Intent intents[] = new Intent[MainActivity.notifnum+1];

                        intents[MainActivity.notifnum] = new Intent(this, AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                        alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManagers[MainActivity.notifnum].cancel(pendingIntent);

                        intents[MainActivity.notifnum] = new Intent(this, AlarmReceiver2.class);
                        pendingIntent = PendingIntent.getBroadcast(this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                        alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManagers[MainActivity.notifnum].cancel(pendingIntent);

                        intents[MainActivity.notifnum] = new Intent(this, AlarmReceiver3.class);
                        pendingIntent = PendingIntent.getBroadcast(this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                        alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManagers[MainActivity.notifnum].cancel(pendingIntent);

                        intents[MainActivity.notifnum] = new Intent(this, AlarmReceiver4.class);
                        pendingIntent = PendingIntent.getBroadcast(this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                        alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManagers[MainActivity.notifnum].cancel(pendingIntent);

                        intents[MainActivity.notifnum] = new Intent(this, AlarmReceiver5.class);
                        pendingIntent = PendingIntent.getBroadcast(this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                        alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManagers[MainActivity.notifnum].cancel(pendingIntent);
                    }
                }
                Intent intent = new Intent(PendingTransac.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.profile:
                Intent intent1 = new Intent(PendingTransac.this, ProfileActivity.class);
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

            JSONArray json = jsonParser.makeHttpRequest(URL2, params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {

        }

    }
}
