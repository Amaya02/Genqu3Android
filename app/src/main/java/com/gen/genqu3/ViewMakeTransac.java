package com.gen.genqu3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class ViewMakeTransac extends AppCompatActivity {

    //String URL= "http://192.168.254.2/Android_Login/getcompany.php";

    String URL= "http://192.168.22.7/Android_Login/getcompany.php";
    String URL2= "http://192.168.22.7/Android_Login/updatetoken.php";

    TextView com_name, com_email, com_num, com_address, com_country;

    JSONParser2 jsonParser=new JSONParser2();

    ProgressDialog progress;

    String cn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_make_transac);

        Intent intent = getIntent();

        cn = intent.getStringExtra("COMPANYNAME");
        String ci = intent.getStringExtra("COMPANYID");
        String cc = intent.getStringExtra("COMPANYCOUNTRY");
        String ca = intent.getStringExtra("COMPANYADDRESS");
        String c = intent.getStringExtra("COMPANYNUM");
        String ce = intent.getStringExtra("COMPANYEMAIL");

        com_name=(TextView)findViewById(R.id.com_name);
        com_email=(TextView)findViewById(R.id.com_email);
        com_num=(TextView)findViewById(R.id.com_num);
        com_address=(TextView)findViewById(R.id.com_address);
        com_country=(TextView)findViewById(R.id.com_country);

        com_name.setText(cn);
        com_email.setText(ce);
        com_num.setText(c);
        com_address.setText(ca);
        com_country.setText(" - "+cc);

        progress = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progress.setTitle("Loading");
        progress.setMessage("Please wait..");
        progress.setCancelable(false);

        progress.show();

        ViewMakeTransac.GetCompanyTran getCompany= new ViewMakeTransac.GetCompanyTran();
        getCompany.execute(ci);

    }

    private class GetCompanyTran extends AsyncTask<String, String, JSONArray> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected JSONArray doInBackground(String... args) {

            String companyid = args[0];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("companyid", companyid));

            JSONArray json = jsonParser.makeHttpRequest(URL,params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {

            LinearLayout b[] = new LinearLayout[100];
            final String tn[] = new String[100];
            final String ti[] = new String[100];
            final String tc[] = new String[100];
            final String st[] = new String[100];
            final String et[] = new String[100];
            final String eet[] = new String[100];

            try{
                if(!jArray.getJSONObject(0).getString("result").equals("empty")) {
                    LinearLayout tv = (LinearLayout) findViewById(R.id.makeviewlayout);

                    for (int i = 0; i < jArray.length(); i++) {
                        b[i] = new LinearLayout(ViewMakeTransac.this);
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

                        TextView tn1 = new TextView(ViewMakeTransac.this);
                        tn1.setText("Window "+json_data.getString("transacid")+" - "+json_data.getString("transacname"));
                        tn1.setTextSize(20);
                        tn1.setTextColor(Color.BLACK);
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp2.setMargins(10, 10, 10, 10);
                        tn1.setLayoutParams(lp2);

                        b[i].addView(tn1);

                        ti[i]= String.valueOf(json_data.getString("transacid"));
                        tn[i]= String.valueOf(json_data.getString("transacname"));
                        st[i]= String.valueOf(json_data.getString("starttime"));
                        et[i]= String.valueOf(json_data.getString("endtime"));
                        eet[i]= String.valueOf(json_data.getString("estimatedtime"));
                        tc[i]=String.valueOf(json_data.getString("companyid"));

                        final int count = i;
                        b[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ViewMakeTransac.this, ConfirmTransac.class);
                                intent.putExtra("TRANSACID",ti[count]);
                                intent.putExtra("TRANSACNAME",tn[count]);
                                intent.putExtra("STARTTIME",st[count]);
                                intent.putExtra("ENDTIME",et[count]);
                                intent.putExtra("ESTIMATEDTIME",eet[count]);
                                intent.putExtra("COMPANYNAME",cn);
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
                Toast.makeText(getApplicationContext(), "JsonArray fail", Toast.LENGTH_SHORT).show();
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
                SharedPreferences.Editor editor = SaveSharedPreference.getSharedPreferences(ViewMakeTransac.this).edit();
                editor.clear();
                editor.commit();
                ViewMakeTransac.EditToken getCompany= new ViewMakeTransac.EditToken();
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
                    }
                }
                Intent intent = new Intent(ViewMakeTransac.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.profile:
                Intent intent1 = new Intent(ViewMakeTransac.this, ProfileActivity.class);
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

