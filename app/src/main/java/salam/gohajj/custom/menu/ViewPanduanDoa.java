package salam.gohajj.custom.menu;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import salam.gohajj.custom.GetTemplates;
import salam.gohajj.custom.Interfaces;
import salam.gohajj.custom.R;
import salam.gohajj.custom.Utilities;
import salam.gohajj.custom.activity.CustomListPanduan1;
import salam.gohajj.custom.activity.LoginActivity;
import salam.gohajj.custom.activity.RequestHandler;
import salam.gohajj.custom.app.AppConfig;
import salam.gohajj.custom.helper.ArabicUtilities;
import salam.gohajj.custom.helper.SQLiteHandler;
import salam.gohajj.custom.helper.SessionManager;
import com.readystatesoftware.viewbadger.BadgeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;
import me.leolin.shortcutbadger.ShortcutBadger;

public class ViewPanduanDoa extends AppCompatActivity implements View.OnClickListener,ListView.OnItemClickListener {
    private Activity mActivity;
    private TextView txtname,txtid,info, state,txtarab;
    WebView txtdesc,txtTerjemahan;
    private Button buttonStart,buttonSave,btnTerjemahan;
    private ListView listView;
    private String id,file,uid;
    private SeekBar timeLine;
    LinearLayout timeFrame;
    ProgressDialog pd;
    TextView timePos, timeDur;
    private String JSON_STRING; MediaPlayer mediaPlayer;
    String srcPath = null;
    enum MP_State {
        Idle, Initialized, Prepared, Started, Paused,
        Stopped, PlaybackCompleted, End, Error, Preparing}

