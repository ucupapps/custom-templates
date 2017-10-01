package salam.gohajj.custom.TabFragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.app.Fragment;
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
import android.widget.ListAdapter;
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
import salam.gohajj.custom.R;
import salam.gohajj.custom.Utilities;
import salam.gohajj.custom.activity.RequestHandler;
import salam.gohajj.custom.app.AppConfig;
import salam.gohajj.custom.app.AppController;
import salam.gohajj.custom.helper.SQLiteHandler;
import salam.gohajj.custom.helper.SessionManager;
import salam.gohajj.custom.menu.items;


public class TitipanDoaFragment extends Fragment implements ListView.OnItemClickListener {

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
    public TitipanDoaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        database = getActivity().openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.tab_titip_doa, container, false);
        Calligrapher calligrapher=new Calligrapher(getContext());
        calligrapher.setFont(getActivity(),"fonts/helvetica.ttf",true);

        footerView = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, null, false);

        limit = 7;

        session = new SessionManager(getContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        txtkoneksi= (TextView) vi.findViewById(R.id.txtkoneksi);

        if (!Utilities.cek_status(getContext()))
        {
            txtkoneksi.setVisibility(View.VISIBLE);
        }else{
            txtkoneksi.setVisibility(View.GONE);
        }

        doaLinear = (LinearLayout) vi.findViewById(R.id.linearDoa);
        //tracker
        AppController application = (AppController)getContext().getApplicationContext();
        mTracker = application.getDefaultTracker();
        sendScreenImageName("Titip Doa");

        // SqLite database handler
        db = new SQLiteHandler(getContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        nama = user.get("name");

        database = getActivity().openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
//        CountInbox();

        //end

        session = new SessionManager(getContext());
        listView = (ListView) vi.findViewById(R.id.listView);
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

//        final ImageView img_home=(ImageView) vi.findViewById(R.id.img_home);
//        img_home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getContext(), panduan.class);
//                startActivity(i);
//                getActivity().finish();
//            }
//        });
//        final  ImageView img_setting=(ImageView) vi.findViewById(R.id.img_setting);
//        img_setting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getContext(), setting.class);
//                startActivity(i);
//                getActivity().finish();
//            }
//        });

        //popup
        rankDialog = new Dialog(TitipanDoaFragment.this.getContext());
        rankDialog.setContentView(R.layout.create_titip_doa);
        rankDialog.setCanceledOnTouchOutside(false);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/helvetica.ttf");
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
                        Toast.makeText(getContext(), getResources().getString(R.string.email_tujuan_tidak_boleh_kosong), Toast.LENGTH_SHORT).show();
                    }else if(!kepada.equals("") && !kepada.matches(emailPattern) ) {
                        Toast.makeText(getContext(), getResources().getString(R.string.format_email_salah), Toast.LENGTH_SHORT).show();
                    }else if(ps.equals("") ) {
                        Toast.makeText(getContext(), getResources().getString(R.string.pesan_tidak_boleh_kosong), Toast.LENGTH_SHORT).show();
                    }else{
                        rankDialog.dismiss();
                        SendDoa();
                    }
                }

                if(stat.equals("public") ) {
                    if(ps.equals("") ) {
                        Toast.makeText(getContext(), getResources().getString(R.string.pesan_tidak_boleh_kosong), Toast.LENGTH_SHORT).show();
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

        BtnAdd =(FloatingActionButton) vi.findViewById(R.id.fab);
        final PopupMenu addkontak = new PopupMenu(getContext(), BtnAdd);
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

//        //useri mage
//        CircleImageView imgp = (CircleImageView) vi.findViewById(R.id.img_profile);
//        File file = new File("/sdcard/android/data/salam.gohajj.id/images/"+uid+".png");
//        if (!file.exists()) {
//            imgp.setImageResource(R.drawable.profile);
//        }else{
//            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
//            imgp.setImageBitmap(bmp);
//        }
        if (session.isLoggedIn()) {
            getJSON();
        }

        return vi;
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
                            listView.setAdapter((ListAdapter) adapter);
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
        class GetJSON extends AsyncTask<Void,Void,String> {

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
//        Intent intent = new Intent(TitipDoaFragment.this.getContext(), LoginActivity.class);
//        startActivity(intent);
//        getActivity().finish();
    }

    public void addDoakan(final String id){
        class AddBarcode extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TitipanDoaFragment.this.getContext(),"",getResources().getString(R.string.mengirim)+"...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showData();

                Toast.makeText(TitipanDoaFragment.this.getContext(), getResources().getString(R.string.terimakasih_telah_mendoakan), Toast.LENGTH_SHORT).show();
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
                loading = ProgressDialog.show(TitipanDoaFragment.this.getContext(),"",getResources().getString(R.string.mengirim)+"...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(TitipanDoaFragment.this.getContext(), getResources().getString(R.string.pesan_telah_dikirim), Toast.LENGTH_SHORT).show();
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
            super(TitipanDoaFragment.this.getContext(), R.layout.list_doa, ItemList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if(itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.list_doa, parent, false);
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

            Typeface font = Typeface.createFromAsset(TitipanDoaFragment.this.getContext().getAssets(), "fonts/helvetica.ttf");
            message.setTypeface(font);
            time.setTypeface(font);
            from.setTypeface(font, Typeface.BOLD);
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

    protected void CountInbox(){
        Cursor c= database.rawQuery("select * from badge where id=1 ", null);
        c.moveToFirst();
        int jumlah=c.getInt(1);
        target = getActivity().findViewById(R.id.img_inbox);
        badge = new BadgeView(getActivity(), target);
        //badge
        if(jumlah > 0) {
            badge.setText("" + jumlah);
            badge.show();
            ShortcutBadger.applyCount(getContext(), jumlah);
        }else{
            ShortcutBadger.removeCount(getContext());
            badge.hide();
        }
    }
}
