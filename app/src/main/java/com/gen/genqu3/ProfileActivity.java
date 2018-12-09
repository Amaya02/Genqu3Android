package com.gen.genqu3;

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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    TextView user_Name, user_Email;
    Button makeTran, viewTran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();

        String username = SaveSharedPreference.getUserName(ProfileActivity.this);
        String id = SaveSharedPreference.getUserId(ProfileActivity.this);
        String password = SaveSharedPreference.getUserPass(ProfileActivity.this);
        String email = SaveSharedPreference.getUserEmail(ProfileActivity.this);

        MainActivity.userid = id;

        user_Name=(TextView)findViewById(R.id.user_Name);
        user_Email=(TextView)findViewById(R.id.user_Email);

        user_Name.setText(username);
        user_Email.setText(email);

        makeTran=(Button)findViewById(R.id.makeTran);
        viewTran=(Button)findViewById(R.id.viewTran);

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
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
