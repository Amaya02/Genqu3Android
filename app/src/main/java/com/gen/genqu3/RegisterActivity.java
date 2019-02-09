package com.gen.genqu3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    EditText regPassword, regName, regEmail, fName, lName, conNum;
    Button btnLog, btnReg;

    //String URL= "http://192.168.254.2/Android_Login/index.php";

    String URL= "http://genqu3.000webhostapp.com/Android_Login/index.php";

    JSONParser jsonParser=new JSONParser();

    int i=0;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        regName=(EditText)findViewById(R.id.regName);
        regEmail=(EditText)findViewById(R.id.regEmail);
        regPassword=(EditText)findViewById(R.id.regPassword);
        fName=(EditText)findViewById(R.id.firstName);
        lName=(EditText)findViewById(R.id.lastName);
        conNum=(EditText)findViewById(R.id.conNum);

        btnLog=(Button)findViewById(R.id.btnLog);
        btnReg=(Button)findViewById(R.id.btnReg);

        progress = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progress.setTitle("Loading");
        progress.setMessage("Please wait..");
        progress.setCancelable(false);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckNetwork.isAvail(RegisterActivity.this)){
                    progress.show();
                    String enteredUser = regName.getText().toString();
                    String enteredEmail = regEmail.getText().toString();
                    String enteredPassword = regPassword.getText().toString();
                    String enteredfname = fName.getText().toString();
                    String enteredlname = lName.getText().toString();
                    String enterednum = conNum.getText().toString();


                    if(TextUtils.isEmpty(enteredEmail) || TextUtils.isEmpty(enteredPassword) || TextUtils.isEmpty(enteredUser)
                            || TextUtils.isEmpty(enteredfname) || TextUtils.isEmpty(enteredlname) || TextUtils.isEmpty(enterednum)){
                        Toast.makeText(getApplicationContext(), "Fields must be filled!", Toast.LENGTH_LONG).show();
                        progress.dismiss();
                        return;
                    }
                    else if(enteredUser.length()<6 || enteredPassword.length()<6){
                        Toast.makeText(getApplicationContext(), "Username and Password must be atleast 6 characters!", Toast.LENGTH_LONG).show();
                        progress.dismiss();
                        return;
                    }

                    RegisterActivity.AttemptLogin attemptLogin= new RegisterActivity.AttemptLogin();
                    attemptLogin.execute(regName.getText().toString(),regPassword.getText().toString(),regEmail.getText().toString(),
                            fName.getText().toString(),lName.getText().toString(),conNum.getText().toString());
                }
                else{
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(RegisterActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(RegisterActivity.this);
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

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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

            String num = args[5];
            String lname = args[4];
            String fname = args[3];
            String email = args[2];
            String password = args[1];
            String name = args[0];

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", name));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("fname", fname));
            params.add(new BasicNameValuePair("lname", lname));
            params.add(new BasicNameValuePair("num", num));

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
                        Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } else {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Unable to Retrieve Data from Server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
