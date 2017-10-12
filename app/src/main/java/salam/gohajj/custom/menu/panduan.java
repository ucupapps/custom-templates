package salam.gohajj.custom.menu;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.nightonke.boommenu.Util;
import com.readystatesoftware.viewbadger.BadgeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;
import me.leolin.shortcutbadger.ShortcutBadger;
import salam.gohajj.custom.GenericPopup;
import salam.gohajj.custom.GetTemplates;
import salam.gohajj.custom.Interfaces;
import salam.gohajj.custom.R;
import salam.gohajj.custom.TabFragment.InboxFragment;
import salam.gohajj.custom.TabFragment.NavigasiFragment;
import salam.gohajj.custom.TabFragment.PanduanFragment;
import salam.gohajj.custom.TabFragment.ProfilFragment;
import salam.gohajj.custom.TabFragment.TitipanDoaFragment;
import salam.gohajj.custom.Utilities;
import salam.gohajj.custom.activity.CustomListPanduan3;
import salam.gohajj.custom.activity.LoginActivity;
import salam.gohajj.custom.activity.RequestHandler;
import salam.gohajj.custom.app.AppConfig;
import salam.gohajj.custom.app.AppController;
import salam.gohajj.custom.helper.SQLiteHandler;
import salam.gohajj.custom.helper.SessionManager;

import static salam.gohajj.custom.app.AppConfig.URL_HOME;


public class panduan extends AppCompatActivity implements ListView.OnItemClickListener {
    private Activity mActivity;
    private static final String TAG = "MyHOME";
    private ListView listView;
    private TextView txtid,jak_degree,jak_cuaca,jak_date,jak_time,mek_degree,mek_cuaca,mek_date,mek_time;
    private String JSON_STRING,uid;
    private SQLiteHandler db;
    private SessionManager session;
    MediaPlayer mMediaPlayer ;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static int tabIndex;
    private static final String[] requiredPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.RECORD_AUDIO,
    };
    private Tracker mTracker;
    boolean doubleBackToExitPressedOnce = false;
    View target ;
    BadgeView badge ;
    private SQLiteDatabase database;
    static final Integer READ_EXST = 0x4;
    private String getpref;
    private LinearLayout gohajjMenu, place1, place2;
    private FloatingActionMenu floatingMenu;
    private BoomMenuButton bmb;

    public static int getTabIndex() {
        return tabIndex;
    }

    public static void setTabIndex(int tabIndex) {
        panduan.tabIndex = tabIndex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView
        SetContentView();

        database = openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        String query = "INSERT INTO loader (status) VALUES(1);";
        database.execSQL(query);
        ChooseTemplate();

        if (Build.VERSION.SDK_INT > 22 && !hasPermissions(requiredPermissions)) {
            //permission
            if (ContextCompat.checkSelfPermission(panduan.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, READ_EXST);
            }
//            else if (ContextCompat.checkSelfPermission(panduan.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                askForPermission(Manifest.permission.RECORD_AUDIO, READ_EXST);
//            }
        }
        CountInbox();
        //tracker
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        sendScreenImageName("Home");

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        session = new SessionManager(getApplicationContext());

        //useri mage
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/salam.gohajj.custom/images/"+uid+".png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }

        ImageView img = (ImageView) findViewById(R.id.img_panduan);
        img.setBackgroundResource(R.drawable.circle_green_active);
        img.setPadding(22,22,22,22);
        img.setImageDrawable(getResources().getDrawable(R.drawable.panduan_active));

        ImageView rankBtn = (ImageView) findViewById(R.id.img_center);
