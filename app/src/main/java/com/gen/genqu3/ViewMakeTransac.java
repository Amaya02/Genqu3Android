package com.gen.genqu3;

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

    //String URL= "http://192.168.1.100/Android_Login/getcompany.php";

    String URL= "http://192.168.1.38/Android_Login/getcompany.php";

    TextView com_name, com_email, com_num, com_address, com_country;

    JSONParser2 jsonParser=new JSONParser2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_make_transac);

        Intent intent = getIntent();

        String cn = intent.getStringExtra("COMPANYNAME");
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

            Button b[] = new Button[100];
            final String tn[] = new String[100];
            final String ti[] = new String[100];
            final String tc[] = new String[100];
            final String st[] = new String[100];
            final String et[] = new String[100];
            final String eet[] = new String[100];

            try{
                if(!jArray.getJSONObject(0).getString("result").equals("empty")) {
                    TableLayout tv = (TableLayout) findViewById(R.id.table2);
                    int flag = 0;
                    for (int i = 0; i < jArray.length(); i++) {
                        TableRow tr = new TableRow(ViewMakeTransac.this);
                        TableLayout.LayoutParams lp = new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.MATCH_PARENT);
                        lp.setMargins(0, 0, 0, 15);
                        tr.setLayoutParams(lp);
                        JSONObject json_data = jArray.getJSONObject(i);
                        Log.i("log_tag", "Name: " + json_data.getString("transacname"));
                        b[i] = new Button(ViewMakeTransac.this);
                        String stime = String.valueOf(json_data.getString("transacname"));
                        ti[i]= String.valueOf(json_data.getString("transacid"));
                        tn[i]= String.valueOf(json_data.getString("transacname"));
                        st[i]= String.valueOf(json_data.getString("starttime"));
                        et[i]= String.valueOf(json_data.getString("endtime"));
                        eet[i]= String.valueOf(json_data.getString("estimatedtime"));
                        tc[i]=String.valueOf(json_data.getString("companyid"));
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
                                Intent intent = new Intent(ViewMakeTransac.this, ConfirmTransac.class);
                                intent.putExtra("TRANSACID",ti[count]);
                                intent.putExtra("TRANSACNAME",tn[count]);
                                intent.putExtra("STARTTIME",st[count]);
                                intent.putExtra("ENDTIME",et[count]);
                                intent.putExtra("ESTIMATEDTIME",eet[count]);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.logout:
                SharedPreferences.Editor editor = SaveSharedPreference.getSharedPreferences(ViewMakeTransac.this).edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(ViewMakeTransac.this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.profile:
                Intent intent1 = new Intent(ViewMakeTransac.this, ProfileActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}

