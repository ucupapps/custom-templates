package com.garudatekno.jemaah.menu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.MainActivity;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ViewPanduan extends AppCompatActivity implements View.OnClickListener {


    private TextView txtData,txtid,info, state;
    private Button buttonStart,buttonStop;

    private String id;
    private SeekBar timeLine;
    LinearLayout timeFrame;
    TextView timePos, timeDur;
    final static int RQS_OPEN_AUDIO_MP3 = 1;

    MediaPlayer mediaPlayer;
    String srcPath = null;
    enum MP_State {
        Idle, Initialized, Prepared, Started, Paused,
        Stopped, PlaybackCompleted, End, Error, Preparing}

    MP_State mediaPlayerState;

    int numMessages = 0;


    MediaPlayer mMediaPlayer ;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_panduan);

        // HEADER
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
        txt_panduan.setTextColor(getResources().getColor(R.color.colorTextActive));
        ImageView img_panduan=(ImageView) findViewById(R.id.img_panduan);
        img_panduan.setImageDrawable(getResources().getDrawable(R.drawable.panduan_active));
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
                Intent i = new Intent(getApplicationContext(), Doa.class);
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
        menu_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), inbox.class);
                startActivity(i);
            }
        });

        //FOOTER
        TextView txt_thowaf=(TextView) findViewById(R.id.txt_thowaf);
        TextView txt_sai=(TextView) findViewById(R.id.txt_sai);
        final TextView txt_go=(TextView) findViewById(R.id.txt_go);
        txt_thowaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
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
        txt_go.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ViewPanduan.this, txt_go);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == R.id.bus) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"BUS");
                            startActivity(i);
                        }
                        if(id == R.id.hotel) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"HOTEL");
                            startActivity(i);
                        }
                        if(id == R.id.pintu) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"NO PINTU MASJID");
                            startActivity(i);
                        }
                        if(id == R.id.meeting) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"MEETING POINT");
                            startActivity(i);
                        }
//                        if(id == R.id.poi) {
//                            Intent i = new Intent(getApplicationContext(), input.class);
//                            startActivity(i);
//                        }
                        if(id == R.id.pin) {
                            Intent i = new Intent(getApplicationContext(), marker.class);
                            startActivity(i);
                        }

                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        Intent intent = getIntent();
        id = intent.getStringExtra(AppConfig.EMP_ID);
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
//            mp.setDataSource("/sdcard/android/data/com.garudatekno.jemaah/"+id+".mp3");
//            mp.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        srcPath="/sdcard/android/data/com.garudatekno.jemaah/"+id+".mp3";
        cmdReset();
        cmdSetDataSource(srcPath);

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
                Toast.makeText(ViewPanduan.this,
                        e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IllegalStateException e) {
                Toast.makeText(ViewPanduan.this,
                        e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(ViewPanduan.this,
                        e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else{
            Toast.makeText(ViewPanduan.this,
                    "Invalid State@cmdSetDataSource - skip",
                    Toast.LENGTH_LONG).show();
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
                Toast.makeText(ViewPanduan.this,
                        e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            if (requestCode == RQS_OPEN_AUDIO_MP3) {
//                Uri audioFileUri = data.getData();
//
//                srcPath = audioFileUri.getPath();
//                buttonStart.setText(srcPath);
//
//                cmdReset();
//                cmdSetDataSource(srcPath);
//
//            }
//        }
//    }


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
                String s = rh.sendGetRequestParam(AppConfig.URL_GET_DOA,id);
                Log.d("GETDOA", AppConfig.URL_GET_DOA+ id);
                return s;
            }
        }
        GetData ge = new GetData();
        ge.execute();
    }

//    private void PlayAudio(){
//        if(mp.isPlaying()){
//                    mp.pause();
//            buttonStart.setText("PLAY");
//                } else {
//                    mp.start();
//            buttonStart.setText("STOP");
//                }
//    }
    private void showData(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(AppConfig.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            String id = c.getString(AppConfig.KEY_ID);
            String data = c.getString(AppConfig.KEY_DESCRIPTION);
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
