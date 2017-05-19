package com.garudatekno.jemaah.menu;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.CustomListPanduan3;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.activity.MapsActivity;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.app.AppController;
import com.garudatekno.jemaah.gcm.weather;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.readystatesoftware.viewbadger.BadgeView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
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

import static com.garudatekno.jemaah.app.AppConfig.URL_HOME;
import static java.lang.Boolean.FALSE;
import static org.apache.http.params.CoreProtocolPNames.USER_AGENT;


public class panduan extends AppCompatActivity implements ListView.OnItemClickListener {

    private static final String TAG = "MyHOME";

    private ListView listView;
    private TextView txtid,txtkoneksi,jak_degree,jak_cuaca,jak_date,jak_time,mek_degree,mek_cuaca,mek_date,mek_time;
    private String JSON_STRING,uid;
    private SQLiteHandler db;
    private SessionManager session;

    MediaPlayer mMediaPlayer ;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static final String[] requiredPermissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            /* ETC.. */
    };
    private Tracker mTracker;
    View target ;
    BadgeView badge ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panduan);
        //tracker
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        sendScreenImageName("Home");
        //badge
        target = findViewById(R.id.img_inbox);
        badge = new BadgeView(this, target);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            CountInbox();
        }

        //end
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);
        txtkoneksi= (TextView) findViewById(R.id.txtkoneksi);
        Thread th = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!cek_status(getApplicationContext()))
                                {
                                    txtkoneksi.setVisibility(View.VISIBLE);
                                }else{
                                    txtkoneksi.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        th.start();

        //useri mage
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/com.garudatekno.jemaah/images/profile.png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }

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


        ImageView img = (ImageView) findViewById(R.id.img_panduan);
        img.setBackgroundResource(R.drawable.circle_green_active);
        img.setPadding(22,22,22,22);
        img.setImageDrawable(getResources().getDrawable(R.drawable.panduan_active));

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

        //jakarta
        jak_degree=(TextView) findViewById(R.id.jak_degree);
        jak_cuaca=(TextView) findViewById(R.id.jak_cuaca);
        jak_date=(TextView) findViewById(R.id.jak_date);
        jak_time=(TextView) findViewById(R.id.jak_time);

        //mekkah
        mek_degree=(TextView) findViewById(R.id.mek_degree);
        mek_cuaca=(TextView) findViewById(R.id.mek_cuaca);
        mek_date=(TextView) findViewById(R.id.mek_date);
        mek_time=(TextView) findViewById(R.id.mek_time);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
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

        t.start();
    }

    private void sendScreenImageName(String name) {
        // [START screen_view_hit]
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
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
        adapter.addFragment(new OneFragment(), "Sebelum Umrah");
        adapter.addFragment(new TwoFragment(), "Saat Umrah");
        adapter.addFragment(new ThreeFragment(), "Setelah Umrah");
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
        Calendar cal_jak = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
        Date currentLocalTimejak = cal_jak.getTime();
        DateFormat date_jak = new SimpleDateFormat("EEEE, d MMMM yyyy",new Locale("id"));
        date_jak.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        DateFormat time_jak = new SimpleDateFormat("HH:mm");
        date_jak.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String j_date = date_jak.format(currentLocalTimejak);
        String j_time = time_jak.format(currentLocalTimejak);

        jak_date.setText("" + j_date);
        jak_time.setText("" + j_time);

        Calendar cal_mek = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"));
        Date currentLocalTimemak = cal_mek.getTime();
        DateFormat date_mak = new SimpleDateFormat("EEEE, d MMMM yyyy",new Locale("id"));
        date_mak.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        DateFormat time_mak = new SimpleDateFormat("HH:mm");
        time_mak.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        String m_date = date_mak.format(currentLocalTimemak);
        String m_time = time_mak.format(currentLocalTimemak);

        mek_date.setText("" + m_date);
        mek_time.setText("" + m_time);

        weather.placeIdTask asyncTask =new weather.placeIdTask(new weather.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {

//                cityField.setText(weather_city);
//                updatedField.setText(weather_updatedOn);
                jak_cuaca.setText(weather_description);
                jak_degree.setText(weather_temperature+ " \u2103");
//                humidity_field.setText("Humidity: "+weather_humidity);
//                pressure_field.setText("Pressure: "+weather_pressure);
//                weatherIcon.setText(Html.fromHtml(weather_iconText));

            }
        });
        asyncTask.execute("-6.2147", "106.8451"); //  asyncTask.execute("Latitude", "Longitude")

        weather.placeIdTask asyncTask2 =new weather.placeIdTask(new weather.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {

//                cityField.setText(weather_city);
//                updatedField.setText(weather_updatedOn);
                mek_cuaca.setText(weather_description);
                mek_degree.setText(weather_temperature+ " \u2103" );
//                humidity_field.setText("Humidity: "+weather_humidity);
//                pressure_field.setText("Pressure: "+weather_pressure);
//                weatherIcon.setText(Html.fromHtml(weather_iconText));

            }
        });
        asyncTask2.execute("21.4267", "39.8261");

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
                OutputStream output = new FileOutputStream("/sdcard/android/data/com.garudatekno.jemaah/"+id+".mp3");

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
//        Toast.makeText(getApplicationContext(),audio, Toast.LENGTH_LONG).show();
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
//            mp.setDataSource("/sdcard/android/data/com.garudatekno.jemaah/" + strID + ".mp3");
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

    protected void CountInbox(){
        class GetJSON extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                String hsl = s.trim();
                Integer a = Integer.parseInt(hsl);
                if(a > 0){
                    badge.setText(hsl);
                    badge.show();
                    ShortcutBadger.applyCount(getApplicationContext(), a);
                }else {
                    ShortcutBadger.removeCount(getApplicationContext());
                    badge.hide();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(AppConfig.URL_COUNT_INBOX,uid);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }
}
