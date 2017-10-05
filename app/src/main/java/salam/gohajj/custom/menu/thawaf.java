package salam.gohajj.custom.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import me.anwarshahriar.calligrapher.Calligrapher;
import salam.gohajj.custom.GetTemplates;
import salam.gohajj.custom.R;
import salam.gohajj.custom.app.AppConfig;
import salam.gohajj.custom.app.AppController;
import salam.gohajj.custom.helper.SQLiteHandler;
import salam.gohajj.custom.helper.SessionManager;


public class thawaf extends AppCompatActivity implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private SQLiteHandler db;
    private SessionManager session;

    private SQLiteDatabase database;

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private TextView state,txt_play,txt_back,txt_next,
            circle,txtArab,txtArti,txt_doa1,txt_doa2,txt_doa3,
            txt_doa4,txt_doa5,txt_doa6,txt_doa7,txt_doa8,vname,varab,varti,vaudio,v_play;
    ImageView img_back,img_next,progress,img_play,arrow_back,img_vplay;
    String srcPath = null;
    enum MP_State {
        Idle, Initialized, Prepared, Started, Paused,
        Stopped, PlaybackCompleted, End, Error, Preparing}

    thawaf.MP_State mediaPlayerState;
    private String id,uid, text_arti, text_terjemahan;
    private SeekBar timeLine;
    LinearLayout timeFrame;
    private Button BtnTerjemahan, btnTerjemah;
    TextView timePos, timeDur;
    final static int RQS_OPEN_AUDIO_MP3 = 1;
    MediaPlayer mediaPlayer;
    MediaPlayer mp;
    ProgressDialog pd;
    LinearLayout menu_play,menu_next,menu_back,judul,isi,vplay;
    private Tracker mTracker;
    private Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thawaf);
        activity = this;
        GetTemplates.GetStatusBar(activity);
        final RelativeLayout rel_header = (RelativeLayout) findViewById(R.id.header2);
        rel_header.setBackground(getResources().getDrawable(GetTemplates.GetHeaderTemplates(activity)));
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        //tracker
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        // [START screen_view_hit]
        mTracker.setScreenName("Thawaf");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
        //end

        database=openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);

        // session manager
        session = new SessionManager(getApplicationContext());
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


        circle = (TextView) findViewById(R.id.circle);
        txt_back = (TextView) findViewById(R.id.txt_back);
        txt_play = (TextView) findViewById(R.id.txt_play);
        txt_next = (TextView) findViewById(R.id.txt_next);
        txtArab = (TextView) findViewById(R.id.txtArab);
        txtArti = (TextView) findViewById(R.id.txtArti);
        btnTerjemah = (Button) findViewById(R.id.btnTerjemah);
        txt_doa1 = (TextView) findViewById(R.id.txt_doa1);
        txt_doa2 = (TextView) findViewById(R.id.txt_doa2);
        txt_doa3 = (TextView) findViewById(R.id.txt_doa3);
        txt_doa4 = (TextView) findViewById(R.id.txt_doa4);
        txt_doa5 = (TextView) findViewById(R.id.txt_doa5);
        txt_doa6 = (TextView) findViewById(R.id.txt_doa6);
        txt_doa7 = (TextView) findViewById(R.id.txt_doa7);
        txt_doa8 = (TextView) findViewById(R.id.txt_doa8);

        progress = (ImageView) findViewById(R.id.progress);
        img_next = (ImageView) findViewById(R.id.img_next);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_play = (ImageView) findViewById(R.id.img_play);

        menu_back = (LinearLayout) findViewById(R.id.menu_back);
        menu_play = (LinearLayout) findViewById(R.id.menu_play);
        menu_next = (LinearLayout) findViewById(R.id.menu_next);
        judul = (LinearLayout) findViewById(R.id.judul);
        isi = (LinearLayout) findViewById(R.id.isi);
        state = (TextView)findViewById(R.id.state);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        //service
//        startService(new Intent(thawaf.this, BackgroundService.class));
        ScheduledExecutorService myScheduledExecutorService = Executors.newScheduledThreadPool(1);