//        rankBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
//                startActivity(i);
//                final Dialog rankDialog = new Dialog(panduan.this);
//                rankDialog.setContentView(R.layout.rank_dialog);
//                rankDialog.setCancelable(true);
//
//                Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
//                updateButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RatingBar ratingBar = (RatingBar)rankDialog.findViewById(R.id.dialog_ratingbar);
//                        String rating=String.valueOf(ratingBar.getRating());
//                        addRating(uid,rating);
////                        Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();
//                        rankDialog.dismiss();
//                    }
//                });
//                //now that the dialog is set up, it's time to show it
//                rankDialog.show();
//            }
//        });

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

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        if (getpref.equals(Interfaces.TEMPLATE_1)){
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    ImageView titleImg = (ImageView)findViewById(R.id.img_center);
                    TextView titleMenu = (TextView)findViewById(R.id.title_menu);
                    titleImg.setVisibility(View.GONE);
                    titleMenu.setVisibility(View.VISIBLE);
                    final TextView txtkoneksi= (TextView) findViewById(R.id.txtkoneksi);
                    if (tab.getPosition()==0){
                        titleMenu.setText(R.string.panduan);
                        place1.setVisibility(View.VISIBLE);
                        place2.setVisibility(View.VISIBLE);
                        Utilities.ShowLog("panduan",""+tab.getPosition());
                        if (!Utilities.cek_status(getApplicationContext())) {
                            txtkoneksi.setVisibility(View.VISIBLE);
                        } else {
                            txtkoneksi.setVisibility(View.GONE);
                        }
                    }else if (tab.getPosition()==1){
                        titleMenu.setText(R.string.titip_doa);
                        txtkoneksi.setVisibility(View.GONE);
                        place1.setVisibility(View.GONE);
                        place2.setVisibility(View.GONE);
                    }else if (tab.getPosition()==2){
                        titleMenu.setText(R.string.navigasi);
                        txtkoneksi.setVisibility(View.GONE);
                        place1.setVisibility(View.GONE);
                        place2.setVisibility(View.GONE);
                    }else if (tab.getPosition()==3){
                        titleMenu.setText(R.string.pesan);
                        txtkoneksi.setVisibility(View.GONE);
                        place1.setVisibility(View.GONE);
                        place2.setVisibility(View.GONE);
                    }else if (tab.getPosition()==4){
                        titleMenu.setText(R.string.profile);
                        txtkoneksi.setVisibility(View.GONE);
                        place1.setVisibility(View.GONE);
                        place2.setVisibility(View.GONE);
                    }
                    else {
                        txtkoneksi.setVisibility(View.GONE);
                        place1.setVisibility(View.GONE);
                        place2.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            if (getTabIndex()!=Interfaces.MENU_PANDUAN) {
                tabLayout.getTabAt(getTabIndex()).select();
                setTabIndex(Interfaces.MENU_PANDUAN);
            }else {
                tabLayout.getTabAt(Interfaces.MENU_PANDUAN).select();
            }


        }else {

            final TextView txtkoneksi= (TextView) findViewById(R.id.txtkoneksi);
            if (!Utilities.cek_status(getApplicationContext())) {
                txtkoneksi.setVisibility(View.VISIBLE);
            } else {
                txtkoneksi.setVisibility(View.GONE);
            }
        }

        Thread th = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10*1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTime();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        th.start();


//        weather.placeIdTask asyncTask =new weather.placeIdTask(new weather.AsyncResponse() {
//            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
//
////                cityField.setText(weather_city);
////                updatedField.setText(weather_updatedOn);
//                jak_cuaca.setText(weather_description);
//                jak_degree.setText(weather_temperature+ " \u2103");
////                humidity_field.setText("Humidity: "+weather_humidity);
////                pressure_field.setText("Pressure: "+weather_pressure);
////                weatherIcon.setText(Html.fromHtml(weather_iconText));
//
//            }
//        });
//        asyncTask.execute("-6.2147", "106.8451"); //  asyncTask.execute("Latitude", "Longitude")
////
//        weather.placeIdTask asyncTask2 =new weather.placeIdTask(new weather.AsyncResponse() {
//            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
//
////                cityField.setText(weather_city);
////                updatedField.setText(weather_updatedOn);
//                mek_cuaca.setText(weather_description);
//                mek_degree.setText(weather_temperature+ " \u2103" );
////                humidity_field.setText("Humidity: "+weather_humidity);
////                pressure_field.setText("Pressure: "+weather_pressure);
////                weatherIcon.setText(Html.fromHtml(weather_iconText));
//
//            }
//        });
//        asyncTask2.execute("21.4267", "39.8261");

    }
    private void SetContentView(){
        mActivity = this;
        setContentView(GetTemplates.GetPanduanTemplates(mActivity));
        getpref = Utilities.getPref("id_pref",mActivity)!=null? Utilities.getPref("id_pref",mActivity):"";
        GetTemplates.GetStatusBar(mActivity);
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "fonts/helvetica.ttf", true);
    }

    private void sendScreenImageName(String name) {
        // [START screen_view_hit]
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }

    private void showWeather() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(AppConfig.TAG_JSON_ARRAY);
            for (int i = 0; i < result.length(); i++) {
//                ArrayList<items> list = new ArrayList<>();
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(AppConfig.KEY_ID);
                String name = jo.getString(AppConfig.KEY_NAME);
                String desc = jo.getString(AppConfig.KEY_DESC);
                String temp = jo.getString(AppConfig.KEY_TEMP);

                if(name.equals("Jakarta")){
                 jak_cuaca.setText(desc);
                 jak_degree.setText(temp);
                }

                if(name.equals("Mekkah")){
                    mek_cuaca.setText(desc);
                    mek_degree.setText(temp);
                }
            }

        } catch (JSONException e) {
//            e.printStackTrace();
        }
    }

    private void getWeather(){
        // TODO: Weather
        jak_degree = (TextView) findViewById(R.id.jak_degree);
        jak_cuaca = (TextView) findViewById(R.id.jak_cuaca);
        mek_degree = (TextView) findViewById(R.id.mek_degree);
        mek_cuaca = (TextView) findViewById(R.id.mek_cuaca);

        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                showWeather();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(AppConfig.URL_WEATHER);
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
        Intent intent = new Intent(panduan.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean hasPermissions(@NonNull String... permissions) {
        for (String permission : permissions)
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(permission))
                return false;
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (getpref.equals(Interfaces.TEMPLATE_1)) {
            adapter.addFragment(new PanduanFragment(), "");
            adapter.addFragment(new TitipanDoaFragment(), "");
            adapter.addFragment(new NavigasiFragment(), "");
            adapter.addFragment(new InboxFragment(), "");
            adapter.addFragment(new ProfilFragment(), "");
        }else {
            adapter.addFragment(new OneFragment(), getResources().getString(R.string.sebelum_umrah));
            adapter.addFragment(new TwoFragment(), getResources().getString(R.string.saat_umrah));
            adapter.addFragment(new ThreeFragment(), getResources().getString(R.string.sesudah_umrah));
        }
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.d(TAG, mFragmentTitleList.get(position).toString());
            return mFragmentTitleList.get(position);
        }
    }

    private void updateTime(){
        // TODO: Time
        jak_date = (TextView) findViewById(R.id.jak_date);
        jak_time = (TextView) findViewById(R.id.jak_time);
        mek_date = (TextView) findViewById(R.id.mek_date);
        mek_time = (TextView) findViewById(R.id.mek_time);

        Cursor mCou= database.rawQuery("select kode from language where id=1", null);
        mCou.moveToFirst();
        final String time_id;
        String kode= mCou.getString(0);
        if(kode.equals("id")){
            time_id= "id";
        }else{
            time_id= "en";
        }

        Calendar cal_jak = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
        Date currentLocalTimejak = cal_jak.getTime();
        DateFormat date_jak = new SimpleDateFormat("EEEE, d MMMM yyyy",new Locale(time_id));
        date_jak.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        DateFormat time_jak = new SimpleDateFormat("HH:mm");
        date_jak.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String j_date = date_jak.format(currentLocalTimejak);
        String j_time = time_jak.format(currentLocalTimejak);

        jak_date.setText("" + j_date);
        jak_time.setText("" + j_time);

        Calendar cal_mek = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"));
        Date currentLocalTimemak = cal_mek.getTime();
        DateFormat date_mak = new SimpleDateFormat("EEEE, d MMMM yyyy",new Locale(time_id));
        date_mak.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        DateFormat time_mak = new SimpleDateFormat("HH:mm");
        time_mak.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        String m_date = date_mak.format(currentLocalTimemak);
        String m_time = time_mak.format(currentLocalTimemak);

        mek_date.setText("" + m_date);
        mek_time.setText("" + m_time);

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
//            e.printStackTrace();
        }

        CustomListPanduan3 adapter = new CustomListPanduan3(this, list,
                R.layout.list_panduan, new String[] { AppConfig.KEY_ID,AppConfig.KEY_NAME },
                new int[] { R.id.txtNO,R.id.txtNAME });
        listView.setAdapter(adapter);
        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
    }


    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(panduan.this,"Fetching Data","Wait...",false,false);
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
                String s = rh.sendGetRequest(AppConfig.URL_PANDUAN);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void startDownload(String id) {
        String url = URL_HOME+"/uploads/doa/"+id+".mp3";
        new DownloadFileAsync().execute(url);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading file..");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {
        final String id = txtid.getText().toString().trim();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {

                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream("/sdcard/android/data/salam.gohajj.custom/"+id+".mp3");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                Intent i = new Intent(getApplicationContext(), panduan.class);
                finish();
                startActivity(i);
            } catch (Exception e) {}
            return null;

        }
        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
        }
    }

