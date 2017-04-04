package com.garudatekno.jemaah.menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.chat.CustomList;
import com.garudatekno.jemaah.activity.MainActivity;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class inbox extends AppCompatActivity implements ListView.OnItemClickListener {

    private static final String TAG = "MyUser";

    private ListView listView;
    String uid;
    private String JSON_STRING;
    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        //header
        LinearLayout menu_panduan=(LinearLayout) findViewById(R.id.menu_panduan);
        TextView txt_panduan=(TextView) findViewById(R.id.txt_panduan);
        LinearLayout menu_doa=(LinearLayout) findViewById(R.id.menu_doa);
        TextView txt_doa=(TextView) findViewById(R.id.txt_doa);
        LinearLayout menu_emergency=(LinearLayout) findViewById(R.id.menu_emergency);
        TextView txt_emergency=(TextView) findViewById(R.id.txt_emergency);
        LinearLayout menu_profile=(LinearLayout) findViewById(R.id.menu_profile);
        TextView txt_profile=(TextView) findViewById(R.id.txt_profile);
        LinearLayout menu_inbox=(LinearLayout) findViewById(R.id.menu_inbox);
        TextView txt_inbox=(TextView) findViewById(R.id.txt_inbox);
        txt_inbox.setTextColor(getResources().getColor(R.color.colorTextActive));
        ImageView img_inbox=(ImageView) findViewById(R.id.img_inbox);
        img_inbox.setImageDrawable(getResources().getDrawable(R.drawable.inbox_active));
        menu_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), profile.class);
                startActivity(i);
            }
        });

        menu_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), emergency.class);
                startActivity(i);
            }
        });
        menu_panduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
            }
        });
        menu_doa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Doa.class);
                startActivity(i);
            }
        });
        //FOOTER
        TextView txt_thowaf=(TextView) findViewById(R.id.txt_thowaf);
        TextView txt_go=(TextView) findViewById(R.id.txt_go);
        TextView txt_sai=(TextView) findViewById(R.id.txt_sai);
        txt_thowaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        getJSON();

    }


    private void showData(){
        JSONObject jsonObject = null;
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(AppConfig.TAG_JSON_ARRAY);
            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(AppConfig.KEY_ID);
                String message = jo.getString(AppConfig.KEY_MESSAGE);
                String time = jo.getString(AppConfig.KEY_TIME);
                String from = jo.getString(AppConfig.KEY_FROM);
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_ID,id);
                data.put(AppConfig.KEY_MESSAGE,message);
                data.put(AppConfig.KEY_TIME,time);
                data.put(AppConfig.KEY_FROM,from);
                list.add(data);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomList adapter = new CustomList(this, list,
                R.layout.list_chat, new String[] { AppConfig.KEY_ID,AppConfig.KEY_MESSAGE,AppConfig.KEY_TIME,AppConfig.KEY_FROM },
                new int[] { R.id.txtNO,R.id.txtMESSAGE,R.id.txtTIME,R.id.txtFROM });

        listView.setAdapter(adapter);
        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(inbox.this,"Fetching Data","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showData();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(AppConfig.URL_GET_INBOX,uid);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(this, ViewData.class);
//        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
//        String empId = map.get(AppConfig.TAG_ID).toString();
//        intent.putExtra(AppConfig.EMP_ID,empId);
//        startActivity(intent);
    }
}
