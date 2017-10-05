package salam.gohajj.custom.menu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;

import salam.gohajj.custom.GetTemplates;
import salam.gohajj.custom.Interfaces;
import salam.gohajj.custom.R;
import salam.gohajj.custom.Utilities;
import salam.gohajj.custom.activity.LoginActivity;
import salam.gohajj.custom.activity.RequestHandler;
import salam.gohajj.custom.app.AppConfig;
import salam.gohajj.custom.helper.SQLiteHandler;
import salam.gohajj.custom.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;

public class ViewPanduan extends AppCompatActivity implements View.OnClickListener {


    private TextView txtData,txtid,info, state;
    private Button buttonStart,buttonStop;

    private String id,msg,uid;
    private SeekBar timeLine;
    LinearLayout timeFrame;
    TextView timePos, timeDur;
    final static int RQS_OPEN_AUDIO_MP3 = 1;

    MediaPlayer mediaPlayer;
    private SQLiteHandler db;
    private SessionManager session;
    String srcPath = null;
    enum MP_State {
        Idle, Initialized, Prepared, Started, Paused,
        Stopped, PlaybackCompleted, End, Error, Preparing}

    MP_State mediaPlayerState;

    int numMessages = 0;
    MediaPlayer mMediaPlayer ;
    private MediaPlayer mp;
    private Activity mActivity;
    private String getpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.view_panduan);
        mActivity = this;
        GetTemplates.GetStatusBar(mActivity);
        setContentView(GetTemplates.GetViewPanduan(mActivity));
        getpref = Utilities.getPref("id_pref",mActivity)!=null? Utilities.getPref("id_pref",mActivity):"";
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);
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
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ViewPanduan.this, img_setting);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == R.id.logout) {
                            logoutUser();
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        Intent intent = getIntent();
        id = intent.getStringExtra(AppConfig.EMP_ID);
        msg = intent.getStringExtra(AppConfig.KEY_MESSAGE);
        state = (TextView)findViewById(R.id.state);

        txtData = (TextView) findViewById(R.id.data);
        txtid= (TextView) findViewById(R.id.txtid);
        buttonStart = (Button) findViewById(R.id.buttonPLAY);
        buttonStop = (Button) findViewById(R.id.buttonSTOP);
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        getData();

        timeLine = (SeekBar)findViewById(R.id.seekbartimeline);
        timeFrame = (LinearLayout)findViewById(R.id.timeframe);
        timePos = (TextView)findViewById(R.id.pos);
        timeDur = (TextView)findViewById(R.id.dur);

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        //useri mage
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/salam.gohajj.custom/images/profile.png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }
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

//        mp = new MediaPlayer();
//        try {
//            mp.setDataSource("/sdcard/android/data/salam.gohajj.custom/"+id+".mp3");
//            mp.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        srcPath="/sdcard/android/data/salam.gohajj.custom/panduan"+msg+"/"+id+".mp3";
        cmdReset();
        cmdSetDataSource(srcPath);

    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(ViewPanduan.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
                timePos.setText(String.valueOf((float)mediaPosition/1000) + "s");
                timeDur.setText(String.valueOf((float)mediaDuration/1000) + "s");
            }else{
                timeLine.setVisibility(View.INVISIBLE);
                timeFrame.setVisibility(View.INVISIBLE);
                buttonStop.setVisibility(View.GONE);
                buttonStart.setVisibility(View.VISIBLE);
            }
        }
    }

    OnErrorListener mediaPlayerOnErrorListener
            = new OnErrorListener(){

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            // TODO Auto-generated method stub

            mediaPlayerState = MP_State.Error;
            showMediaPlayerState();

            return false;
        }};


    private void cmdReset(){
        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnErrorListener(mediaPlayerOnErrorListener);
        }
        mediaPlayer.reset();
        mediaPlayerState = MP_State.Idle;
        showMediaPlayerState();
    }

    private void cmdSetDataSource(String path){
        if(mediaPlayerState == MP_State.Idle){
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayerState = MP_State.Initialized;
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

        if(mediaPlayerState == MP_State.Initialized
                ||mediaPlayerState == MP_State.Stopped){
            try {
                mediaPlayer.prepare();
                mediaPlayerState = MP_State.Prepared;
            } catch (IllegalStateException e) {
                Toast.makeText(ViewPanduan.this,
                        e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
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
        if(mediaPlayerState == MP_State.Prepared
                ||mediaPlayerState == MP_State.Started
                ||mediaPlayerState == MP_State.Paused
                ||mediaPlayerState == MP_State.PlaybackCompleted){
            mediaPlayer.start();
            mediaPlayerState = MP_State.Started;
        }else{
//            Toast.makeText(ViewPanduan.this,
//                    "Invalid State@cmdStart() - skip",
//                    Toast.LENGTH_LONG).show();
        }

        showMediaPlayerState();
    }

    private void cmdPause(){
        if(mediaPlayerState == MP_State.Started
                ||mediaPlayerState == MP_State.Paused){
            mediaPlayer.pause();
            mediaPlayerState = MP_State.Paused;
        }else{
//            Toast.makeText(ViewPanduan.this,
//                    "Invalid State@cmdPause() - skip",
//                    Toast.LENGTH_LONG).show();
        }
        showMediaPlayerState();
    }

    private void cmdStop(){

        if(mediaPlayerState == MP_State.Prepared
                ||mediaPlayerState == MP_State.Started
                ||mediaPlayerState == MP_State.Stopped
                ||mediaPlayerState == MP_State.Paused
                ||mediaPlayerState == MP_State.PlaybackCompleted){
            mediaPlayer.stop();
            mediaPlayerState = MP_State.Stopped;
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

    private void getData(){
        class GetData extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewPanduan.this,"","Wait...",false,false);
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
                Log.d("GETDOA", AppConfig.URL_GET_PANDUAN+ id);
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
            String data = c.getString(AppConfig.KEY_DESCRIPTION)!=null?c.getString(AppConfig.KEY_DESCRIPTION):"";
            txtid.setText(id);
            txtData.setText(data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        if(v == buttonStart){
//            PlayAudio();
            if(srcPath == null){
                Toast.makeText(ViewPanduan.this,
                        "No file selected",
                        Toast.LENGTH_LONG).show();
            }else{
                cmdPrepare();
                cmdStart();
                buttonStart.setVisibility(View.GONE);
                buttonStop.setVisibility(View.VISIBLE);
            }
        }

        if(v == buttonStop){
            cmdPause();
            buttonStop.setVisibility(View.GONE);
            buttonStart.setVisibility(View.VISIBLE);
        }

    }
}
