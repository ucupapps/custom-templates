package com.garudatekno.jemaah.sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.CustomList;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewAll extends AppCompatActivity implements ListView.OnItemClickListener {

    private static final String TAG = "MyUser";

    private ListView listView;

    private String JSON_STRING;
    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_sample);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        getJSON();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refresh:
                getJSON();
                return true;
        }
        return false;
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
                String name = jo.getString(AppConfig.KEY_NAME);
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_ID,id);
                data.put(AppConfig.KEY_NAME,name);
                list.add(data);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomList adapter = new CustomList(this, list,
                R.layout.list_item, new String[] { AppConfig.KEY_NAME },
                new int[] { R.id.from });
//                R.layout.list_item, new String[] { AppConfig.UPLOAD_KEY, AppConfig.KEY_VALUE, AppConfig.KEY_BARCODE },
//                new int[] { R.id.userIcon, R.id.username, R.id.usertext });

//        ListAdapter adapter = new SimpleAdapter(
//                ViewAll.this, list, R.layout.list_item,
////                new String[]{Config.TAG_ID,Config.TAG_NAME},
////                new int[]{R.id.id, R.id.name});
//                new String[]{AppConfig.KEY_VALUE,AppConfig.KEY_BARCODE,AppConfig.UPLOAD_KEY},
//                new int[]{ R.id.title,R.id.text,R.id.icon});

        listView.setAdapter(adapter);
        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewAll.this,"Fetching Data","Wait...",false,false);
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
                HashMap<String, String> user = db.getUserDetails();
                String id = user.get("uid");
                Log.d(TAG, "IDUser: " + id);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(AppConfig.URL_ORDER_LIST,id);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ViewData.class);
        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
        String empId = map.get(AppConfig.TAG_ID).toString();
        intent.putExtra(AppConfig.EMP_ID,empId);
        startActivity(intent);
    }
}
