package com.garudatekno.jemaah.menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;

public class TitipanDoa extends AppCompatActivity implements ListView.OnItemClickListener {

    private static final String TAG = "MyUser";

    private LinearLayout doaLinear;

    private ListDoa adapter;

    private ArrayList<items> list = new ArrayList<>();

    private ListView listView;
    private String JSON_STRING,uid;
    private SQLiteHandler db;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doa);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        session = new SessionManager(getApplicationContext());
        listView = (ListView) findViewById(R.id.listView);
        listView.destroyDrawingCache();
        listView.setOnItemClickListener(this);

        doaLinear = (LinearLayout) findViewById(R.id.linearDoa);

        //HEADER
        TextView txt_emergency=(TextView) findViewById(R.id.txt_emergency);
        TextView txt_thowaf=(TextView) findViewById(R.id.txt_thowaf);
        TextView txt_sai=(TextView) findViewById(R.id.txt_sai);
        txt_thowaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), thawaf.class);
                startActivity(i);
            }
        });
        txt_sai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), sai.class);
                startActivity(i);
            }
        });
        txt_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), emergency.class);
                startActivity(i);
            }
        });

        // FOOTER
        LinearLayout menu_panduan=(LinearLayout) findViewById(R.id.menu_panduan);
        TextView txt_panduan=(TextView) findViewById(R.id.txt_panduan);
        LinearLayout menu_doa=(LinearLayout) findViewById(R.id.menu_doa);
        TextView txt_doa=(TextView) findViewById(R.id.txt_doa);
        LinearLayout menu_navigasi=(LinearLayout) findViewById(R.id.menu_navigasi);
        TextView txt_navigasi=(TextView) findViewById(R.id.txt_emergency);
        LinearLayout menu_profile=(LinearLayout) findViewById(R.id.menu_profile);
        TextView txt_profile=(TextView) findViewById(R.id.txt_profile);
        LinearLayout menu_inbox=(LinearLayout) findViewById(R.id.menu_inbox);
        TextView txt_inbox=(TextView) findViewById(R.id.txt_inbox);

        ImageView img = (ImageView) findViewById(R.id.img_doa);
        img.setBackgroundResource(R.drawable.circle_green_active);
        img.setPadding(22,22,22,22);
        img.setImageDrawable(getResources().getDrawable(R.drawable.doa_active));

        menu_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), profile.class);
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
                Intent i = new Intent(getApplicationContext(), TitipanDoa.class);
                startActivity(i);
            }
        });
        menu_navigasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), navigasi.class);
                startActivity(i);
            }
        });
        menu_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), inbox.class);
                startActivity(i);
            }
        });

        final ImageView img_home=(ImageView) findViewById(R.id.img_home);
        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
            }
        });
        final  ImageView img_setting=(ImageView) findViewById(R.id.img_setting);
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), setting.class);
                startActivity(i);
            }
        });

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        //useri mage
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/com.garudatekno.jemaah/images/profile.png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }
        getJSON();

    }

    private void showData(){
        JSONObject jsonObject = null;
//        ArrayList<items> list = new ArrayList<>();

        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(AppConfig.TAG_JSON_ARRAY);
            for(int i = 0; i<result.length(); i++){
//                ArrayList<items> list = new ArrayList<>();
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(AppConfig.KEY_ID);
                String message = jo.getString(AppConfig.KEY_MESSAGE);
                String time = jo.getString(AppConfig.KEY_TIME);
                String from = jo.getString(AppConfig.KEY_FROM);
                String jum = jo.getString(AppConfig.KEY_JUMLAH);
//                HashMap<String,String> data = new HashMap<>();
//                data.put(AppConfig.KEY_ID,id);
//                data.put(AppConfig.KEY_MESSAGE,message);
//                data.put(AppConfig.KEY_MESSAGE,message);
//                data.put(AppConfig.KEY_FROM,from);
//                data.put(AppConfig.KEY_JUMLAH,jum);
                list.add(new items(id,message,time,from,jum));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        adapter = new CustomListDoa(TitipanDoa.this, R.layout.list_doa,list);
        doaLinear.removeAllViews();
        adapter = new ListDoa(list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        doaLinear.addView(listView);
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TitipanDoa.this,"","",false,false);
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
                String s = rh.sendGetRequest(AppConfig.URL_TITIPAN_DOA);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void logoutUser() {
        if (session.isLoggedIn()) {
            session.setLogin(false);
            db.deleteUsers();
        }

        // Launching the login activity
        Intent intent = new Intent(TitipanDoa.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void addDoakan(final String id){
        class AddBarcode extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TitipanDoa.this,"","",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showData();

//                Toast.makeText(TitipanDoa.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_ID, id);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(AppConfig.URL_DOAKAN, data);
                return res;
            }
        }

        AddBarcode ae = new AddBarcode();
        ae.execute();
//        startActivity(new Intent(TitipanDoa.this, TitipanDoa.class));

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
//        final String empId = map.get(AppConfig.TAG_ID).toString();
//        final Button btndoakan = (Button)view.findViewById(R.id.btndoakan);
//        btndoakan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Toast.makeText(getApplicationContext(),"Download "+empId, Toast.LENGTH_LONG).show();
//                addDoakan(empId);
//            }
//        });
    }

    private class ListDoa extends ArrayAdapter<items> {
        public ListDoa(ArrayList<items> ItemList) {
            super(TitipanDoa.this, R.layout.list_doa, ItemList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.list_doa, parent, false);
            }


            TextView no = (TextView)itemView.findViewById(R.id.txtNO);
            TextView message = (TextView)itemView.findViewById(R.id.txtMESSAGE);
            TextView time = (TextView)itemView.findViewById(R.id.txtTIME);
            TextView from = (TextView)itemView.findViewById(R.id.txtFROM);
            TextView jum = (TextView)itemView.findViewById(R.id.txtJumlah);
            TextView titip = (TextView)itemView.findViewById(R.id.txttitip);
            Button btndoakan = (Button)itemView.findViewById(R.id.btndoakan);

            Typeface font = Typeface.createFromAsset(TitipanDoa.this.getAssets(), "fonts/helvetica.ttf");
            message.setTypeface(font);
            time.setTypeface(font);
            from.setTypeface(font,Typeface.BOLD);
            jum.setTypeface(font);
            titip.setTypeface(font);
            btndoakan.setTypeface(font);

            final String strID = list.get(position).getId();
            String strMessage = list.get(position).getMessage();
            String strTime = list.get(position).getTime();
            String strFrom = list.get(position).getFrom();
            String strJum = list.get(position).getJum();
            no.setText(strID);
            message.setText(strMessage);
            time.setText(strTime);
            from.setText(strFrom);
            jum.setText(strJum);

            btndoakan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                notifyDataSetChanged();
//                    TitipanDoa d= new TitipanDoa();
//                    d.addDoakan(strID);
                    list.clear();
                    addDoakan(strID);
//                Intent intent= new Intent(mContext, TitipanDoa.class);
//                ((Activity)mContext).finish();
//                mContext.startActivity(intent);
                }


            });

            return itemView;

        }
    }
}