//        myScheduledExecutorService.scheduleWithFixedDelay(
//                new Runnable(){
//                    @Override
//                    public void run() {
//                        monitorHandler.sendMessage(monitorHandler.obtainMessage());
//                    }},
//                200, //initialDelay
//                200, //delay
//                TimeUnit.MILLISECONDS);

        menu_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ids = circle.getText().toString().trim();
                final String play = txt_play.getText().toString().trim();
//                media/doa_harian/01_doa_tawaf_1.mp3
                Log.e("id_circle",ids);
                srcPath="/sdcard/android/data/com.gohajj.id/thawaf/"+ids+".mp3";
                File cek = new File(srcPath);
                if (!cek.exists()) {
                    srcPath=AppConfig.URL_HOME+"/uploads/panduan/thawaf/"+ids+".mp3";
                }
//                cmdReset();
//                cmdSetDataSource(srcPath);

                if(play.equals(getResources().getString(R.string.mulai))) {
                    judul.setVisibility(View.VISIBLE);
                    isi.setVisibility(View.VISIBLE);
                    img_next.setVisibility(View.VISIBLE);
                    img_back.setVisibility(View.VISIBLE);
                    txt_back.setVisibility(View.VISIBLE);
                    txt_next.setVisibility(View.VISIBLE);
                    txt_next.setVisibility(View.VISIBLE);
                    txt_play.setText(getResources().getString(R.string.mainkan));
                    circle.setText("1");
                    SetProgess(1);
                    circle.setBackground(getImageDrawable(R.drawable.circle_sai_blue));
                }else if(play.equals(getResources().getString(R.string.pause))) {
//                    cmdStop();
//                    cmdPause();
                    if(mp.isPlaying()){
                        mp.pause();
                    }
                    img_play.setImageDrawable(getImageDrawable(R.drawable.play));
                    txt_play.setText(getResources().getString(R.string.mainkan));
                }else if(play.equals(getResources().getString(R.string.mainkan))) {
                    if (!cek_status(getApplicationContext()))
                    {
                        Toast.makeText(thawaf.this,
                                getResources().getString(R.string.cek_koneksi),
                                Toast.LENGTH_SHORT).show();
                    }else {
                        stopPlaying();
                        try {
                            String query = "UPDATE play set status=1 where id=1;";
                            database.execSQL(query);
                            pd = new ProgressDialog(thawaf.this);
                            pd.setMessage(getResources().getString(R.string.mempersiapkan_audio)+".....");
                            pd.show();
                            mp = new MediaPlayer();
                            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mp.setOnPreparedListener(thawaf.this);
                            mp.setOnErrorListener(thawaf.this);
                            mp.setDataSource(srcPath);
                            mp.prepareAsync();
                            mp.setOnCompletionListener(thawaf.this);
//                            txt_play.setText(getResources().getString(R.string.pause));
//                            img_play.setImageDrawable(getImageDrawable(R.drawable.stop));
                        } catch (Exception e) {
                            Log.e("StreamAudioDemo", e.getMessage());
                        }
                    }
                }
            }
        });

        menu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tply=txt_play.getText().toString();
                if (!tply.equals(getResources().getString(R.string.mulai))) {
                    stopPlaying();
                    txt_play.setText(getResources().getString(R.string.mainkan));
                    img_play.setImageDrawable(getImageDrawable(R.drawable.play));
                    final String ids = circle.getText().toString();
                    if (Integer.parseInt(ids) > 1) {
                        int no = Integer.parseInt(ids) - 1;
                        circle.setText("" + no + "");
                        SetProgess(no);
                    } else {
                        isi.setVisibility(View.GONE);
                        img_next.setVisibility(View.GONE);
                        img_back.setVisibility(View.GONE);
                        txt_back.setVisibility(View.GONE);
                        txt_next.setVisibility(View.GONE);
                        txt_next.setVisibility(View.GONE);
                        txt_play.setText(getResources().getString(R.string.mulai));
                        SetProgess(0);
                        circle.setBackground(getImageDrawable(R.drawable.circle));
                        circle.setText("");
                    }
                }
            }
        });

        menu_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tply=txt_play.getText().toString();
                if (!tply.equals(getResources().getString(R.string.mulai))) {
                    stopPlaying();
                    txt_play.setText(getResources().getString(R.string.mainkan));
                    img_play.setImageDrawable(getImageDrawable(R.drawable.play));
                    final String ids = circle.getText().toString();
                    if (Integer.parseInt(ids) < 7) {
                        int no = Integer.parseInt(ids) + 1;
                        circle.setText("" + no + "");
                        SetProgess(no);
                    } else {
                        isi.setVisibility(View.GONE);
                        img_next.setVisibility(View.GONE);
                        img_back.setVisibility(View.GONE);
                        txt_back.setVisibility(View.GONE);
                        txt_next.setVisibility(View.GONE);
                        txt_next.setVisibility(View.GONE);
                        txt_play.setText(getResources().getString(R.string.mulai));
                        SetProgess(0);
                        circle.setBackground(getImageDrawable(R.drawable.circle));
                        circle.setText("");

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(thawaf.this);
                        alertDialogBuilder.setMessage(getResources().getString(R.string.alert_thawaf));
                        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ya),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Intent i = new Intent(getApplicationContext(), sai.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });

                        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.tidak), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                            finish();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
            }
        });

        //popup
        final Dialog rankDialog = new Dialog(thawaf.this);
        rankDialog.setContentView(R.layout.view_dialog);
        rankDialog.setCanceledOnTouchOutside(false);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/helvetica.ttf");
        vname = (TextView) rankDialog.findViewById(R.id.vname);
        varab = (TextView) rankDialog.findViewById(R.id.vArab);
        varti = (TextView) rankDialog.findViewById(R.id.varti);
        vaudio = (TextView) rankDialog.findViewById(R.id.vaudio);
        v_play = (TextView) rankDialog.findViewById(R.id.txt_play);
        arrow_back = (ImageView) rankDialog.findViewById(R.id.arrow_back);
        img_vplay = (ImageView) rankDialog.findViewById(R.id.img_play);
        vplay = (LinearLayout) rankDialog.findViewById(R.id.menu_play);
        BtnTerjemahan = (Button) rankDialog.findViewById(R.id.btnTerjemahan);
        vname.setTypeface(font);
        varab.setTypeface(font);
        varti.setTypeface(font);
        v_play.setTypeface(font);
        v_play.setText(getResources().getString(R.string.mainkan));

        rankDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    stopPlaying();
                    rankDialog.dismiss();
                }
                return true;
            }
        });

        vplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ids = vaudio.getText().toString().trim();
                final String play = v_play.getText().toString().trim();
                srcPath="/sdcard/android/data/com.gohajj.id/thawaf/"+ids;
                File cek = new File(srcPath);
                if (!cek.exists()) {
                    srcPath=AppConfig.URL_HOME+"/uploads/panduan/thawaf/"+ids;
                }

                if(play.equals(getResources().getString(R.string.pause))) {
//                    cmdStop();
                    if(mp.isPlaying()){
                        mp.pause();
                    }
                    img_vplay.setImageDrawable(getImageDrawable(R.drawable.play));
                    v_play.setText(getResources().getString(R.string.mainkan));
                }else if(play.equals(getResources().getString(R.string.mainkan))) {
//                    mProgressDialog = ProgressDialog.show(thawaf.this,"","Harap Tunggu...",false,false);
                    if (!cek_status(getApplicationContext()))
                    {
                        Toast.makeText(thawaf.this,
                                getResources().getString(R.string.cek_koneksi),
                                Toast.LENGTH_SHORT).show();
                    }else {
                        stopPlaying();
                        try {
                            String query = "UPDATE play set status=2 where id=1;";
                            database.execSQL(query);
                            pd = new ProgressDialog(thawaf.this);
                            pd.setMessage(getResources().getString(R.string.mempersiapkan_audio)+"...");
                            pd.show();
                            mp = new MediaPlayer();
                            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mp.setOnPreparedListener(thawaf.this);
                            mp.setOnErrorListener(thawaf.this);
                            mp.setDataSource(srcPath);
                            mp.prepareAsync();
                            mp.setOnCompletionListener(thawaf.this);
//                            v_play.setText(getResources().getString(R.string.pause));
//                            img_vplay.setImageDrawable(getImageDrawable(R.drawable.stop));
                        } catch (Exception e) {
                            Log.e("StreamAudioDemo", e.getMessage());
                        }
                    }

                }
            }
        });

