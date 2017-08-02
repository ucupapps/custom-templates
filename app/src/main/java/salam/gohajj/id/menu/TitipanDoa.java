package salam.gohajj.id.menu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.readystatesoftware.viewbadger.BadgeView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;
import me.leolin.shortcutbadger.ShortcutBadger;
import salam.gohajj.id.R;
import salam.gohajj.id.activity.LoginActivity;
import salam.gohajj.id.activity.RequestHandler;
import salam.gohajj.id.app.AppConfig;
import salam.gohajj.id.app.AppController;
import salam.gohajj.id.helper.SQLiteHandler;
import salam.gohajj.id.helper.SessionManager;

public class TitipanDoa extends AppCompatActivity implements ListView.OnItemClickListener {

    private static final String TAG = "MyUser";

    private LinearLayout doaLinear;

    private ListDoa adapter;

    private boolean isLoadMore = false;

    private View footerView;

    private int loadMoreTextViewLength;

    private int limit;

    private String max_data;

    private ArrayList<items> list = new ArrayList<>();

    private ListView listView;
    private String JSON_STRING,uid,nama;
    private SQLiteHandler db;
    private SessionManager session;
    private Tracker mTracker;
    View target ;
    BadgeView badge ;
    TextView txtkoneksi,vname;
    ImageView arrow_back,icon_private;
    EditText to,pesan,status;
    Button BtnKirim;
    private SQLiteDatabase database;
    private FloatingActionButton BtnAdd;
    Dialog rankDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doa);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, null, false);

        limit = 7;

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
        nama = user.get("name");

        database = openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        CountInbox();

        //end

        session = new SessionManager(getApplicationContext());
        listView = (ListView) findViewById(R.id.listView);
        listView.destroyDrawingCache();
        listView.setOnItemClickListener(this);

        listView.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Do nothing
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if((lastInScreen == totalItemCount)){
                    if(isLoadMore == false)
                    {
                        if(loadMoreTextViewLength != 0)
                        {
                            if(limit < Integer.parseInt(max_data)) {
                                isLoadMore = true;
                                limit += 7;
                                // Add footer to ListView
                                listView.addFooterView(footerView);
                                getJSON();
                            }
                        }
                    }
                }
            }
        });

        // Creating a button - Load More
//        Button btnLoadMore = new Button(this);
//        btnLoadMore.setText("Load More");

        // Adding button to listview at footer
//        listView.addFooterView(btnLoadMore);

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

        //popup
        rankDialog = new Dialog(TitipanDoa.this);
        rankDialog.setContentView(R.layout.create_titip_doa);
        rankDialog.setCanceledOnTouchOutside(false);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/helvetica.ttf");
        vname = (TextView) rankDialog.findViewById(R.id.vname);
        arrow_back = (ImageView) rankDialog.findViewById(R.id.arrow_back);
        to = (EditText) rankDialog.findViewById(R.id.to);
        pesan = (EditText) rankDialog.findViewById(R.id.message);
        status = (EditText) rankDialog.findViewById(R.id.status);
        BtnKirim = (Button) rankDialog.findViewById(R.id.btnKirim);
        vname.setTypeface(font);
        BtnKirim.setTypeface(font);
        to.setTypeface(font);
        pesan.setTypeface(font);

        rankDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    rankDialog.dismiss();
                }
                return true;
            }
        });

        BtnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stat = status.getText().toString().trim();
                String ps = pesan.getText().toString().trim();
                String kepada = to.getText().toString().trim();

                    if(stat.equals("private") ) {
                        if(kepada.equals("") ) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_tidak_boleh_kosong), Toast.LENGTH_SHORT).show();
                        }else if(!kepada.equals("") && !kepada.matches(emailPattern) ) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.format_email_salah), Toast.LENGTH_SHORT).show();
                        }else if(ps.equals("") ) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.pesan_tidak_boleh_kosong), Toast.LENGTH_SHORT).show();
                        }else{
                            rankDialog.dismiss();
                            SendDoa();
                        }
                    }

                if(stat.equals("public") ) {
                    if(ps.equals("") ) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.pesan_tidak_boleh_kosong), Toast.LENGTH_SHORT).show();
                    }else{
                        rankDialog.dismiss();
                        SendDoa();
                    }
                }
            }
        });

        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rankDialog.dismiss();
            }
        });

        BtnAdd =(FloatingActionButton) findViewById(R.id.fab);
        final PopupMenu addkontak = new PopupMenu(this, BtnAdd);
        addkontak.getMenu().add(1, 1, 1, "Personal");
        addkontak.getMenu().add(1, 2, 2, getResources().getString(R.string.publik));

        BtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addkontak.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == 1) {
                            to.setVisibility(View.VISIBLE);
                            status.setText("private");
                            rankDialog.show();
                        }if(id == 2) {
                            to.setVisibility(View.GONE);
                            status.setText("public");
                            rankDialog.show();
                        }
                        return true;
                    }
                });
                addkontak.show();
            }
        });

        //useri mage
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/salam.gohajj.id/images/"+uid+".png");
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
        int position = listView.getLastVisiblePosition();
