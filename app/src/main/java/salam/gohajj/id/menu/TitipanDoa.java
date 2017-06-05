package salam.gohajj.id.menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import salam.gohajj.id.R;
import salam.gohajj.id.activity.LoginActivity;
import salam.gohajj.id.activity.RequestHandler;
import salam.gohajj.id.app.AppConfig;
import salam.gohajj.id.app.AppController;
import salam.gohajj.id.helper.SQLiteHandler;
import salam.gohajj.id.helper.SessionManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.readystatesoftware.viewbadger.BadgeView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;
import me.leolin.shortcutbadger.ShortcutBadger;

import static java.sql.Types.NULL;

public class TitipanDoa extends AppCompatActivity implements ListView.OnItemClickListener {

    private static final String TAG = "MyUser";

    private LinearLayout doaLinear;

    private ListDoa adapter;

    private ArrayList<items> list = new ArrayList<>();

    private ListView listView;
    private String JSON_STRING,uid;
    private SQLiteHandler db;
    private SessionManager session;
    private Tracker mTracker;
    View target ;
    BadgeView badge ;
    TextView txtkoneksi;
    private SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doa);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        txtkoneksi= (TextView) findViewById(R.id.txtkoneksi);

        if (!cek_status(getApplicationContext()))
        {
            txtkoneksi.setVisibility(View.VISIBLE);
        }else{
            txtkoneksi.setVisibility(View.GONE);
        }

        doaLinear = (LinearLayout) findViewById(R.id.linearDoa);
        //tracker
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        sendScreenImageName("Titip Doa");

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        database = openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        CountInbox();

        //end

        session = new SessionManager(getApplicationContext());
        listView = (ListView) findViewById(R.id.listView);
        listView.destroyDrawingCache();
        listView.setOnItemClickListener(this);


        //HEADER
        TextView txt_emergency=(TextView) findViewById(R.id.txt_emergency);
        TextView txt_thowaf=(TextView) findViewById(R.id.txt_thowaf);
        TextView txt_sai=(TextView) findViewById(R.id.txt_sai);
        txt_thowaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), thawaf.class);
                startActivity(i);
                finish();
            }
        });
        txt_sai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), sai.class);
                startActivity(i);
                finish();
            }
        });
        txt_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), emergency.class);
                startActivity(i);
                finish();
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
                finish();
            }
        });
        menu_panduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
                finish();
            }
        });
        menu_doa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TitipanDoa.class);
                startActivity(i);
                finish();
            }
        });
        menu_navigasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), navigasi.class);
                startActivity(i);
                finish();
            }
        });
        menu_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), inbox.class);
                startActivity(i);
                finish();
            }
        });

        final ImageView img_home=(ImageView) findViewById(R.id.img_home);
        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
                finish();
            }
        });
        final  ImageView img_setting=(ImageView) findViewById(R.id.img_setting);
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), setting.class);
                startActivity(i);
                finish();
            }
        });


        //useri mage
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/com.gohajj.id/images/profile.png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }
        if (session.isLoggedIn()) {
            getJSON();
        }

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
                String userId = jo.getString(AppConfig.KEY_USERID);
                String from = jo.getString(AppConfig.KEY_FROM);
                String jum = jo.getString(AppConfig.KEY_JUMLAH);
//                HashMap<String,String> data = new HashMap<>();
//                data.put(AppConfig.KEY_ID,id);
//                data.put(AppConfig.KEY_MESSAGE,message);
//                data.put(AppConfig.KEY_MESSAGE,message);
//                data.put(AppConfig.KEY_FROM,from);
//                data.put(AppConfig.KEY_JUMLAH,jum);
                list.add(new items(id,message,time,userId,from,jum));
            }


//        adapter = new CustomListDoa(TitipanDoa.this, R.layout.list_doa,list);
        doaLinear.removeAllViews();
        adapter = new ListDoa(list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        doaLinear.addView(listView);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendScreenImageName(String name) {
        // [START screen_view_hit]
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TitipanDoa.this,"","Mohon Tunggu...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                if(!JSON_STRING.equals(NULL) || !JSON_STRING.equals("")) {
                    showData();
                }
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
                loading = ProgressDialog.show(TitipanDoa.this,"","Mengirim...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showData();

                Toast.makeText(TitipanDoa.this, "Terimakasih telah mendoakan", Toast.LENGTH_SHORT).show();
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


            CircleImageView imgDoa = (CircleImageView) itemView.findViewById(R.id.imgprofile_doa);
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
            String strIdUser = list.get(position).getUserid();
            String strFrom = list.get(position).getFrom();
            String strJum = list.get(position).getJum();
            no.setText(strID);
            message.setText(strMessage);
            time.setText(strTime);
            from.setText(strFrom);
            jum.setText(strJum);

            Picasso.with(getContext()).load(AppConfig.URL_HOME+"/uploads/profile/"+strIdUser+"/agent.jpg").memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).error(R.drawable.profile).into(imgDoa);

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

    public boolean cek_status(Context cek) {

        ConnectivityManager cm = (ConnectivityManager) cek.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected())
        {
            return true;
        } else{
            return false;
        }
    }

    protected void CountInbox(){
        Cursor c= database.rawQuery("select * from badge where id=1 ", null);
        c.moveToFirst();
        int jumlah=c.getInt(1);
        target = findViewById(R.id.img_inbox);
        badge = new BadgeView(this, target);
        //badge
        if(jumlah > 0) {
            badge.setText("" + jumlah);
            badge.show();
            ShortcutBadger.applyCount(getApplicationContext(), jumlah);
        }else{
            ShortcutBadger.removeCount(getApplicationContext());
            badge.hide();
        }
    }
}