//    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        View vi=view;
        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
        final String strID = map.get(AppConfig.TAG_ID).toString();
        final TextView txtaudio = (TextView)view.findViewById(R.id.txtAudio);
        final String audio = txtaudio.getText().toString().trim();
        if(audio.equals("FALSE")){
            txtid.setText(strID);
            startDownload(strID);
        }else{
            Intent intent = new Intent(this, ViewPanduan.class);
            intent.putExtra(AppConfig.EMP_ID,strID);
            startActivity(intent);
        }
//        final TextView play = (TextView)vi.findViewById(R.id.txtPlay);
//        final TextView download = (TextView)vi.findViewById(R.id.txtDownload);
//
//        final MediaPlayer mp = new MediaPlayer();
//        try {
//            mp.setDataSource("/sdcard/android/data/salam.gohajj.custom/" + strID + ".mp3");
//            mp.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        download.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(getApplicationContext(),"Download "+strID, Toast.LENGTH_LONG).show();
//                    txtid.setText(strID);
//                    startDownload(strID);
//                }
//            });
//
//        play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mp.isPlaying()){
//                    mp.pause();
//                    play.setText("Play");
//                } else {
//                    mp.start();
//                    play.setText("Pause");
//                }
//            }
//
//        });

    }

    public void ChooseTemplate(){
        place1 = (LinearLayout)findViewById(R.id.lin_place1);
        place2 = (LinearLayout)findViewById(R.id.lin_place2);
        gohajjMenu = (LinearLayout)findViewById(R.id.menufooter);
        floatingMenu=(FloatingActionMenu)findViewById(R.id.fabmenu);
        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        Utilities.ShowLog("pref",getpref);
        if (getpref.equals(Interfaces.TEMPLATE_1)){
            headerButton();
            floatingMenu();
            updateTime();
            gohajjMenu();
            getWeather();
            floatingMenu.setVisibility(View.GONE);
        }else if(getpref.equals(Interfaces.TEMPLATE_2)){
            HamFloatingMenu();
            updateTime();
            getWeather();
            headerButton();
        }else if(getpref.equals(Interfaces.TEMPLATE_3)){
            floatingMenu();
            updateTime();
            getWeather();
            headerButton();
        }else {
            gohajjMenu();
            updateTime();
            getWeather();
            headerButton();
        }

    }

    public void headerButton(){
        //HEADER
        TextView txt_emergency = (TextView) findViewById(R.id.txt_emergency);
        TextView txt_thowaf = (TextView) findViewById(R.id.txt_thowaf);
        TextView txt_sai = (TextView) findViewById(R.id.txt_sai);

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
    }
    public void gohajjMenu(){
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
        if(getpref.equals(Interfaces.TEMPLATE_1)) {
            gohajjMenu.setVisibility(View.VISIBLE);
            menu_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tabLayout.getTabAt(4).select();
                }
            });
            menu_panduan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tabLayout.getTabAt(0).select();
                }
            });
            menu_doa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tabLayout.getTabAt(1).select();
                }
            });
            menu_navigasi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tabLayout.getTabAt(2).select();
                }
            });
            menu_inbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tabLayout.getTabAt(3).select();
                }
            });

        }else {
        floatingMenu.setVisibility(View.GONE);
        gohajjMenu.setVisibility(View.VISIBLE);

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
        }
    }

    public void HamFloatingMenu(){
        gohajjMenu.setVisibility(View.GONE);
        floatingMenu.setVisibility(View.GONE);
        bmb.setVisibility(View.VISIBLE);
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_1);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_1);

        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            HamButton.Builder builder = new HamButton.Builder()
                    .normalImageRes(R.drawable.panduan).imageRect(new Rect(0, 0, Util.dp2px(60), Util.dp2px(60))).highlightedTextRes(R.string.panduan);
            bmb.addBuilder(builder);
        }
