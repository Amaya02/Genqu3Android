package com.gen.genqu3;


import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

public class MakeTransac extends AppCompatActivity {

    //String URL= "http://192.168.1.100/Android_Login/getcompany.php";
    String URL= "http://192.168.1.38/Android_Login/getcompany.php";

    TextView Example;

    JSONParser2 jsonParser=new JSONParser2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_transac);

        MakeTransac.GetCompany getCompany= new MakeTransac.GetCompany();
        getCompany.execute("");

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

            Button b[] = new Button[100];
            final String cn[] = new String[100];
            final String ci[] = new String[100];
            final String ce[] = new String[100];
            final String ca[] = new String[100];
            final String cc[] = new String[100];
            final String c[] = new String[100];

            try{
                if(!jArray.getJSONObject(0).getString("result").equals("empty")) {
                    TableLayout tv = (TableLayout) findViewById(R.id.table);
                    int flag = 0;
                    for (int i = 0; i < jArray.length(); i++) {
                        TableRow tr = new TableRow(MakeTransac.this);
                        TableLayout.LayoutParams lp = new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.MATCH_PARENT);
                        lp.setMargins(0, 0, 0, 15);
                        tr.setLayoutParams(lp);
                        JSONObject json_data = jArray.getJSONObject(i);
                        Log.i("log_tag", "Name: " + json_data.getString("companyname"));
                        b[i] = new Button(MakeTransac.this);
                        String stime = String.valueOf(json_data.getString("companyname"));
                        cn[i] = String.valueOf(json_data.getString("companyname"));
                        ci[i] = String.valueOf(json_data.getString("companyid"));
                        ce[i] = String.valueOf(json_data.getString("email"));
                        ca[i] = String.valueOf(json_data.getString("address"));
                        cc[i] = String.valueOf(json_data.getString("country"));
                        c[i] = String.valueOf(json_data.getString("cnumber"));
                        b[i].setText(stime);
                        b[i].setTextColor(Color.BLACK);
                        b[i].setTextSize(15);
                        b[i].setBackgroundColor(Color.parseColor("#a4f18912"));
                        tr.addView(b[i]);
                        tv.addView(tr);
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
                }
                else{
                    Toast.makeText(getApplicationContext(), "No Available Company", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Log.e("log_tag", "Error parsing data" + e.toString());
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
                SharedPreferences.Editor editor = SaveSharedPreference.getSharedPreferences(MakeTransac.this).edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(MakeTransac.this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.profile:
                Intent intent1 = new Intent(MakeTransac.this, ProfileActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
