package com.gen.genqu3;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
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

public class ConfirmTransac extends AppCompatActivity {

    Button t_back, t_confirm;
    TextView t_name,t_time,t_esti,t_arriv,t_date;

    //String URL= "http://192.168.1.100/Android_Login/confirmtransaction.php";
    //String URL2= "http://192.168.1.100/Android_Login/addtransaction.php";

    String URL= "http://192.168.1.38/Android_Login/confirmtransaction.php";
    String URL2= "http://192.168.1.38/Android_Login/addtransaction.php";

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
                    Toast.makeText(getApplicationContext(), "Transaction Added Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ConfirmTransac.this, ProfileActivity.class);
                    startActivity(intent);
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

}