    ViewPanduanDoa.MP_State mediaPlayerState;
    private String getpref;
    private SQLiteHandler db;
    private SessionManager session;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    View target ;
    BadgeView badge ;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.view_panduan_doa);
        mActivity = this;
        getpref = Utilities.getPref("id_pref",mActivity)!=null? Utilities.getPref("id_pref",mActivity):"";
        setContentView(GetTemplates.GetViewPanduanDoa(mActivity));
        GetTemplates.GetStatusBar(mActivity);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

        session = new SessionManager(getApplicationContext());
        database = openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        CountInbox();
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
        LinearLayout footerMenu = (LinearLayout)findViewById(R.id.menufooter);
        if (getpref.equals(Interfaces.TEMPLATE_1)){
            footerMenu.setVisibility(View.GONE);
        }
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
//        if (!session.isLoggedIn()) {
//            logoutUser();
//        }

        Intent intent = getIntent();
        id = intent.getStringExtra(AppConfig.EMP_ID);
        file = intent.getStringExtra(AppConfig.KEY_FILE);

        txtdesc = (WebView) findViewById(R.id.txtDesc);
        txtTerjemahan = (WebView) findViewById(R.id.txtTerjemahan);
        btnTerjemahan = (Button) findViewById(R.id.btnTerjemahan);
        txtid= (TextView) findViewById(R.id.txtid);
        txtname= (TextView) findViewById(R.id.txtName);
        txtarab= (TextView) findViewById(R.id.txtArab);

        timeLine = (SeekBar)findViewById(R.id.seekbartimeline);
        timeFrame = (LinearLayout)findViewById(R.id.timeframe);
        timePos = (TextView)findViewById(R.id.pos);
        timeDur = (TextView)findViewById(R.id.dur);
        state = (TextView)findViewById(R.id.state);

        buttonStart = (Button) findViewById(R.id.btnPlay);
        buttonSave = (Button) findViewById(R.id.btnSimpan);
        File cek = new File("/sdcard/android/data/salam.gohajj.custom/doa/"+file);
        if (!cek.exists()) {
            srcPath=AppConfig.URL_HOME+"/uploads/panduan/doa/"+file;
            buttonSave.setVisibility(View.VISIBLE);
        }else{
            buttonSave.setVisibility(View.GONE);
            srcPath="/sdcard/android/data/salam.gohajj.custom/doa/"+file;
        }

        btnTerjemahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String val=btnTerjemahan.getText().toString();
                if(val.equals(getResources().getString(R.string.terjemahan))){
                        txtTerjemahan.setVisibility(View.VISIBLE);
                        txtdesc.setVisibility(View.GONE);
                        btnTerjemahan.setText(getResources().getString(R.string.teks));
                }else{
                    txtTerjemahan.setVisibility(View.GONE);
                    txtdesc.setVisibility(View.VISIBLE);
                    btnTerjemahan.setText(getResources().getString(R.string.terjemahan));

                }
            }
        });

        ScheduledExecutorService myScheduledExecutorService = Executors.newScheduledThreadPool(1);

        myScheduledExecutorService.scheduleWithFixedDelay(
                new Runnable(){
                    @Override
                    public void run() {
                        monitorHandler.sendMessage(monitorHandler.obtainMessage());
                    }},
                200, //initialDelay
                200, //delay
                TimeUnit.MILLISECONDS);
        cmdReset();
        cmdSetDataSource(srcPath);

        buttonStart.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        getData();

        //useri mage
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/salam.gohajj.custom/images/"+uid+".png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }

    }

    private void logoutUser() {
        if (session.isLoggedIn()) {
            session.setLogin(false);
            db.deleteUsers();
        }

        // Launching the login activity
        Intent intent = new Intent(ViewPanduanDoa.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void getData(){
        class GetData extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewPanduanDoa.this,"",getResources().getString(R.string.mohon_tunggu)+"...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showData(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(AppConfig.URL_GET_PANDUAN,id);
                return s;
            }
        }
        GetData ge = new GetData();
        ge.execute();
    }

    private void showData(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(AppConfig.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            String id = c.getString(AppConfig.KEY_ID)!=null?c.getString(AppConfig.KEY_ID):"";
            String name = c.getString(AppConfig.KEY_NAME)!=null?c.getString(AppConfig.KEY_NAME):"";
            String jenis = c.getString(AppConfig.KEY_JENIS)!=null?c.getString(AppConfig.KEY_JENIS):"";
            String ctg = c.getString(AppConfig.KEY_CATEGORY)!=null?c.getString(AppConfig.KEY_CATEGORY):"";
            String desc = c.getString(AppConfig.KEY_DESCRIPTION)!=null?c.getString(AppConfig.KEY_DESCRIPTION):"";
            String arab = c.getString(AppConfig.KEY_ARAB)!=null? c.getString(AppConfig.KEY_ARAB):"";
            String terjemahan = c.getString(AppConfig.KEY_TERJEMAHAN)!=null?c.getString(AppConfig.KEY_TERJEMAHAN):"";

            txtid.setText(id);
            txtname.setText(name);
            txtarab.setText(ArabicUtilities.reshape(arab));
            txtdesc.loadData(desc, "text/html; charset=utf-8", "utf-8");
            txtTerjemahan.loadData(terjemahan, "text/html; charset=utf-8", "utf-8");

            getJSON(jenis,ctg,id);

        } catch (JSONException e) {
//            e.printStackTrace();
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

    @Override
    public void onClick(View v) {

        if(v == buttonStart){
            if(buttonStart.getText().toString().trim().equals(getResources().getString(R.string.mainkan_audio))){
                if(srcPath == null){
                    Toast.makeText(ViewPanduanDoa.this,
                            "No file selected",
                            Toast.LENGTH_LONG).show();
                }else{
                    if (!cek_status(getApplicationContext()))
                    {
                        Toast.makeText(ViewPanduanDoa.this,
                                getResources().getString(R.string.cek_koneksi),
                                Toast.LENGTH_SHORT).show();
                    }else {
                        pd = new ProgressDialog(ViewPanduanDoa.this);
                        pd.setMessage(getResources().getString(R.string.mempersiapkan_audio)+"...");
                        pd.show();
                        cmdPrepare();
                        cmdStart();
                        buttonStart.setText("Stop");
                    }
                }
            }else{
                cmdStop();
                buttonStart.setText(getResources().getString(R.string.mainkan_audio));
            }
        }

        if(v == buttonSave){
            File folder = new File("/sdcard/android/data/salam.gohajj.custom/doa");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            startDownload();
        }

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();
        else
            return;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();
        else
            return;
    }

    Handler monitorHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            mediaPlayerMonitor();
        }
    };

    private void mediaPlayerMonitor(){
        if (mediaPlayer == null){
            timeLine.setVisibility(View.INVISIBLE);
            timeFrame.setVisibility(View.INVISIBLE);
        }else{
            if(mediaPlayer.isPlaying()){
                timeLine.setVisibility(View.VISIBLE);
                timeFrame.setVisibility(View.VISIBLE);

                int mediaDuration = mediaPlayer.getDuration();
                int mediaPosition = mediaPlayer.getCurrentPosition();
                timeLine.setMax(mediaDuration);
                timeLine.setProgress(mediaPosition);
                int minutes = mediaPosition/1000 / 60;
                int seconds = mediaPosition/1000 % 60;
                int minutes2 = mediaDuration/1000 / 60;
                int seconds2 = mediaDuration/1000 % 60;
                timePos.setText(String.format("%02d:%02d", minutes, seconds));
                timeDur.setText(String.format("%02d:%02d", minutes2, seconds2));
                pd.dismiss();
//                timePos.setText(String.valueOf( minutes+":"+seconds));
//                timeDur.setText(String.valueOf( minutes2+":"+seconds2));

            }else{
                timeLine.setVisibility(View.GONE);
                timeFrame.setVisibility(View.INVISIBLE);
                buttonStart.setText(getResources().getString(R.string.mainkan_audio));
            }
        }
    }

    OnErrorListener mediaPlayerOnErrorListener
            = new OnErrorListener(){

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            // TODO Auto-generated method stub

            mediaPlayerState = ViewPanduanDoa.MP_State.Error;
            showMediaPlayerState();

            return false;
        }};


    private void cmdReset(){
        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnErrorListener(mediaPlayerOnErrorListener);
        }
        mediaPlayer.reset();
        mediaPlayerState = ViewPanduanDoa.MP_State.Idle;
        showMediaPlayerState();
    }

    private void cmdSetDataSource(String path){
        if(mediaPlayerState == ViewPanduanDoa.MP_State.Idle){
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayerState = ViewPanduanDoa.MP_State.Initialized;
            } catch (IllegalArgumentException e) {
//                Toast.makeText(ViewPanduan.this,
//                        e.toString(), Toast.LENGTH_LONG).show();
//                e.printStackTrace();
            } catch (IllegalStateException e) {
//                Toast.makeText(ViewPanduan.this,
//                        e.toString(), Toast.LENGTH_LONG).show();
//                e.printStackTrace();
            } catch (IOException e) {
//                Toast.makeText(ViewPanduan.this,
//                        e.toString(), Toast.LENGTH_LONG).show();
//                e.printStackTrace();
            }
        }else{
//            Toast.makeText(ViewPanduan.this,
//                    "Invalid State@cmdSetDataSource - skip",
//                    Toast.LENGTH_LONG).show();
        }

        showMediaPlayerState();
    }

    private void cmdPrepare(){

        if(mediaPlayerState == ViewPanduanDoa.MP_State.Initialized
                ||mediaPlayerState == ViewPanduanDoa.MP_State.Stopped){
            try {
                mediaPlayer.prepare();
                mediaPlayerState = ViewPanduanDoa.MP_State.Prepared;
            } catch (IllegalStateException e) {
                Toast.makeText(ViewPanduanDoa.this,
                        e.toString(), Toast.LENGTH_LONG).show();
//                e.printStackTrace();
            }
            catch (IOException e) {
//                Toast.makeText(ViewPanduan.this,
//                        e.toString(), Toast.LENGTH_LONG).show();
//                e.printStackTrace();
            }
        }else{
//            Toast.makeText(ViewPanduan.this,
//                    "Invalid State@cmdPrepare() - skip",
//                    Toast.LENGTH_LONG).show();
        }

        showMediaPlayerState();
    }

    private void cmdStart(){
        if(mediaPlayerState == ViewPanduanDoa.MP_State.Prepared
                ||mediaPlayerState == ViewPanduanDoa.MP_State.Started
                ||mediaPlayerState == ViewPanduanDoa.MP_State.Paused
                ||mediaPlayerState == ViewPanduanDoa.MP_State.PlaybackCompleted){
            mediaPlayer.start();
            mediaPlayerState = ViewPanduanDoa.MP_State.Started;
        }else{
//            Toast.makeText(ViewPanduan.this,
//                    "Invalid State@cmdStart() - skip",
//                    Toast.LENGTH_LONG).show();
        }

        showMediaPlayerState();
    }

    private void cmdPause(){
        if(mediaPlayerState == ViewPanduanDoa.MP_State.Started
                ||mediaPlayerState == ViewPanduanDoa.MP_State.Paused){
            mediaPlayer.pause();
            mediaPlayerState = ViewPanduanDoa.MP_State.Paused;
        }else{
//            Toast.makeText(ViewPanduan.this,
//                    "Invalid State@cmdPause() - skip",
//                    Toast.LENGTH_LONG).show();
        }
        showMediaPlayerState();
    }

    private void cmdStop(){

        if(mediaPlayerState == ViewPanduanDoa.MP_State.Prepared
                ||mediaPlayerState == ViewPanduanDoa.MP_State.Started
                ||mediaPlayerState == ViewPanduanDoa.MP_State.Stopped
                ||mediaPlayerState == ViewPanduanDoa.MP_State.Paused
                ||mediaPlayerState == ViewPanduanDoa.MP_State.PlaybackCompleted){
            mediaPlayer.stop();
            mediaPlayerState = ViewPanduanDoa.MP_State.Stopped;
        }else{
//            Toast.makeText(ViewPanduan.this,
//                    "Invalid State@cmdStop() - skip",
//                    Toast.LENGTH_LONG).show();
        }
        showMediaPlayerState();

    }

    private void showMediaPlayerState(){

        switch(mediaPlayerState){
            case Idle:
                state.setText("Idle");
                break;
            case Initialized:
                state.setText("Initialized");
                break;
            case Prepared:
                state.setText("Prepared");
                break;
            case Started:
                state.setText("Started");
                break;
            case Paused:
                state.setText("Paused");
                break;
            case Stopped:
                state.setText("Stopped");
                break;
            case PlaybackCompleted:
                state.setText("PlaybackCompleted");
                break;
            case End:
                state.setText("End");
                break;
            case Error:
                state.setText("Error");
                break;
            case Preparing:
                state.setText("Preparing");
                break;
            default:
                state.setText("Unknown!");
        }
    }


    private void showJson(){
        JSONObject jsonObject = null;
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(AppConfig.TAG_JSON_ARRAY);
            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(AppConfig.KEY_ID);
                String name = jo.getString(AppConfig.KEY_NAME);
                String jenis = jo.getString(AppConfig.KEY_JENIS);
                String file = jo.getString(AppConfig.KEY_FILE);
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_ID,id);
                data.put(AppConfig.KEY_NAME,name);
                data.put(AppConfig.KEY_JENIS,jenis);
                data.put(AppConfig.KEY_FILE,file);
                list.add(data);
            }

        } catch (JSONException e) {
//            e.printStackTrace();
        }

        CustomListPanduan1 adapter = new CustomListPanduan1(this, list,
                R.layout.list_panduan, new String[] { AppConfig.KEY_ID,AppConfig.KEY_NAME,AppConfig.KEY_JENIS,AppConfig.KEY_FILE },
                new int[] { R.id.txtNO,R.id.txtNAME,R.id.txtImg });
        listView.setAdapter(adapter);

        listView.setAdapter(adapter);
        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
    }

    private void getJSON(final String Jenis,final String Category,final String Id){
        class GetJSON extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loading = ProgressDialog.show(ViewPanduanDoa.this,"","",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                loading.dismiss();
                JSON_STRING = s;
                showJson();
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_CATEGORY, Category);
                data.put(AppConfig.KEY_JENIS, Jenis);
                data.put(AppConfig.KEY_ID, Id);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(AppConfig.URL_PANDUAN_JENIS, data);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
        final String strID = map.get(AppConfig.TAG_ID).toString();
        final String strFile = map.get(AppConfig.KEY_FILE).toString();
        Intent i = new Intent(getApplicationContext(), ViewPanduanDoa.class);
        i.putExtra(AppConfig.EMP_ID,strID);
        i.putExtra(AppConfig.KEY_FILE,strFile);
        finish();
        startActivity(i);
    }

    private void startDownload() {
        String url = srcPath;
        new ViewPanduanDoa.DownloadFileAsync().execute(url);
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
                OutputStream output = new FileOutputStream("/sdcard/android/data/salam.gohajj.custom/doa/"+file);

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
                Intent i = new Intent(getApplicationContext(), ViewPanduanDoa.class);
                i.putExtra(AppConfig.EMP_ID,id);
                i.putExtra(AppConfig.KEY_FILE,file);
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
