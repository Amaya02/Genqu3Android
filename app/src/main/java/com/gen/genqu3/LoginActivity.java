package com.gen.genqu3;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class LoginActivity extends AppCompatActivity {

    EditText editPassword, editName;
    Button btnSignIn, btnRegister, btnForgot;

    String URL= "http://genqu3.000webhostapp.com/Android_Login/index.php";
    String URL2= "http://genqu3.000webhostapp.com/Android_Login/updatetoken.php";
    String URL3= "http://genqu3.000webhostapp.com/Android_Login/restartalarm.php";
    String URL4= "http://genqu3.000webhostapp.com/Android_Login/sendPass.php";

    JSONParser jsonParser=new JSONParser();
    JSONParser2 jsonParser2=new JSONParser2();

    int i=0;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        editName=(EditText)findViewById(R.id.editName);
        editPassword=(EditText)findViewById(R.id.editPassword);

        btnSignIn=(Button)findViewById(R.id.btnSignIn);
        btnRegister=(Button)findViewById(R.id.btnRegister);
        btnForgot=(Button)findViewById(R.id.btnForgot);

        progress = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progress.setTitle("Loading");
        progress.setMessage("Please wait..");
        progress.setCancelable(false);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckNetwork.isAvail(LoginActivity.this)){
                    progress.show();
                    String enteredUser = editName.getText().toString();
                    String enteredPassword = editPassword.getText().toString();

                    if(TextUtils.isEmpty(enteredUser) || TextUtils.isEmpty(enteredPassword)){
                        Toast.makeText(getApplicationContext(), "Fields must be filled!", Toast.LENGTH_LONG).show();
                        progress.dismiss();
                        return;
                    }

                    LoginActivity.AttemptLogin attemptLogin= new LoginActivity.AttemptLogin();
                    attemptLogin.execute(editName.getText().toString(),editPassword.getText().toString(),"");
                }
                else {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(LoginActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(LoginActivity.this);
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

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Forgot Password");
                builder.setMessage("Please Enter your Email Address");
                LinearLayout container = new LinearLayout(LoginActivity.this);
                container.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(35, 0, 35, 0);
                final EditText input = new EditText(LoginActivity.this);
                input.setLayoutParams(lp);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setTextColor(Color.WHITE);
                container.addView(input);

                builder.setView(container);
                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        if(CheckNetwork.isAvail(LoginActivity.this)){
                            progress.show();
                            if(TextUtils.isEmpty(m_Text)){
                                Toast.makeText(getApplicationContext(), "Fields must be filled!", Toast.LENGTH_LONG).show();
                                progress.dismiss();
                                return;
                            }
                            LoginActivity.ForgotPass forgot= new LoginActivity.ForgotPass();
                            forgot.execute(m_Text);
                        }
                        else {
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(LoginActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(LoginActivity.this);
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
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

    }

    private class AttemptLogin extends AsyncTask<String, String, JSONObject> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected JSONObject doInBackground(String... args) {


            String password = args[1];
            String name = args[0];

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", name));
            params.add(new BasicNameValuePair("password", password));

            JSONObject json = jsonParser.makeHttpRequest(URL, "POST", params);


            return json;

        }

        protected void onPostExecute(JSONObject result) {

            // dismiss the dialog once product deleted
            //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();

            try {
                if (result != null) {
                    if (result.getString("success").equals("1")) {
                        progress.dismiss();

                        SaveSharedPreference.setUserName(LoginActivity.this,result.getString("username"));
                        SaveSharedPreference.setUserEmail(LoginActivity.this,result.getString("email"));
                        SaveSharedPreference.setUserPass(LoginActivity.this,result.getString("password"));
                        SaveSharedPreference.setUserId(LoginActivity.this,result.getString("id"));
                        SaveSharedPreference.setfName(LoginActivity.this,result.getString("fname"));
                        SaveSharedPreference.setlName(LoginActivity.this,result.getString("lname"));
                        SaveSharedPreference.setNum(LoginActivity.this,result.getString("num"));

                        MainActivity.userid = result.getString("id");

                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( LoginActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                String token = instanceIdResult.getToken();
                                Log.d("FCM_TOKEN",token);
                                storeRegIdInPref(token);
                                LoginActivity.EditToken getCompany= new LoginActivity.EditToken();
                                getCompany.execute(MainActivity.userid,token);
                            }
                        });

                        MainActivity.notifnum =0;
                        MainActivity.notifnum2 =0;
                        String id = SaveSharedPreference.getUserId(LoginActivity.this);
                        LoginActivity.RestartAlarm getCompany= new LoginActivity.RestartAlarm();
                        getCompany.execute(id);

                        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } else {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Unable to retrieve data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


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

            JSONArray json = jsonParser2.makeHttpRequest(URL2, params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {

        }

    }

    private class ForgotPass extends AsyncTask<String, String, JSONArray> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected JSONArray doInBackground(String... args) {

            String email= args[0];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));

            JSONArray json = jsonParser2.makeHttpRequest(URL4, params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {
            try {
                if(!jArray.getJSONObject(0).getString("result").equals("empty")){
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Recovery Password Sent! Please check your email..", Toast.LENGTH_SHORT).show();
                }
                else{
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Email not Registered!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
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

            JSONArray json = jsonParser2.makeHttpRequest(URL3, params);

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

                                AlarmManager[] alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                Intent intents[] = new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 30,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver5.class);
                                pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 10,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver4.class);
                                pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 2,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver2.class);
                                pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min ,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver3.class);
                                pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                            }
                            else if(difference>1800){
                                MainActivity.notifnum++;

                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 30,0);

                                AlarmManager[] alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                Intent intents[]= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver5.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 10,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver4.class);
                                pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 2,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver2.class);
                                pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min ,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver3.class);
                                pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                            }
                            else if(difference>600){
                                MainActivity.notifnum++;

                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 10,0);

                                AlarmManager[] alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                Intent intents[]= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver4.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min - 2,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver2.class);
                                pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min ,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver3.class);
                                pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
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

                                intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver2.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                                alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                                MainActivity.notifnum++;

                                c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.clear();
                                c.set(year, (month-1), day, hour, min ,0);

                                alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                                intents= new Intent[MainActivity.notifnum+1];

                                intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver3.class);
                                pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
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

                                    intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver3.class);
                                    intents[MainActivity.notifnum].putExtra("ID",4);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
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

                            intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver.class);
                            intents[MainActivity.notifnum].putExtra("ID",0);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                            alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                            MainActivity.notifnum++;

                            c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            c.clear();
                            c.set(year, (month-1), day, hour, min - 30,0);

                            alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                            intents= new Intent[MainActivity.notifnum+1];

                            intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver5.class);
                            intents[MainActivity.notifnum].putExtra("ID",3);
                            pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                            alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                            MainActivity.notifnum++;

                            c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            c.clear();
                            c.set(year, (month-1), day, hour, min - 10,0);

                            alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                            intents= new Intent[MainActivity.notifnum+1];

                            intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver4.class);
                            intents[MainActivity.notifnum].putExtra("ID",3);
                            pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                            alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                            MainActivity.notifnum++;

                            c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            c.clear();
                            c.set(year, (month-1), day, hour, min - 2,0);

                            alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                            intents= new Intent[MainActivity.notifnum+1];

                            intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver2.class);
                            intents[MainActivity.notifnum].putExtra("ID",3);
                            pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
                            alarmManagers[MainActivity.notifnum] = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            alarmManagers[MainActivity.notifnum].set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                            MainActivity.notifnum++;

                            c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            c.clear();
                            c.set(year, (month-1), day, hour, min ,0);

                            alarmManagers = new AlarmManager[MainActivity.notifnum+1];
                            intents= new Intent[MainActivity.notifnum+1];

                            intents[MainActivity.notifnum] = new Intent(LoginActivity.this, AlarmReceiver3.class);
                            intents[MainActivity.notifnum].putExtra("ID",4);
                            pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, MainActivity.notifnum, intents[MainActivity.notifnum], 0);
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
