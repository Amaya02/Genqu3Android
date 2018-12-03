package com.gen.genqu3;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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

    String URL= "http://192.168.22.5/Android_Login/getusertransaction.php";

    JSONParser2 jsonParser=new JSONParser2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_transac);

        PendingTransac.GetTransac getCompany= new PendingTransac.GetTransac();
        getCompany.execute(MainActivity.userid,"pending");
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
            final String c[] = new String[100];
            final String d[] = new String[100];
            final String s[] = new String[100];

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
                        tn.setText(json_data.getString("companyname")+" - "+json_data.getString("transacname"));
                        tn.setTextSize(20);
                        tn.setTextColor(Color.BLACK);

                        TextView td = new TextView(PendingTransac.this);
                        td.setText("Generated: "+String.valueOf(json_data.getString("date_tran")));
                        td.setTextSize(15);
                        td.setTextColor(Color.WHITE);

                        TextView ts = new TextView(PendingTransac.this);
                        ts.setText("Status: "+String.valueOf(json_data.getString("status")));
                        ts.setTextSize(15);
                        ts.setTextColor(Color.WHITE);
                        ts.setAllCaps(true);

                        b[i].addView(tn);
                        b[i].addView(td);
                        b[i].addView(ts);

                        n[i]=json_data.getString("transacname");
                        c[i]=json_data.getString("companyname");
                        d[i]=String.valueOf(json_data.getString("date_tran"));
                        s[i]=String.valueOf(json_data.getString("status"));

                        final int count = i;
                        b[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(PendingTransac.this, QRTransac.class);
                                intent.putExtra("TRANSACNAME", n[count]);
                                intent.putExtra("COMPANYNAME", c[count]);
                                intent.putExtra("DATE", d[count]);
                                intent.putExtra("STATUS", s[count]);
                                startActivity(intent);
                            }
                        });
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "No Available Transaction", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
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
}