//        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
//            HamButton.Builder builder = new HamButton.Builder()
//                    .listener(new OnBMClickListener() {
//                        @Override
//                        public void onBoomButtonClick(int index) {
//                            // When the boom-button corresponding this builder is clicked.
//                            Toast.makeText(panduan.this, "Clicked " + index, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//            bmb.addBuilder(builder);
//        }
    }

    public void floatingMenu() {
        gohajjMenu.setVisibility(View.GONE);
        floatingMenu.setVisibility(View.VISIBLE);
        // New FAB

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fabpanduan);
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fabdoa);
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fabnavigation);
        FloatingActionButton fab4 = (FloatingActionButton) findViewById(R.id.fabinbox);
        FloatingActionButton fab5 = (FloatingActionButton) findViewById(R.id.fabprofile);
        FloatingActionButton thawaf = (FloatingActionButton) findViewById(R.id.fabThawaf);
        FloatingActionButton sai = (FloatingActionButton) findViewById(R.id.fabSai);
        if (getpref.equals(Interfaces.TEMPLATE_1)) {
            thawaf.setVisibility(View.VISIBLE);
            sai.setVisibility(View.VISIBLE);
            fab1.setVisibility(View.GONE);
            fab2.setVisibility(View.GONE);
            fab3.setVisibility(View.GONE);
            fab4.setVisibility(View.GONE);
            fab5.setVisibility(View.GONE);
        } else {
            thawaf.setVisibility(View.GONE);
            sai.setVisibility(View.GONE);
            fab1.setVisibility(View.VISIBLE);
            fab2.setVisibility(View.VISIBLE);
            fab3.setVisibility(View.VISIBLE);
            fab4.setVisibility(View.VISIBLE);
            fab5.setVisibility(View.VISIBLE);
        }

        thawaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), thawaf.class);
                startActivity(i);
            }
        });
        sai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), sai.class);
                startActivity(i);
            }
        });
    }

    protected void CountInbox(){
        Cursor c= database.rawQuery("select * from badge where id=1 ", null);
        c.moveToFirst();
        int jumlah=c.getInt(1);
        if (getpref.equals(Interfaces.TEMPLATE_1)){
            target = findViewById(R.id.img_inbox_tab);
        }else {
            target = findViewById(R.id.img_inbox);
        }
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

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
//            if (ContextCompat.checkSelfPermission(panduan.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, READ_EXST);
//            }else{
//                int REQUEST_OVERLAY_PERMISSION=1;
//                if (Build.VERSION.SDK_INT >= 19 && MIUIUtils.isMIUI() && !MIUIUtils.isFloatWindowOptionAllowed(getApplicationContext())) {
//                    Log.i(TAG, "MIUI DEVICE: Screen Overlay Not allowed");
//                    startActivityForResult(MIUIUtils.toFloatWindowPermission(getApplicationContext(), getPackageName()), REQUEST_OVERLAY_PERMISSION);
//                } else if (Build.VERSION.SDK_INT >= 23 && MIUIUtils.isMIUI() && !Settings.canDrawOverlays(getApplicationContext())) {
//                    Log.i(TAG, "SDK_INT > 23: Screen Overlay Not allowed");
//                    startActivityForResult(new Intent(
//                                    "android.settings.action.MANAGE_OVERLAY_PERMISSION",
//                                    Uri.parse("package:" +getPackageName()))
//                            , REQUEST_OVERLAY_PERMISSION
//                    );
//                } else {
//                    Log.i(TAG, "SKK_INT < 19 or Have overlay permission");
//
//                }
//            }
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//            if (ContextCompat.checkSelfPermission(panduan.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, READ_EXST);
//            }else{
//                int REQUEST_OVERLAY_PERMISSION=1;
//                if (Build.VERSION.SDK_INT >= 19 && MIUIUtils.isMIUI() && !MIUIUtils.isFloatWindowOptionAllowed(getApplicationContext())) {
//                    Log.i(TAG, "MIUI DEVICE: Screen Overlay Not allowed");
//                    startActivityForResult(MIUIUtils.toFloatWindowPermission(getApplicationContext(), getPackageName()), REQUEST_OVERLAY_PERMISSION);
//                } else if (Build.VERSION.SDK_INT >= 23 && MIUIUtils.isMIUI() && !Settings.canDrawOverlays(getApplicationContext())) {
//                    Log.i(TAG, "SDK_INT > 23: Screen Overlay Not allowed");
//                    startActivityForResult(new Intent(
//                                    "android.settings.action.MANAGE_OVERLAY_PERMISSION",
//                                    Uri.parse("package:" +getPackageName()))
//                            , REQUEST_OVERLAY_PERMISSION
//                    );
//                } else {
//                    Log.i(TAG, "SKK_INT < 19 or Have overlay permission");
//
//                }
//            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager()!=null) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    finishAffinity();
                    //GenericPopup.Init(mActivity,getResources().getString(R.string.keluar),getResources().getString(R.string.konfirmasi_keluar),GenericPopup.CONFIRM_BUTTON);
                    //GenericPopup.Show();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Utilities.ShowToast(mActivity,getResources().getString(R.string.double_click_exit));
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            }
        }

    }

}
