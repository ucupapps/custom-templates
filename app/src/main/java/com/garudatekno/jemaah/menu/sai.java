package com.garudatekno.jemaah.menu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.BackgroundService;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.activity.MainActivity;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;

import static com.garudatekno.jemaah.app.AppConfig.URL_HOME;
import static java.lang.Boolean.FALSE;


public class sai extends AppCompatActivity {

    private SQLiteHandler db;
    private SessionManager session;

    protected TextView CC,C_1,C_2,C_3,C_4,C_5,C_6,C_7,T_1,T_2,T_3,T_4,T_5,T_6,T_7,state;

    private SQLiteDatabase database;

    private Button btnplay;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private TextView txtid;

    String srcPath = null;
    enum MP_State {
        Idle, Initialized, Prepared, Started, Paused,
        Stopped, PlaybackCompleted, End, Error, Preparing}

    sai.MP_State mediaPlayerState;
    private String uid;
    private SeekBar timeLine;
    LinearLayout timeFrame;
    TextView timePos, timeDur;
    final static int RQS_OPEN_AUDIO_MP3 = 1;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sai);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        // session manager
        session = new SessionManager(getApplicationContext());
        final ImageView img_home=(ImageView) findViewById(R.id.img_home);
        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
            }
        });
        final  ImageView img_setting=(ImageView) findViewById(R.id.img_setting);
        final PopupMenu popup = new PopupMenu(this, img_setting);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        if (!session.isLoggedIn()) {
            Menu popupMenu = popup.getMenu();
            popupMenu.findItem(R.id.logout).setVisible(FALSE);
        }
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == R.id.logout) {
                            logoutUser();
                        }if(id == R.id.donasi) {
                            Uri uriUrl = Uri.parse("https://kitabisa.com/gohaji");
                            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                            startActivity(launchBrowser);
                        }if(id == R.id.penilaian) {
                            Intent i = new Intent(getApplicationContext(), PenilaianTravel.class);
                            startActivity(i);
                        }if(id == R.id.cek_visa) {
                            Uri uriUrl = Uri.parse("https://eservices.haj.gov.sa/eservices3/pages/VisaInquiry/SearchVisa.xhtml?dswid=4963");
                            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                            startActivity(launchBrowser);
                        }if(id == R.id.share) {
                            try {
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, "GoHajj");
                                String sAux = "\nLet me recommend you this application\n\n";
                                sAux = sAux + "https://play.google.com/store/apps/details?id=GoHajj.Soft \n\n";
                                i.putExtra(Intent.EXTRA_TEXT, sAux);
                                startActivity(Intent.createChooser(i, "choose one"));
                            } catch(Exception e) {
                                //e.toString();
                            }
                        }if(id == R.id.download_doa) {

                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });

        state = (TextView)findViewById(R.id.state);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

//        if (!session.isLoggedIn()) {
//            logoutUser();
//        }

       //circle menu
        CC =(TextView) findViewById(R.id.circle);
        C_1 =(TextView) findViewById(R.id.circle1);
        C_2 =(TextView) findViewById(R.id.circle2);
        C_3=(TextView) findViewById(R.id.circle3);
        C_4=(TextView) findViewById(R.id.circle4);
        C_5=(TextView) findViewById(R.id.circle5);
        C_6=(TextView) findViewById(R.id.circle6);
        C_7=(TextView) findViewById(R.id.circle7);

        //triangle
        T_1 =(TextView) findViewById(R.id.triangle_circle1);
        T_2 =(TextView) findViewById(R.id.triangle_circle2);

        ImageView img_click =(ImageView) findViewById(R.id.img_sai_klik);

        img_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgCenter();
            }
        });