//        ArrayList<items> list = new ArrayList<>();
        Log.e("hasil", JSON_STRING);

        try {
            jsonObject = new JSONObject(JSON_STRING);
//            JSONArray result = jsonObject.getJSONArray(AppConfig.TAG_JSON_ARRAY);
            JSONObject result = jsonObject.getJSONObject("result");
            max_data = jsonObject.getString("all_data");
            loadMoreTextViewLength = result.length();
//            Log.e("jummlah json : ", String.valueOf(loadMoreTextViewLength) + " "+ max_data);
            if(result != null)
            {
                if(result.equals(""))
                {
//                    MyUtility.saveFlagFromMessageThread(homeActivity, false);
//                    fromPushNotification = false;
                    if(isLoadMore)
                    {
                        listView.removeFooterView(footerView);
                        isLoadMore = false;
                    }
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    if(!result.equals("null"))
                    {
                        if(isLoadMore)
                        {
                            listView.removeFooterView(footerView);
                            isLoadMore = false;
                        }

                        if(result.length() > 0)
                        {
                            list.clear();
                            for(int i = 0; i<result.length(); i++){
//                                JSONObject jo = result.getJSONObject(i);
                                JSONObject jo = result.getJSONObject(String.valueOf(i+1));
                                String id = jo.getString(AppConfig.KEY_ID);
                                String message = jo.getString(AppConfig.KEY_MESSAGE);
                                String time = jo.getString(AppConfig.KEY_TIME);
                                String userId = jo.getString(AppConfig.KEY_USERID);
                                String from = jo.getString(AppConfig.KEY_FROM);
                                String jum = jo.getString(AppConfig.KEY_JUMLAH);
                                String sts = jo.getString(AppConfig.KEY_STATUS);

                                list.add(new items(id,message,time,userId,from,jum,sts));
                            }
                            // add items into Arralist
                            doaLinear.removeAllViews();
                            adapter = new ListDoa(list);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            doaLinear.addView(listView);
                            listView.setSelectionFromTop(position, 0);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(isLoadMore)
            {
                listView.removeFooterView(footerView);
                isLoadMore = false;
            }
        }

//        try {
//            jsonObject = new JSONObject(JSON_STRING);
//            JSONArray result = jsonObject.getJSONArray(AppConfig.TAG_JSON_ARRAY);
//
//            for(int i = 0; i<result.length(); i++){
////                ArrayList<items> list = new ArrayList<>();
//                JSONObject jo = result.getJSONObject(i);
//                String id = jo.getString(AppConfig.KEY_ID);
//                String message = jo.getString(AppConfig.KEY_MESSAGE);
//                String time = jo.getString(AppConfig.KEY_TIME);
//                String userId = jo.getString(AppConfig.KEY_USERID);
//                String from = jo.getString(AppConfig.KEY_FROM);
//                String jum = jo.getString(AppConfig.KEY_JUMLAH);
//                String sts = jo.getString(AppConfig.KEY_STATUS);
////                HashMap<String,String> data = new HashMap<>();
////                data.put(AppConfig.KEY_ID,id);
////                data.put(AppConfig.KEY_MESSAGE,message);
////                data.put(AppConfig.KEY_MESSAGE,message);
////                data.put(AppConfig.KEY_FROM,from);
////                data.put(AppConfig.KEY_JUMLAH,jum);
//                list.add(new items(id,message,time,userId,from,jum,sts));
//            }
//
//
////        adapter = new CustomListDoa(TitipanDoa.this, R.layout.list_doa,list);
//        doaLinear.removeAllViews();
//        adapter = new ListDoa(list);
//        listView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//        doaLinear.addView(listView);
//            listView.setSelectionFromTop(position, 0);
//
//        } catch (JSONException e) {
////            e.printStackTrace();
//        }
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
//                loading = ProgressDialog.show(TitipanDoa.this,"","Mohon Tunggu...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                loading.dismiss();
                JSON_STRING = s;
                    showData();
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_LIMIT, String.valueOf(limit));
                data.put(AppConfig.KEY_ID, uid);
//                Log.e("data kirim :" , String.valueOf(data));
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(AppConfig.URL_TITIPAN_DOA,data);
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
                loading = ProgressDialog.show(TitipanDoa.this,"",getResources().getString(R.string.mengirim)+"...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showData();

                Toast.makeText(TitipanDoa.this, getResources().getString(R.string.terimakasih_telah_mendoakan), Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_ID, id);
                data.put(AppConfig.KEY_USERID, uid);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(AppConfig.URL_DOAKAN, data);
                return res;
            }
        }

        AddBarcode ae = new AddBarcode();
        ae.execute();
//        startActivity(new Intent(TitipanDoa.this, TitipanDoa.class));

    }

    public void SendDoa(){

        final String psn = pesan.getText().toString().trim();
        final String sts = status.getText().toString().trim();
        final String t = to.getText().toString().trim();

        class send extends AsyncTask<Bitmap,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TitipanDoa.this,"",getResources().getString(R.string.mengirim)+"...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(TitipanDoa.this, getResources().getString(R.string.pesan_telah_dikirim), Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_STATUS, sts);
                data.put(AppConfig.KEY_TO, t);
                data.put(AppConfig.KEY_MESSAGE, psn);
                data.put(AppConfig.KEY_NAME, nama);
                data.put(AppConfig.KEY_USERID, uid);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(AppConfig.URL_SEND_DOA, data);
                return res;
            }
        }

        send ae = new send();
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
            icon_private = (ImageView) itemView.findViewById(R.id.icon_private);

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
            String stsStatus = list.get(position).getStatus();
            if(stsStatus.equals("private")){
                icon_private.setVisibility(View.VISIBLE);
            }else{
                icon_private.setVisibility(View.GONE);
            }
            no.setText(strID);
            message.setText(strMessage);
            time.setText(strTime);
            from.setText(strFrom+" ");
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
