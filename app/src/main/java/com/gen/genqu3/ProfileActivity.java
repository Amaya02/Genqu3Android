package com.gen.genqu3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import com.gen.genqu3.R;
import com.gen.genqu3.Config;
import com.gen.genqu3.NotificationUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    TextView user_Name, user_Name2, user_Email, user_Num;
    ImageButton makeTran, viewTran;
    private static final String TAG = ProfileActivity.class.getSimpleName();

    String URL2= "http://genqu3.000webhostapp.com/Android_Login/updatetoken.php";
    JSONParser2 jsonParser=new JSONParser2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();

        String username = SaveSharedPreference.getUserName(ProfileActivity.this);
        String id = SaveSharedPreference.getUserId(ProfileActivity.this);
        String password = SaveSharedPreference.getUserPass(ProfileActivity.this);
        String email = SaveSharedPreference.getUserEmail(ProfileActivity.this);
        String fname = SaveSharedPreference.getUserfName(ProfileActivity.this);
        String lname = SaveSharedPreference.getUserlName(ProfileActivity.this);
        String num = SaveSharedPreference.getUserNum(ProfileActivity.this);

        MainActivity.userid = id;

        user_Name=(TextView)findViewById(R.id.user_Name);
        user_Name2=(TextView)findViewById(R.id.user_Name2);
        user_Email=(TextView)findViewById(R.id.user_Email);
        user_Num=(TextView)findViewById(R.id.user_Num);

        user_Name.setText(username);
        user_Name2.setText(fname+" "+lname);
        user_Email.setText(email);
        user_Num.setText(num);

        makeTran=(ImageButton)findViewById(R.id.makeTran);
        viewTran=(ImageButton)findViewById(R.id.viewTran);

        makeTran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MakeTransac.class);
                startActivity(intent);
            }
        });

        viewTran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ViewTransac.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.logout:
                SharedPreferences.Editor editor = SaveSharedPreference.getSharedPreferences(ProfileActivity.this).edit();
                editor.clear();
                editor.commit();
                ProfileActivity.EditToken getCompany= new ProfileActivity.EditToken();
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
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.settings:
                Intent intent1 = new Intent(this, SettingActivity.class);
                startActivity(intent1);
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