//        Button btnSimple =(Button) findViewById(R.id.buttonSimple);
        C_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBackground(); insertIntoDB("1",C_1,T_1);
            }
        });
        C_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBackground(); insertIntoDB("2",C_2,T_2);
            }
        });
        C_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBackground(); insertIntoDB("3",C_3,T_1);
            }
        });
        C_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBackground(); insertIntoDB("4",C_4,T_2);
            }
        });
        C_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBackground(); insertIntoDB("5",C_5,T_1);
            }
        });
        C_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBackground(); insertIntoDB("6",C_6,T_2);
            }
        });
        C_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBackground(); insertIntoDB("7",C_7,T_1);
            }
        });

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        //service
        startService(new Intent(sai.this, BackgroundService.class));

        btnplay = (Button) findViewById(R.id.btnplay);
        createDatabase();

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ids = CC.getText().toString().trim();
                final String str = btnplay.getText().toString().trim();

                srcPath="/sdcard/android/data/com.garudatekno.jemaah/sai/"+ids+".mp3";
                cmdReset();
                cmdSetDataSource(srcPath);

                if(str.equals("Download")){
                    //make dir
                    File folder = new File("/sdcard/android/data/com.garudatekno.jemaah/sai");
                    boolean success = true;
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    startDownload(ids);
                }else if(str.equals("Stop")) {
                    cmdStop();
                    btnplay.setText("Putar Audio");
                }else if(str.equals("Putar Audio")) {
                    cmdPrepare();
                    cmdStart();
                    btnplay.setText("Stop");
                }
            }
        });
    }

    protected void resetBackground(){
        C_1.setBackgroundResource(R.drawable.circle_sai);
        C_1.setTextColor(Color.WHITE);
        C_2.setBackgroundResource(R.drawable.circle_sai);
        C_2.setTextColor(Color.WHITE);
        C_3.setBackgroundResource(R.drawable.circle_sai);
        C_3.setTextColor(Color.WHITE);
        C_4.setBackgroundResource(R.drawable.circle_sai);
        C_4.setTextColor(Color.WHITE);
        C_5.setBackgroundResource(R.drawable.circle_sai);
        C_5.setTextColor(Color.WHITE);
        C_6.setBackgroundResource(R.drawable.circle_sai);
        C_6.setTextColor(Color.WHITE);
        C_7.setBackgroundResource(R.drawable.circle_sai);
        C_7.setTextColor(Color.WHITE);

        T_1.setBackgroundResource(R.drawable.triangle_clear);
        T_2.setBackgroundResource(R.drawable.triangle_clear);
    }

    protected void imgCenter(){
        resetBackground();
        Cursor c = database.rawQuery("SELECT * FROM sai WHERE name='sai'", null);
        c.moveToFirst();
        String stat=c.getString(2);
        if(stat.equals("1")){
            insertIntoDB("2",C_2,T_2);
        }else if(stat.equals("2")){
            insertIntoDB("3",C_3,T_1);
        }else if(stat.equals("3")){
            insertIntoDB("4",C_4,T_2);
        }else if(stat.equals("4")){
            insertIntoDB("5",C_5,T_1);
        }else if(stat.equals("5")){
            insertIntoDB("6",C_6,T_2);
        }else if(stat.equals("6")){
            insertIntoDB("7",C_7,T_1);
        }else if(stat.equals("7")){
            insertIntoDB("1",C_1,T_1);
        }else{
            insertIntoDB("1",C_1,T_1);
        }

    }

    protected void createDatabase(){
        database=openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS sai(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR,status VARCHAR);");
        Cursor mCount= database.rawQuery("select count(*) from sai where name='sai'", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        if(count > 0) {
            insertIntoDB("1",C_1,T_1);
        }else {
            String query = "INSERT INTO sai (name,status) VALUES('sai', '1');";
            database.execSQL(query);
            insertIntoDB("1",C_1,T_1);
        }
    }

    protected void insertIntoDB(String status,TextView circle,TextView triangle){
       String query = "UPDATE sai SET status='" + status + "' WHERE name='sai';";
       database.execSQL(query);

        Cursor c = database.rawQuery("SELECT * FROM sai WHERE name='sai'", null);

        c.moveToFirst();
        String stat=c.getString(2);
        Log.d("MyDataShow", "status: " + stat);
        history(stat);
        CC.setText(stat);
        triangle.setBackgroundResource(R.drawable.triangle);
        circle.setBackgroundResource(R.drawable.circle_sai_blue);
        circle.setTextColor(Color.WHITE);

        File file = new File("/sdcard/android/data/com.garudatekno.jemaah/sai/"+stat+".mp3");

        if (!file.exists()) {
            btnplay.setText("Download");
            btnplay.setBackgroundResource(R.drawable.button_blue);
        }else{
            btnplay.setText("Putar Audio");
            btnplay.setBackgroundResource(R.drawable.button);
        }

    }
    protected void history(String stat){
        if(stat.equals("1")){
//            insertIntoDB("2",C_2,T_2);
        }else if(stat.equals("2")){
           C_1.setBackgroundResource(R.drawable.circle_sai_green);
        }else if(stat.equals("3")){
            C_1.setBackgroundResource(R.drawable.circle_sai_green);
            C_2.setBackgroundResource(R.drawable.circle_sai_green);
        }else if(stat.equals("4")){
            C_1.setBackgroundResource(R.drawable.circle_sai_green);
            C_2.setBackgroundResource(R.drawable.circle_sai_green);
            C_3.setBackgroundResource(R.drawable.circle_sai_green);
        }else if(stat.equals("5")){
            C_1.setBackgroundResource(R.drawable.circle_sai_green);
            C_2.setBackgroundResource(R.drawable.circle_sai_green);
            C_3.setBackgroundResource(R.drawable.circle_sai_green);
            C_4.setBackgroundResource(R.drawable.circle_sai_green);
        }else if(stat.equals("6")){
            C_1.setBackgroundResource(R.drawable.circle_sai_green);
            C_2.setBackgroundResource(R.drawable.circle_sai_green);
            C_3.setBackgroundResource(R.drawable.circle_sai_green);
            C_4.setBackgroundResource(R.drawable.circle_sai_green);
            C_5.setBackgroundResource(R.drawable.circle_sai_green);
        }else if(stat.equals("7")){
            C_1.setBackgroundResource(R.drawable.circle_sai_green);
            C_2.setBackgroundResource(R.drawable.circle_sai_green);
            C_3.setBackgroundResource(R.drawable.circle_sai_green);
            C_4.setBackgroundResource(R.drawable.circle_sai_green);
            C_5.setBackgroundResource(R.drawable.circle_sai_green);
            C_6.setBackgroundResource(R.drawable.circle_sai_green);
        }
    }
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(sai.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void startDownload(String id) {
        String url = URL_HOME+"/uploads/doa/"+id+".mp3";
        new sai.DownloadFileAsync().execute(url);
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
        final String id = CC.getText().toString().trim();
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

                //save file
                OutputStream output = new FileOutputStream("/sdcard/android/data/com.garudatekno.jemaah/sai/"+id+".mp3");

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
                Intent i = new Intent(getApplicationContext(), sai.class);
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

    //play
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
            }
        }
    }

    MediaPlayer.OnErrorListener mediaPlayerOnErrorListener
            = new MediaPlayer.OnErrorListener(){

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            // TODO Auto-generated method stub

            mediaPlayerState = sai.MP_State.Error;
            showMediaPlayerState();

            return false;
        }};


    private void cmdReset(){
        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnErrorListener(mediaPlayerOnErrorListener);
        }
        mediaPlayer.reset();
        mediaPlayerState = sai.MP_State.Idle;
        showMediaPlayerState();
    }

    private void cmdSetDataSource(String path){
        if(mediaPlayerState == sai.MP_State.Idle){
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayerState = sai.MP_State.Initialized;
            } catch (IllegalArgumentException e) {
//                Toast.makeText(sai.this,
//                        e.toString(), Toast.LENGTH_LONG).show();
//                e.printStackTrace();
            } catch (IllegalStateException e) {
//                Toast.makeText(sai.this,
//                        e.toString(), Toast.LENGTH_LONG).show();
//                e.printStackTrace();
            } catch (IOException e) {
//                Toast.makeText(sai.this,
//                        e.toString(), Toast.LENGTH_LONG).show();
//                e.printStackTrace();
            }
        }else{
//            Toast.makeText(sai.this,
//                    "Invalid State@cmdSetDataSource - skip",
//                    Toast.LENGTH_LONG).show();
        }

        showMediaPlayerState();
    }

    private void cmdPrepare(){

        if(mediaPlayerState == sai.MP_State.Initialized
                ||mediaPlayerState == sai.MP_State.Stopped){
            try {
                mediaPlayer.prepare();
                mediaPlayerState = sai.MP_State.Prepared;
            } catch (IllegalStateException e) {
                Toast.makeText(sai.this,
                        e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(sai.this,
                        e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else{
//            Toast.makeText(sai.this,
//                    "Invalid State@cmdPrepare() - skip",
//                    Toast.LENGTH_LONG).show();
        }

        showMediaPlayerState();
    }

    private void cmdStart(){
        if(mediaPlayerState == sai.MP_State.Prepared
                ||mediaPlayerState == sai.MP_State.Started
                ||mediaPlayerState == sai.MP_State.Paused
                ||mediaPlayerState == sai.MP_State.PlaybackCompleted){
            mediaPlayer.start();
            mediaPlayerState = sai.MP_State.Started;
        }else{
//            Toast.makeText(sai.this,
//                    "Invalid State@cmdStart() - skip",
//                    Toast.LENGTH_LONG).show();
        }

        showMediaPlayerState();
    }

    private void cmdPause(){
        if(mediaPlayerState == sai.MP_State.Started
                ||mediaPlayerState == sai.MP_State.Paused){
            mediaPlayer.pause();
            mediaPlayerState = sai.MP_State.Paused;
        }else{
//            Toast.makeText(sai.this,
//                    "Invalid State@cmdPause() - skip",
//                    Toast.LENGTH_LONG).show();
        }
        showMediaPlayerState();
    }

    private void cmdStop(){

        if(mediaPlayerState == sai.MP_State.Prepared
                ||mediaPlayerState == sai.MP_State.Started
                ||mediaPlayerState == sai.MP_State.Stopped
                ||mediaPlayerState == sai.MP_State.Paused
                ||mediaPlayerState == sai.MP_State.PlaybackCompleted){
            mediaPlayer.stop();
            mediaPlayerState = sai.MP_State.Stopped;
        }else{
//            Toast.makeText(sai.this,
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

}
