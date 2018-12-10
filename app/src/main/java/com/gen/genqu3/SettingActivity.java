package com.gen.genqu3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

public class SettingActivity extends AppCompatActivity {

    EditText up_user, up_email, up_curpass, up_newpass;
    Button up_edit, up_editpass, up_cancel, up_save1, up_back, up_save2;
    LinearLayout up_lay, up_lay2;

    //String URL= "http://192.168.43.43/Android_Login/updateuser.php";
    String URL= "http://192.168.22.9/Android_Login/updateuser.php";

    JSONParser2 jsonParser=new JSONParser2();

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        progress = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progress.setTitle("Loading");
        progress.setMessage("Please wait..");
        progress.setCancelable(false);

        String username = SaveSharedPreference.getUserName(this);
        String id = SaveSharedPreference.getUserId(this);
        String password = SaveSharedPreference.getUserPass(this);
        String email = SaveSharedPreference.getUserEmail(this);

        up_user=(EditText)findViewById(R.id.up_user);
        up_email=(EditText)findViewById(R.id.up_email);
        up_curpass=(EditText)findViewById(R.id.up_curpass);
        up_newpass=(EditText)findViewById(R.id.up_newpass);

        up_user.setText(username);
        up_email.setText(email);

        up_edit=(Button)findViewById(R.id.up_edit);
        up_editpass=(Button)findViewById(R.id.up_editpass);
        up_cancel=(Button)findViewById(R.id.up_cancel);
        up_save1=(Button)findViewById(R.id.up_save1);
        up_save2=(Button)findViewById(R.id.up_save2);
        up_back=(Button)findViewById(R.id.up_back);

        up_lay=(LinearLayout)findViewById(R.id.up_lay);
        up_lay2=(LinearLayout)findViewById(R.id.up_lay2);

        up_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                up_user.setEnabled(true);
                up_email.setEnabled(true);

                up_back.setVisibility(View.GONE);
                up_cancel.setVisibility(View.VISIBLE);
                up_edit.setVisibility(View.GONE);
                up_editpass.setVisibility(View.GONE);
                up_save1.setVisibility(View.VISIBLE);
            }
        });

        up_editpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                up_lay2.setVisibility(View.GONE);
                up_lay.setVisibility(View.VISIBLE);

                up_back.setVisibility(View.GONE);
                up_cancel.setVisibility(View.VISIBLE);
                up_edit.setVisibility(View.GONE);
                up_save2.setVisibility(View.VISIBLE);
                up_editpass.setVisibility(View.GONE);
            }
        });

        up_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        up_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        up_save1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredUser = up_user.getText().toString();
                String enteredEmail = up_email.getText().toString();

                if(TextUtils.isEmpty(enteredUser) || TextUtils.isEmpty(enteredEmail)){
                    Toast.makeText(getApplicationContext(), "Fields must be filled!", Toast.LENGTH_LONG).show();
                    return;
                }

                progress.show();

                SettingActivity.EditUser getCompany= new SettingActivity.EditUser();
                getCompany.execute(MainActivity.userid,enteredUser,enteredEmail);
            }
        });

        up_save2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredCurPass = up_curpass.getText().toString();
                String enteredNewPass = up_newpass.getText().toString();

                if(TextUtils.isEmpty(enteredCurPass) || TextUtils.isEmpty(enteredNewPass)){
                    Toast.makeText(getApplicationContext(), "Fields must be filled!", Toast.LENGTH_LONG).show();
                    return;
                }

                progress.show();

                SettingActivity.EditUserPass getCompany= new SettingActivity.EditUserPass();
                getCompany.execute(MainActivity.userid,enteredCurPass,enteredNewPass);
            }
        });

    }

    private class EditUser extends AsyncTask<String, String, JSONArray> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected JSONArray doInBackground(String... args) {

            String userid = args[0];
            String username = args[1];
            String email = args[2];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userid", userid));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("email", email));

            JSONArray json = jsonParser.makeHttpRequest(URL,params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {
            try {
                if(jArray.getJSONObject(0).getString("result").equals("success")){
                    progress.dismiss();

                    String enteredUser = up_user.getText().toString();
                    String enteredEmail = up_email.getText().toString();

                    SaveSharedPreference.setUserName(SettingActivity.this,enteredUser);
                    SaveSharedPreference.setUserEmail(SettingActivity.this,enteredEmail);

                    Toast.makeText(getApplicationContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else{
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), jArray.getJSONObject(0).getString("result"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                progress.dismiss();
                e.printStackTrace();
            }

        }
    }

    private class EditUserPass extends AsyncTask<String, String, JSONArray> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected JSONArray doInBackground(String... args) {

            String userid = args[0];
            String curpass = args[1];
            String newpass = args[2];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userid", userid));
            params.add(new BasicNameValuePair("curpass", curpass));
            params.add(new BasicNameValuePair("newpass", newpass));

            JSONArray json = jsonParser.makeHttpRequest(URL,params);

            return json;

        }

        protected void onPostExecute(JSONArray jArray) {
            try {
                if(jArray.getJSONObject(0).getString("result").equals("success")){
                    progress.dismiss();
                    SaveSharedPreference.setUserPass(SettingActivity.this,jArray.getJSONObject(0).getString("pass"));

                    Toast.makeText(getApplicationContext(), "Password Updated Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else{
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), jArray.getJSONObject(0).getString("result"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                progress.dismiss();
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.main_menu3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.logout:
                SharedPreferences.Editor editor = SaveSharedPreference.getSharedPreferences(this).edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.profile:
                Intent intent1 = new Intent(this, ProfileActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