//        rankDialog.getWindow().setLayout(700, 450);
        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();
                rankDialog.dismiss();
            }
        });

        txt_doa1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText(getResources().getString(R.string.mainkan));
                img_vplay.setImageDrawable(getImageDrawable(R.drawable.play));
                vname.setText(getResources().getString(R.string.thawaf1));
                varab.setText(getResources().getString(R.string.arab_doa_istilam));
//                varti.setText(getResources().getString(R.string.text_doa_istilam));
                vaudio.setText("8 doa istilam.mp3");
                text_arti=getResources().getString(R.string.text_doa_istilam);
                text_terjemahan=getResources().getString(R.string.terjemahan_istilam);
                BtnTerjemahan.setText(getResources().getString(R.string.teks));
                setBtnTerjemahan();
                BtnTerjemahan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setBtnTerjemahan();
                    }
                });
                rankDialog.show();
            }
        });

        txt_doa2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText(getResources().getString(R.string.mainkan));
                img_vplay.setImageDrawable(getImageDrawable(R.drawable.play));
                vname.setText(getResources().getString(R.string.thawaf2));
                varab.setText(getResources().getString(R.string.arab_doa_diantara_rukun_yamani));
                varti.setText(getResources().getString(R.string.text_doa_diantara_rukun_yamani));
                vaudio.setText("9 doa diantara rukun yamani dan hajar aswad.mp3");
                text_arti=getResources().getString(R.string.text_doa_diantara_rukun_yamani);
                text_terjemahan=getResources().getString(R.string.terjemahan_diantara_rukun_yamani);
                BtnTerjemahan.setText(getResources().getString(R.string.teks));
                setBtnTerjemahan();
                BtnTerjemahan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setBtnTerjemahan();
                    }
                });

                rankDialog.show();
            }
        });
        txt_doa3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText(getResources().getString(R.string.mainkan));
                img_vplay.setImageDrawable(getImageDrawable(R.drawable.play));
                vname.setText(getResources().getString(R.string.thawaf3));
                varab.setText(getResources().getString(R.string.arab_doa_saat_di_multazam));
                varti.setText(getResources().getString(R.string.text_doa_saat_di_multazam));
                vaudio.setText("10 doa saat di Multazam.mp3");
                text_arti=getResources().getString(R.string.text_doa_saat_di_multazam);
                text_terjemahan=getResources().getString(R.string.terjemahan_saat_di_multazam);
                BtnTerjemahan.setText(getResources().getString(R.string.teks));
                setBtnTerjemahan();
                BtnTerjemahan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setBtnTerjemahan();
                    }
                });
                rankDialog.show();
            }
        });
        txt_doa4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText(getResources().getString(R.string.mainkan));
                img_vplay.setImageDrawable(getImageDrawable(R.drawable.play));
                vname.setText(getResources().getString(R.string.thawaf4));
                varab.setText(getResources().getString(R.string.arab_doa_mencium_hajar_aswad));
                varti.setText(getResources().getString(R.string.text_doa_mencium_hajar_aswad));
                vaudio.setText("11 doa mencium hajar aswad.mp3");
                text_arti=getResources().getString(R.string.text_doa_mencium_hajar_aswad);
                text_terjemahan=getResources().getString(R.string.terjemahan_mencium_hajar_aswad);
                BtnTerjemahan.setText(getResources().getString(R.string.teks));
                setBtnTerjemahan();
                BtnTerjemahan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setBtnTerjemahan();
                    }
                });
                rankDialog.show();
            }
        });
        txt_doa5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText(getResources().getString(R.string.mainkan));
                img_vplay.setImageDrawable(getImageDrawable(R.drawable.play));
                vname.setText(getResources().getString(R.string.thawaf5));
                varab.setText(getResources().getString(R.string.arab_doa_setelah_shalat_sunnah_tawaf));
                varti.setText(getResources().getString(R.string.text_doa_setelah_shalat_sunnah_tawaf));
                vaudio.setText("12 doa setelah shalat sunnah tawaf.mp3");
                text_arti=getResources().getString(R.string.text_doa_setelah_shalat_sunnah_tawaf);
                text_terjemahan=getResources().getString(R.string.terjemahan_setelah_shalat_sunnah_tawaf);
                BtnTerjemahan.setText(getResources().getString(R.string.teks));
                setBtnTerjemahan();
                BtnTerjemahan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setBtnTerjemahan();
                    }
                });
                rankDialog.show();
            }
        });
        txt_doa6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText(getResources().getString(R.string.mainkan));
                img_vplay.setImageDrawable(getImageDrawable(R.drawable.play));
                vname.setText(getResources().getString(R.string.thawaf6));
                varab.setText(getResources().getString(R.string.arab_doa_minum_air_zam_zam));
                varti.setText(getResources().getString(R.string.text_doa_minum_air_zam_zam));
                vaudio.setText("13 doa minum air zam zam.mp3");
                text_arti=getResources().getString(R.string.text_doa_minum_air_zam_zam);
                text_terjemahan=getResources().getString(R.string.terjemahan_minum_air_zam_zam);
                BtnTerjemahan.setText(getResources().getString(R.string.teks));
                setBtnTerjemahan();
                BtnTerjemahan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setBtnTerjemahan();
                    }
                });
                rankDialog.show();
            }
        });
        txt_doa7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText(getResources().getString(R.string.mainkan));
                img_vplay.setImageDrawable(getImageDrawable(R.drawable.play));
                vname.setText(getResources().getString(R.string.thawaf7));
                varab.setText(getResources().getString(R.string.arab_doa_tawaf_wada));
                varti.setText(getResources().getString(R.string.text_doa_tawaf_wada));
                vaudio.setText("14 doa tawaf wada.mp3");
                text_arti=getResources().getString(R.string.text_doa_tawaf_wada);
                text_terjemahan=getResources().getString(R.string.terjemahan_tawaf_wada);
                BtnTerjemahan.setText(getResources().getString(R.string.teks));
                setBtnTerjemahan();
                BtnTerjemahan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setBtnTerjemahan();
                    }
                });
                rankDialog.show();
            }
        });
        txt_doa8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText(getResources().getString(R.string.mainkan));
                img_vplay.setImageDrawable(getImageDrawable(R.drawable.play));
                vname.setText(getResources().getString(R.string.thawaf8));
                varab.setText(getResources().getString(R.string.arab_doa_setelah_selesai_tawaf_wada));
                varti.setText(getResources().getString(R.string.text_doa_setelah_selesai_tawaf_wada));
                vaudio.setText("15 doa setelah selesai tawaf wada.mp3");
                text_arti=getResources().getString(R.string.text_doa_setelah_selesai_tawaf_wada);
                text_terjemahan=getResources().getString(R.string.terjemahan_setelah_selesai_tawaf_wada);
                BtnTerjemahan.setText(getResources().getString(R.string.teks));
                setBtnTerjemahan();
                BtnTerjemahan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setBtnTerjemahan();
                    }
                });
                rankDialog.show();
            }
        });

    }

    public void setBtnTerjemahan(){

        String val=BtnTerjemahan.getText().toString();
        if(val.equals(getResources().getString(R.string.terjemahan))){
            varti.setText(text_terjemahan);
            BtnTerjemahan.setText(getResources().getString(R.string.teks));
        }else{
            varti.setText(text_arti);
            BtnTerjemahan.setText(getResources().getString(R.string.terjemahan));

        }
    }

    public Drawable getImageDrawable(int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //>= API 21
            return getResources().getDrawable(id, getApplicationContext().getTheme());
        } else {
            return getResources().getDrawable(id);
        }
    }

    private void SetProgess(int no){
        String txtLisan = "";
        String txtTerjemah = "";
        if(no == 1){
            progress.setImageDrawable(getImageDrawable(R.drawable.thawaf_1));
            txtArab.setText(getResources().getString(R.string.doa_tawaf_1));
            txtLisan = getResources().getString(R.string.text_doa_tawaf_1);
            txtTerjemah = getResources().getString(R.string.terjemahan_doa_tawaf_1);
        }else if(no == 2){
            progress.setImageDrawable(getImageDrawable(R.drawable.thawaf_2));
            txtArab.setText(getResources().getString(R.string.doa_tawaf_2));
            txtLisan = getResources().getString(R.string.text_doa_tawaf_2);
            txtTerjemah = getResources().getString(R.string.terjemahan_doa_tawaf_2);
        }else if(no == 3){
            progress.setImageDrawable(getImageDrawable(R.drawable.thawaf_3));
            txtArab.setText(getResources().getString(R.string.doa_tawaf_3));
            txtLisan = getResources().getString(R.string.text_doa_tawaf_3);
            txtTerjemah = getResources().getString(R.string.terjemahan_doa_tawaf_3);
        }else if(no == 4){
            progress.setImageDrawable(getImageDrawable(R.drawable.thawaf_4));
            txtArab.setText(getResources().getString(R.string.doa_tawaf_4));
            txtLisan = getResources().getString(R.string.text_doa_tawaf_4);
            txtTerjemah = getResources().getString(R.string.terjemahan_doa_tawaf_4);
        }else if(no == 5){
            progress.setImageDrawable(getImageDrawable(R.drawable.thawaf_5));
            txtArab.setText(getResources().getString(R.string.doa_tawaf_5));
            txtLisan = getResources().getString(R.string.text_doa_tawaf_5);
            txtTerjemah = getResources().getString(R.string.terjemahan_doa_tawaf_5);
        }else if(no == 6){
            progress.setImageDrawable(getImageDrawable(R.drawable.thawaf_6));
            txtArab.setText(getResources().getString(R.string.doa_tawaf_6));
            txtLisan = getResources().getString(R.string.text_doa_tawaf_6);
            txtTerjemah = getResources().getString(R.string.terjemahan_doa_tawaf_6);
        }else if(no == 7){
            progress.setImageDrawable(getImageDrawable(R.drawable.thawaf_7));
            txtArab.setText(getResources().getString(R.string.doa_tawaf_7));
            txtLisan = getResources().getString(R.string.text_doa_tawaf_7);
            txtTerjemah = getResources().getString(R.string.terjemahan_doa_tawaf_7);
        }else{
            progress.setImageDrawable(getImageDrawable(R.drawable.thawaf_0));
            txtArab.setText("");
            txtArti.setText("");
        }

        final String finalTxtTerjemah = txtTerjemah;
        final String finalTxtLisan = txtLisan;
        txtArti.setText(finalTxtLisan);
        btnTerjemah.setText(getResources().getString(R.string.terjemahan));
        btnTerjemah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String val=btnTerjemah.getText().toString();
                if(val.equals(getResources().getString(R.string.terjemahan))){
                    txtArti.setText(finalTxtTerjemah);
                    btnTerjemah.setText(getResources().getString(R.string.teks));
                }else{
                    txtArti.setText(finalTxtLisan);
                    btnTerjemah.setText(getResources().getString(R.string.terjemahan));

                }
            }
        });
    }


    @Override
    public void onStop()
    {
        super.onStop();
        stopPlaying();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
//        pd.setMessage("Playing.....");
        mp.start();
        if(mp.isPlaying()) {
            pd.dismiss();
            Cursor mCoun= database.rawQuery("select status from play where id=1", null);
            mCoun.moveToFirst();
            int coun= mCoun.getInt(0);
            if(coun == 1) {
                txt_play.setText(getResources().getString(R.string.pause));
                img_play.setImageResource(R.drawable.stop);
            }else{
                v_play.setText(getResources().getString(R.string.pause));
                img_vplay.setImageResource(R.drawable.stop);
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        pd.dismiss();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        pd.dismiss();
        final String play = txt_play.getText().toString().trim();
        final String play2 = v_play.getText().toString().trim();
        if(play.equals(getResources().getString(R.string.pause))){
        txt_play.setText(getResources().getString(R.string.mainkan));
        img_play.setImageDrawable(getImageDrawable(R.drawable.play));
        }
        if(play2.equals(getResources().getString(R.string.pause))) {
            img_vplay.setImageDrawable(getImageDrawable(R.drawable.play));
            v_play.setText(getResources().getString(R.string.mainkan));
        }
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.selesai), Toast.LENGTH_LONG).show();
    }

    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
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
    public void onBackPressed() {
        //super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), panduan.class);
        panduan.setTabIndex(2);
        startActivity(i);
        finish();
    }

}
