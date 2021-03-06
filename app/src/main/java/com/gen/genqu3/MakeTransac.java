package com.gen.genqu3;


import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

public class MakeTransac extends AppCompatActivity {

    String URL= "http://genqu3.000webhostapp.com/Android_Login/getcompany.php";
    String URL2= "http://genqu3.000webhostapp.com/Android_Login/searchcompany.php";
    String URL3= "http://genqu3.000webhostapp.com/Android_Login/updatetoken.php";

    Button search_but, search_cancel;
    EditText search;

    JSONParser2 jsonParser=new JSONParser2();

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_transac);

        progress = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progress.setTitle("Loading");
        progress.setMessage("Please wait..");
        progress.setCancelable(false);

        search=(EditText)findViewById(R.id.search);
        search_but=(Button)findViewById(R.id.search_but);
        search_cancel=(Button)findViewById(R.id.search_cancel);

        search_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckNetwork.isAvail(MakeTransac.this)){
                    progress.show();
                    String enteredSearch = search.getText().toString();

                    if(TextUtils.isEmpty(enteredSearch)){
                        Toast.makeText(getApplicationContext(), "Fields must be filled!", Toast.LENGTH_LONG).show();
                        progress.dismiss();
                        return;
                    }
                    search_cancel.setVisibility(View.VISIBLE);
                    MakeTransac.SearchComp searchComp= new MakeTransac.SearchComp();
                    searchComp.execute(search.getText().toString());
                }
                else{
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(MakeTransac.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(MakeTransac.this);
                    }
                    builder.setTitle("Genqu3")
                            .setCancelable(false)
                            .setMessage("No Internet Connection!")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(R.drawable.icon3)
                            .show();
                }

            }
        });

        search_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MakeTransac.this, MakeTransac.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        if(CheckNetwork.isAvail(MakeTransac.this)){
            progress.show();
            MakeTransac.GetCompany getCompany= new MakeTransac.GetCompany();
            getCompany.execute("");
        }
        else{
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(MakeTransac.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(MakeTransac.this);
            }
            builder.setTitle("Genqu3")
                    .setCancelable(false)
                    .setMessage("No Internet Connection!")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent= new Intent(MakeTransac.this, MakeTransac.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setIcon(R.drawable.icon3)
                    .show();
        }

    }


    private class GetCompany extends AsyncTask<String, String, JSONArray> {

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
            final String cn[] = new String[100];
            final String ci[] = new String[100];
            final String ce[] = new String[100];
            final String ca[] = new String[100];
            final String cc[] = new String[100];
            final String c[] = new String[100];

            try{
                if(!jArray.getJSONObject(0).getString("result").equals("empty")) {
                    LinearLayout tv = (LinearLayout) findViewById(R.id.makelayout);
                    tv.removeAllViews();

                    for (int i = 0; i < jArray.length(); i++) {
                        b[i] = new LinearLayout(MakeTransac.this);
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

                        TextView tn = new TextView(MakeTransac.this);
                        tn.setText(json_data.getString("companyname"));
                        tn.setTextSize(23);
                        tn.setTextColor(Color.BLACK);
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp2.setMargins(10, 10, 10, 10);
                        tn.setLayoutParams(lp2);

                        b[i].addView(tn);

                        cn[i] = String.valueOf(json_data.getString("companyname"));
                        ci[i] = String.valueOf(json_data.getString("companyid"));
                        ce[i] = String.valueOf(json_data.getString("email"));
                        ca[i] = String.valueOf(json_data.getString("address"));
                        cc[i] = String.valueOf(json_data.getString("country"));
                        c[i] = String.valueOf(json_data.getString("cnumber"));

                        final int count = i;
                        b[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MakeTransac.this, ViewMakeTransac.class);
                                intent.putExtra("COMPANYNAME", cn[count]);
                                intent.putExtra("COMPANYEMAIL", ce[count]);
                                intent.putExtra("COMPANYADDRESS", ca[count]);
                                intent.putExtra("COMPANYCOUNTRY", cc[count]);
                                intent.putExtra("COMPANYID", ci[count]);
                                intent.putExtra("COMPANYNUM", c[count]);
                                startActivity(intent);
                            }
                        });
                    }
                    progress.dismiss();
                }
                else{
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "No Available Company", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                progress.dismiss();
                Log.e("log_tag", "Error parsing data" + e.toString());
                Toast.makeText(getApplicationContext(), "Unable to Connect to Server", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private class SearchComp extends AsyncTask<String, String, JSONArray> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected JSONArray doInBackground(String... args) {

            String search = args[0];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("search", search));

            JSONArray json = jsonParser.makeHttpRequest(URL2,params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {

            LinearLayout b[] = new LinearLayout[100];
            final String cn[] = new String[100];
            final String ci[] = new String[100];
            final String ce[] = new String[100];
            final String ca[] = new String[100];
            final String cc[] = new String[100];
            final String c[] = new String[100];

            try{
                if(!jArray.getJSONObject(0).getString("result").equals("empty")) {
                    LinearLayout tv = (LinearLayout) findViewById(R.id.makelayout);
                    tv.removeAllViews();

                    for (int i = 0; i < jArray.length(); i++) {
                        b[i] = new LinearLayout(MakeTransac.this);
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

                        TextView tn = new TextView(MakeTransac.this);
                        tn.setText(json_data.getString("companyname"));
                        tn.setTextSize(23);
                        tn.setTextColor(Color.BLACK);
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp2.setMargins(10, 10, 10, 10);
                        tn.setLayoutParams(lp2);

                        b[i].addView(tn);

                        cn[i] = String.valueOf(json_data.getString("companyname"));
                        ci[i] = String.valueOf(json_data.getString("companyid"));
                        ce[i] = String.valueOf(json_data.getString("email"));
                        ca[i] = String.valueOf(json_data.getString("address"));
                        cc[i] = String.valueOf(json_data.getString("country"));
                        c[i] = String.valueOf(json_data.getString("cnumber"));

                        final int count = i;
                        b[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MakeTransac.this, ViewMakeTransac.class);
                                intent.putExtra("COMPANYNAME", cn[count]);
                                intent.putExtra("COMPANYEMAIL", ce[count]);
                                intent.putExtra("COMPANYADDRESS", ca[count]);
                                intent.putExtra("COMPANYCOUNTRY", cc[count]);
                                intent.putExtra("COMPANYID", ci[count]);
                                intent.putExtra("COMPANYNUM", c[count]);
                                startActivity(intent);
                            }
                        });
                    }
                    progress.dismiss();
                }
                else{
                    LinearLayout tv = (LinearLayout) findViewById(R.id.makelayout);
                    tv.removeAllViews();
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Company Not Found!", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                progress.dismiss();
                Log.e("log_tag", "Error parsing data" + e.toString());
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
                SharedPreferences.Editor editor = SaveSharedPreference.getSharedPreferences(MakeTransac.this).edit();
                editor.clear();
                editor.commit();
                MakeTransac.EditToken getCompany= new MakeTransac.EditToken();
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
                Intent intent = new Intent(MakeTransac.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.profile:
                Intent intent1 = new Intent(MakeTransac.this, ProfileActivity.class);
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
