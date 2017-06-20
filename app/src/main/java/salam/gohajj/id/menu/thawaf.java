package salam.gohajj.id.menu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import salam.gohajj.id.R;

import salam.gohajj.id.app.AppConfig;
import salam.gohajj.id.app.AppController;
import salam.gohajj.id.helper.SQLiteHandler;
import salam.gohajj.id.helper.SessionManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import me.anwarshahriar.calligrapher.Calligrapher;


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
    private String id,uid;
    private SeekBar timeLine;
    LinearLayout timeFrame;
    TextView timePos, timeDur;
    final static int RQS_OPEN_AUDIO_MP3 = 1;
    MediaPlayer mediaPlayer;
    MediaPlayer mp;
    ProgressDialog pd;
    LinearLayout menu_play,menu_next,menu_back,judul,isi,vplay;
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thawaf);
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

                srcPath="/sdcard/android/data/com.gohajj.id/thawaf/"+ids+".mp3";
                File cek = new File(srcPath);
                if (!cek.exists()) {
                    srcPath=AppConfig.URL_HOME+"/uploads/panduan/thawaf/"+ids+".mp3";
                }
//                cmdReset();
//                cmdSetDataSource(srcPath);

                if(play.equals("Mulai")) {
                    judul.setVisibility(View.VISIBLE);
                    isi.setVisibility(View.VISIBLE);
                    img_next.setVisibility(View.VISIBLE);
                    img_back.setVisibility(View.VISIBLE);
                    txt_back.setVisibility(View.VISIBLE);
                    txt_next.setVisibility(View.VISIBLE);
                    txt_next.setVisibility(View.VISIBLE);
                    txt_play.setText("Mainkan");
                    circle.setText("1");
                    SetProgess(1);
                    circle.setBackground(getResources().getDrawable(R.drawable.circle_sai_blue));
                }else if(play.equals("Pause")) {
//                    cmdStop();
//                    cmdPause();
                    if(mp.isPlaying()){
                        mp.pause();
                    }
                    img_play.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    txt_play.setText("Mainkan");
                }else if(play.equals("Mainkan")) {
                    if (!cek_status(getApplicationContext()))
                    {
                        Toast.makeText(thawaf.this,
                                "Mohon Cek Koneksi Anda",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        stopPlaying();
                        try {
                            pd = new ProgressDialog(thawaf.this);
                            pd.setMessage("Mempersiapkan Audio.....");
                            pd.show();
                            mp = new MediaPlayer();
                            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mp.setOnPreparedListener(thawaf.this);
                            mp.setOnErrorListener(thawaf.this);
                            mp.setDataSource(srcPath);
                            mp.prepareAsync();
                            mp.setOnCompletionListener(thawaf.this);
                            txt_play.setText("Pause");
                            img_play.setImageDrawable(getResources().getDrawable(R.drawable.stop));
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
                if (!tply.equals("Mulai")) {
                    stopPlaying();
                    txt_play.setText("Mainkan");
                    img_play.setImageDrawable(getResources().getDrawable(R.drawable.play));
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
                        txt_play.setText("Mulai");
                        SetProgess(0);
                        circle.setBackground(getResources().getDrawable(R.drawable.circle));
                        circle.setText("");
                    }
                }
            }
        });

        menu_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tply=txt_play.getText().toString();
                if (!tply.equals("Mulai")) {
                    stopPlaying();
                    txt_play.setText("Mainkan");
                    img_play.setImageDrawable(getResources().getDrawable(R.drawable.play));
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
                        txt_play.setText("Mulai");
                        SetProgess(0);
                        circle.setBackground(getResources().getDrawable(R.drawable.circle));
                        circle.setText("");

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(thawaf.this);
                        alertDialogBuilder.setMessage("Ibadah Thawaf sudah selesai, silakan melanjutkan Ibadah Sa'i");
                        alertDialogBuilder.setPositiveButton("Ya",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Intent i = new Intent(getApplicationContext(), sai.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
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
        vname.setTypeface(font);
        varab.setTypeface(font);
        varti.setTypeface(font);
        v_play.setTypeface(font);
        v_play.setText("Mainkan");

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

                if(play.equals("Pause")) {
//                    cmdStop();
                    if(mp.isPlaying()){
                        mp.pause();
                    }
                    img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    v_play.setText("Mainkan");
                }else if(play.equals("Mainkan")) {
//                    mProgressDialog = ProgressDialog.show(thawaf.this,"","Harap Tunggu...",false,false);
                    if (!cek_status(getApplicationContext()))
                    {
                        Toast.makeText(thawaf.this,
                                "Mohon Cek Koneksi Anda",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        stopPlaying();
                        try {
                            pd = new ProgressDialog(thawaf.this);
                            pd.setMessage("Mempersiapkan Audio...");
                            pd.show();
                            mp = new MediaPlayer();
                            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mp.setOnPreparedListener(thawaf.this);
                            mp.setOnErrorListener(thawaf.this);
                            mp.setDataSource(srcPath);
                            mp.prepareAsync();
                            mp.setOnCompletionListener(thawaf.this);
                            v_play.setText("Pause");
                            img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.stop));
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
                stopPlaying();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa Istilam");
                varab.setText("بِسْمِ اللّٰهِ، اَللّٰهُ اَكْبَرُُ");
                varti.setText("Bismillaahi Allaahu Akbar");
                vaudio.setText("8 doa istilam.mp3");
                rankDialog.show();
            }
        });

        txt_doa2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa di Antara Rukun Yamani dan Hajar Aswad");
                varab.setText("رَبَّنَا آتِنَا/ فِيْ الدُّ نْيَا حَسَنَةً/ وَفِيْ الْآخِرَةِ حَسَنَةً/ وَقِنَا عَذَابَ النَّارِ/ وَأَدْخِلْنَا الْجَنَّةَ/ مَعَ الْأَبْرَارِ/ يَا عَزِيْزُ يَا غَفَارُ/ يَا رَبَّ الْعَا لَمِيْنَ");
                varti.setText("Rabbanaa aatinaa fid dun-yaa hasanataw wa fil aakhirati hasanataw wa qinaa ‘adzaaban naar, wa adkhilnal jannata ma’al abraar, yaa aziizu, yaa ghafaaru yaa rabbal ‘aalamiin");
                vaudio.setText("9 doa diantara rukun yamani dan hajar aswad.mp3");
                rankDialog.show();
            }
        });
        txt_doa3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa Saat di Multazam");
                varab.setText("اللَّهُمَّ يَارَبَّ الْبَيْتِ الْعَتِيْقِ/ أَعْتِقْ رِقَابَنَا/ وَرِقَابَ آبَائِنَا/ وَأُمَّهَاتِنَا/ وَإِخْوَانِنَا/ وَأَوْلاَدِنَا/ مِنَ النَّارِ/ يَاذَاالْجُوْدِ وَالْكَرَمِ/ وَالْفَضْلِ وَالْمَنِّ/ وَالْعَطَاءِ وَالْأِحْسَانِ/ اللَّهُمَّ أَحْسِنْ عَقِبَتَنَا/ فِيْ الْأُمُوْرِ كُلِّهَا/ وَأَجِرْنَا مِنْ خِزْيِ الدُّنْيَا/ وَعَذَا بِ الْآخِرَةِ/ اللَّهُمَّ إِنِّيْ عَبْدُكَ/ وَابْنُ عَبْدِكَ/ وَاقِفٌ تَحْتَ بَابِكَ/ مُلْتَزِمٌ بِأَعْتَابِكَ/ مُتَذَلِّلٌ بَيْنَ يَدَيْكَ/ أَرْجُوْ رَحْمَتَكَ/ وَأَخْشَ عَذَابَكَ/ يَا قَدِيْمَ الْإِحْسَانِ،/ اللَّهُمَّ إِنِّيْ أَسْأَلُكَ/ أَنْ تَرْفَعَ ذِكْرِيْ/ وَتَضَعَ وِزْرِيْ/ وَتُصْلِحَ أَمْرِيْ/ وَتُطَهِّرَ قَلْبِيْ/ وَتُنَوِّرَ لِيْ فِيْ قَبْرِيْ/ وَتَغْفِرَلِيْ ذَنْبِيْ/ وَأَسْأَلُكَ الدَّرَ جَاتِ الْعُلَى/ مِنَ الْجَنَّةِ");
                varti.setText("Allahumma yaa rabbal baitil ‘atiiq, a’tiq riqaabanaa wariqaaba aabaa-inaa wa ummahaatinaa wa ikhwaaninaa wa aulaadinaa minan naari yaa dzaal juudi wal karami wal fadhli wal manni wal ‘athaa-l wal ihsaani, Allaahumma ahsin ‘aaqibatanaa fil umuri kullihaa wa ajirnaa min khizyid dun-yaa wa ‘adzaabil aakhirah. Allaahumma innii ‘abduka wabnu ‘abdika waqifun tahta baabika multazimun  bi-a’taabika mutadzallilun baina yadaika arju rahmataka wa akhsyaa ‘adzaabaka ya qadiimal ihsaan. Allaahumma innii asaluka an tarfa’aa dzikrii wa tadha’a wizrii wa tushliha amri wa tuthahhira qalbi wa tunawwirali fi qabrii wa taghfiralii dzanbii wa as-alukad darajaatil ‘ulaa minal jinnati.");
                vaudio.setText("10 doa saat di Multazam.mp3");
                rankDialog.show();
            }
        });
        txt_doa4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa Mencium Hajar Aswad");
                varab.setText("بِسْمِ اللّٰهِ/ وَاللّٰهُ أَكْبَرُ،/ اللَّهُمَّ إِيْمَانًا بِكَ/ وَتَصْدِ يْقًا بِكِتَابِكَ/ وَوَفَاءً بِعَهْدِكَ/ وَاتِّبَاعًا لِسُنَّةِ/ نَبِيِّكَ مُحَمَّدٍ صَلَى اللّٰهُ عَلَيْهِ وَسَلَّمَ");
                varti.setText("Bismillahi wallahu akbar. Allaahumma iimaanan bika wa tasdiiqon bikitaabika wa wafaa an bi’ahdika wattibaa an lisunnati nabiyika Muhammadin shalallahu ‘alaihi wasallam");
                vaudio.setText("11 doa mencium hajar aswad.mp3");
                rankDialog.show();
            }
        });
        txt_doa5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa Setelah Shalat Sunnah Thawaf");
                varab.setText("اللَّهُمَّ إِنَّكَ/ تَعْلَمُ سِرِّيْ/ وَعَلَا نِيَتِيِ فَاقْبَلْ مَعْذِرَتِيِ/ وَتَعْلَمُ حَاجَتِيْ/ فَأَعْطِنِيْ سُؤْلِيْ/ وَتَعْلَمُ مَا فِيْ نَفْسِيْ/ فَاغْفِرْلِيْ ذُنُوْبِيْ،/ اللَّهُمَّ/ إِنِّيْ أَسْأَلُكَ/ إِيْمَانًا دَائِمًا/ يُبَاشِّرُ قَلْبِيْ/ وَيَقِيْنًا صَادِقًا/ حَتَّى أَعۡلَمَ/ اَنَّهُ لَايُسِيْبُنِيْ/ إِلاَّمَا كَتَبْتَهُ عَلَيَّ/ وَرَضِّنِيْ/ بِمَا قَسَّمْتَهُ لِيْ/ يَا أَرْحَمَ الرَّاحِمِيْنَ،/ أَنْتَ وَلِيِّيْ/ فِيْ الدُّنْيَا وَالْآخِرَةِ/ تَوَفَّنِيْ مُسْلِمًا/ وَأَلْحِقْنِيْ بِالصَّالِحِيْنَ،/ اللَّهُمَّ/ لاَتَدَعْ لَنَا/ فِيْ مَقَامِنَا هَذَا ذَنْبًا اِلاَّغَفَرْتَهُ/ وَلاَ هَمَّا اِلاَّ فَرَّجْتَهُ/ وَلاَحَاجَةً اِلاَّقَضَيْتَهَا/ وَيَسَّرْتَهَا فَيَسِّرْ أُمُوْرَنَا/ وَاشْرَحْ صُدُوْرَنَا/ وَنَوِّرْقُلُوْبَنَا/ وَاخۡتِمۡ بِالصَّالِحَاۃِ اَعۡمَالَنَا/ اللَّهُمَّ تَوَفَّنَا مُسْلِمِيْنَ/ وَأَحْيِنَا مُسْلِمِيْنَ/ وَأَلْحِقْنَا بِالصَّالِحِيْنَ/ غَيْرَ خَزَايَا/ وَلاَمَفْتُوْنِيْنََ");
                varti.setText("Allaahumma innaka ta’lamu sirrii wa ‘alaniyatii faqbal ma’dziratii wa ta’lamuu haajati fa a’thinii su’lii wa ta’lamu maa fii nafsii faghfirlii dzunuubii. Allaahumma innii as’aluka iimaanan daa-iman yubaasyiru qalbii wa yaqiinan shaadiqan hattaa a’lama annahuu laa yushiibunii illaa maa katabtahuu ‘alayya wa radhdhinii bimaa qassamtahuu lii yaa arhamar raahimiin, anta waliyyi fid dun-yaa wal aakhiratii tawaffanii musliman wa alhiqnii bishshaalihiin, Allaahumma laa tada’ lanaa fii maqaamina hadzaa dzanban illa ghofartahu wa laa hamma ilaa farrajtahu wa laa haajatan illaa qadhaitahaa wa yassartahaa fayassir umuuranaa wasyrah shuduuranaa wanawwir quluubanaa wakhtim bishshaalihaati a’maalanaa, Allaahumma tawaffanaa muslimiina wa ahyinaa muslimiin, wa alhiqnaa bishshaalihiin ghaira khazaayaa wa laa maftuuniin");
                vaudio.setText("12 doa setelah shalat sunnah tawaf.mp3");
                rankDialog.show();
            }
        });
        txt_doa6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa Minum Air Zamzam");
                varab.setText("اللَّهُمَّ/ إِنِّيْ أَسْأَلُكَ/ عِلْمًا نَافِعًا/ وَرِزْقَا وَاسِعًا/ وَشِفَاءً مِنْ كُلِّ دَاءٍ/ وَسَقَمٍ/ بِرَحْمَتِكَ يَا أَرْحَمَ الرَّاحِمِيْنََ");
                varti.setText("Allaahumma inni as-aluka ‘ilman naafi’an wa rizqan waasi’an wa syifaa-an min kulli daa-in wa saqamin birahmatika yaa arhamar raahimiin");
                vaudio.setText("13 doa minum air zam zam.mp3");
                rankDialog.show();
            }
        });
        txt_doa7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa Thawaf Wada");
                varab.setText("بِسْمِ اللّٰهِ اَللّٰهُ اَكْبَرُ/ سُبْحَانَ اللّٰهِ/ وَالْحَمْدُلِلّٰهِ/ وَلاَاِلَهَ اِلاَّاللّٰهُ/ وَاللّٰهُ أَكْبَرُ،/ وَلاَحَوْلَ/ وَلاَقُوَّةَ/ اِلاَّبِاللّٰهِ الْعَلِيِّ الْعَظِيْمِ،/ وَالصَّلَاةُ وَالسَّلَامُ/ عَلَى رَسُوْلِ اللّٰهِ/ صَلَى اللّٰهُ عَلَيْهِ وَسَلَّمَ،/ أَللَّهُمَّ إِيْمَانًابِكَ/ وَتَصْدِيْقًا بِكِتَابِكَ/ وَوَفَاءً بِعَهْدِكَ/ وَاتِّبَاعًا لِسُنَّةِ نَبِيِّكَ مُحَمَّدٍ/ صَلَى اللهُ عَلَيْهِ وَسَلَّمَ،/ إِنَّ الَّذِيْ/ فَرَضَ عَلَيْكَ الْقُرْآنَ/ لَرَآدُّوْكَ اِلَى مَعَادٍ،/ يَامُعِيْدُ/ اَعِدْنِيْ، يَاسَمِيْعُ/اَسۡمِعْنِيْ،/ يَاجَبَّارُ/ أُجْبُرْنِيْ،/ يَاسَتَّارُ/ اُسْتُرْنِيْ،/ يَارَحْمَنُ/ اِرْحَمْنِيْ،/ يَا رَدَّادُ/ اُرْدُدْنِيْ اِلَى بَيْتِكَ هَذَا/ وَرْزُقْنِيْ الْعَوْدَ/ ثُمَّ الْعَوْدَ/ كَرّاتٍ بَعْدَ مَرَّاتٍ/ تَاءِبُوْنَ/ عَابِدُوْنَ/ سَائِحُوْنَ/ لِرَبِّناَ حَامِدُوْنَ/ صَدَقَ اللّٰهُ وَعْدَهُ/ وَنَصَرَعَبْدَهُ/ وَهَزَمَ الْأَحْزَابَ وَحْدَهُ./ اللَّهُمَّ احْفَظْنِيْ/ عَنْ يَمِيْنِيْ/ وَعَنْ يَسَارِيْ/ وَمِنْ قُدَّامِيْ/ وَمِنْ وَرَاءِظَهْرِيْ/ وَمِنْ فَوْقِيْ/ وَمِنْ تَحْتِيْ،/ حَتَّى تُوَصِّلَنِيْ/ اِلَى اَهْلِيْ/ وَبَلَدِيْ./ اللَّهُمَّ هَوِّنْ عَلَيْنَا السَّفَرَ/ وَاطْوِلَنَا الْأَرْضَ،/ اللَّهُمَّ أَصْحِبْنَا/ فِيْ سَفَرِنَا/ وَخْلُفْنَا فِيْ أَهْلِهَا/ يَا أَرْحَمَ الرَّاحِمِيْنَ./ رَبَّنَا آتِنَا فِيْ الدُّنْيَا حَسَنَةً/ وَفِي الآ خِرَۃِ حَسَنَۃً/ وَقِنَا عَذَا بَ النَّارِ/ وَأَدْخِلْنَا الْجَنَّةَ مَعَ الْأَبْرَارِ/ يَاعَزِيْزُيَا غَفَارَيَارَبَّ الْعَالَمِيْنَ");
                varti.setText("Bismillaahi Allaahu akbar subhaanallaahi walhamdulillaahi wa laa ilaaha illallaahu wallaahu akbar. Wa laa haula wa laa quwwata illaa billaahil ‘aliyyil ‘adziim, wash shalaatu was salaam alaa rasulillaahi shallallaahu ‘alaihi wa aalihi, Allaahumma iimaanan bika wa tasdiiqan bi kitaabika wa wafaa-an bi ‘ahdika wattibaa-‘a lisunnati nabiyyika Muhammadin shalallahu ‘alaihi wa aalihi, innal ladzii faradha ‘alaika qur-aana laraadduuka ilaa ma’aadin ya mu’iidu a-‘idnii ya samii-u asmi’nii, yaa jabbar ujburnii ya sattar usturnii yaa rahmaanu irhamnii yaa raddaad urdudnii ilaa baitika hdzaa warzuqnil ‘auda tsummal ‘auda karaatin ba’da maraatin taa-ibuuna ‘aabiduuna saaihuuna lirabbinaa haamiduuna shadaqallaahu wa’dahuu wanashara ‘abdahuu wahazamal ahzaaba wahdahuu, Allaahumma fadznii an yamiinii wa ‘an yasaarii wa min quddaamii wa raidzohrii wamin fauqii wa min tahtii hattaa tuwashshilanii ilaa ahli wa baladi Allaahumma hawin ‘alainaa as safara watwi lanaa al-ardha, Allaahumma ashibnaa fii safarinaa wakhlufna fii ahlihaa yaa arhamar raahimiin, rabbanaa aatinaa fiddunyaa hasanatan wa fil aakhirati hasanatan wa qinaa ‘adzaaban naar wa adkhilnal jannata ma’al abraar, yaa ‘aziizu yaa ghaffaaru yaa rabbal ‘aalamiin");
                vaudio.setText("14 doa tawaf wada.mp3");
                rankDialog.show();
            }
        });
        txt_doa8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa Setelah Selesai Thawaf Wada");
                varab.setText("اللَّهُمَّ/ إِنَّ الْبَيْتَ/ بَيْتُكَ/ وَالْعَبْدَ/ عَبْدُكَ/ وَابْنُ عَبْدِكَ/ وَابْنُ أَمَتِكَ/ حَمَلْتَنِيْ/ عَلَى مَا سَخَّرْ تَنِيْ لِيْ/ مِنْ خَلْقِكَ/ حَتَّى سَيَّرْتَنِيْ فِيْ بِلَادِكَ/ وَبَلَغْتَنِيْ بِنِعْمَتِكَ/ حَتَّى اَعَنْتَنِيْ/ عَلَى قَضَاءِ مَنَاسِكَ،/ فَإِنْ كُنْتَ رَضِيْتَ عَنِّيْ/ فَازْدَدْ عَنِّيْ رِضًا/ وَاِلاَّ فَمُنَّ الآنَ/ قَبْلَ تَبَاعُدِيْ عَنْ بَيْتِكَ./ هَذَا أَوَانُ انْصِرَافِيْ/ إِنْ أَذِنْتَ لِيْ/ غَيْرَمُسْتَبْدِلِ بِكَ/ وَلابِبَيۡتِكَ/ وَلاَرَاغِبَ عَنْكَ/ وَلاَ عَنْ بَيْتِكَ./ اللَّهُمَّ اَصْحِبْنِي الۡعَافِيَةَ/ فِيْ بَدَنِيْ/ وَالْعِصْمَةَ فِيْ دِيْنِيْ/ وَحُسْنَ مُنْقَلَبِيْ/ وَارْزُقْنِيْ/ طَاعَتَكَ أَبَدًا مَا اَبْقَيْتَنِيْ/ وَاجْمَعْ لِيْ/ خَيْرَيِ الدُّنْيَاوَالْآخِرَةِ/ إِنَّكَ عَلَى كُلِّ شَيْءٍ قَدِيْرٌ./ اللَّهُمَّ لاَتَجْعَلْ/ هَذَا آخِرَالۡعَهْدِ بِبَيْتِكَ الْحَرَامِ/ وَإِنْ جَعَلْتَهُ آخِرَ الْعَهْدِ/ فَعَوِّضْنِيْ عَنْهُ الْجَنَّةَ/ بِرَحْمَتِكَ يَااَرْحَمَ الرَّحِمِيْنَ/ آمِيْنَ يَا رَبَّ الْعَالَمِيْنََ");
                varti.setText("Allahumma innal baita baituka wal ‘abda ‘abduka wabnu amatika hamaltanii ‘alaa ma sakhkhartanii lii min khalqika hattaa sayyartanii fii bilaadika wa balaghtanii bini’matika hatta a-’antanii ‘alaa qadhaa-l manaasikika fain kunta radhiita ‘annii fazdad ‘anni ridhan wa illaa famunnal ‘aanaqabla tabaa’udii ‘an baitika hadzaa awanunsirafii in adzinta lii ghaira mustabdilin bika walaa bi baitika walaa raaghibiin ‘anka walaa ‘an baitika, Allaahumma ashibnil ‘aafiyata fii badanii wal ‘ishmati fii diinii wa husnamunqalabii war zuqnii thaa’ataka Abadan maa abqaitanii wajma’lii khairayid dun-yaa wal aakhirah, innaka ‘aala kulli syai-in qaadir, Allaahumma la taj’al haadzal aakhiral ‘ahdibibaitikal haraam wa in ja’altahu aakhiral ‘ahdi fa’awwaidnii ‘anhul jannata birahmatika yaa arhamar raahimiin, amiin yaa rabbal ‘aalamiin");
                vaudio.setText("15 doa setelah selesai tawaf wada.mp3");
                rankDialog.show();
            }
        });

    }

    private void SetProgess(int no){
        if(no == 1){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_1));
            txtArab.setText("سُبْحَانَ اللّٰهِ/ وَالْحَمْدُلِلّٰهِ/ وَلاَاِلَهَ اِلاَّاللّٰهُ/ وَاللّٰهُ أَكْبَرُ،/ وَلاَحَوْلَ/ وَلاَقُوَّةَ/ اِلَّابِاللّٰهِ الْعَلِيِّ الْعَظِيْمِ،/ وَالصَّلَاةُ وَالسَّلَامُ/ عَلَى رَسُوْلِ اللّٰهِ/ صَلَى اللّٰهُ عَلَيْهِ وَسَلَّمَ،/ أَللَّهُمَّ إِيْمَانًابِكَ/ وَتَصْدِيْقًا بِكِتَابِكَ/ وَوَفَاءً بِعَهْدِكَ/ وَاتِّبَاعًا لِسُنَّةِ نَبِيِّكَ مُحَمَّدٍ/ صَلَى اللّٰهُ عَلَيْهِ وَسَلَّمَ،/ اَللَّهُمَّ إِنِّيْ/ أَسْأَلُكَالعَفْوَ/ وَالۡعَافِيَةَ/ وَالْمُعَافَاةَ الدَّائِمَةَ/ فِيْ الدِّيْنِ وَالدُّنْيَا وَالْآخِرَةِ/ وَالْفَوْزَبِالْجَنَّةِ/ وَالنَّجَاةَ مِنَالنَّارِ");
            txtArti.setText("Subhaanallaah walhamdu lillaah walaa ilaaha illallaahu wallaahu akbar, walaa haula walaa quwwata illaa billaahi ‘aliyyil ‘azhiim, wash shalaatu was salaamu ‘alaa rasuulillaahi shalallaahu ‘alaihi wa aalihi, Allaahumma iimaanan bika wa tatsdiiqan bikitaabika wa wafaa an bi’ahdika wat tibaa-an lisunnati nabiyyika Muhammadin shalallaahu ‘aalaihi wa salam, Allaahumma innii as-‘aluka afwa wal ‘aafiyata wal mu’aafatad daa’imata fid diini wad dunaa wal aakhirati wal fauza bil jannati wan najaata minan naar");
        }else if(no == 2){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_2));
            txtArab.setText("اللَّهُمَّ إِنِّ هَذَا الْبَيْتَ بَيْتُكَ/ وَالْحَرَمَ حَرَمُكَ/ وَالْأَمْنُكَ/ وَالْعَبْدَ عَبْدُكَ/ وَأَنَاعَبْدُكَ/ وَابْنُ عَبْدِكَ/ وَهَذَامَقَامُ العَائِذِبِكَ مِنَ النَّارِ/ فَحَرَّمْ لُحُوْمَنَا/ وَبَشَرَتَنَاعَلَى النَّارِ،/ اللَّهُمَّ حَبِّبْ إِلَيْنَا الْإِيْمَانَ/ وَزَيِّنْهُ فِيْ قُلُوْبِنَا/ وَكَرِّهْ إِلَيْنَاالۡكُفْرَ/ وَالْفُسُوْقَ/ وَالْعِصْيَانَ/ وَاجْعَلْنَا مِنَ الرَّاشِدِيْنَ،/ اللَّهُمَّ قِنِيْ عَذَابَكَ/ يَوْمَ تَبْعَثُ عِبَادَكَ،/ اللَّهُمَّ ارْزُقْنِيۡ الۡجَنَّةَ بِغَيْرِ حِسَاب");
            txtArti.setText("Allaahumma inna hadzal baita baituka wal harama haramuka wal amna amnuka wal ‘abda ‘abduka wa anaa ‘abduka wabnu ‘abdika wa haadzaa maqaamul ‘aa-idzi bika minan naar, faharrim luhuumanaa wa basyaratanaa ‘alan naar, Allaahumma habib ilainal iimaana wa zayyinhu fi quluubina wa karrih ilainal kufra wal fusuuqa wal ishyaana waj’alnaa minar raasyidiin, Allaahumma qinii ‘adzaabaka, yauma tab ‘atsu ‘ibaadaka, Allaahummarzuqni al jannata bighairi hisaab");
        }else if(no == 3){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_3));
            txtArab.setText("اللَّهُمَّ إِنِّيْ أَعُوْذُبِكَ/ مِنَ الشَّكِّ/ وَالشِّرْكِ/ وَاشَّقَاقِ/ وَالنِّفَاقِ/ وَسُوْءِالْأَخْلَاقِ/ وَسُوْءِالْمَنْظَرِ/ وَالْمُنْقَلَبُ فِيْ الْمَالِ وَالْأَهْلِ وَالْوَلَدِ،/ اللَّهُمَّ إِنِّيْ أَسْأَلُكَ/ رِضَاكَ وَالْجَنَّةَ/ وَأَعُوْذُبِكَ/ مِنْ سَخَطِكَ وَالنَّارِ،/ أَللَّهُمَّ إِنَّيْ أَعُوْذُبِكَ/ مِنْ فِتْنَةِ الْقَبْرِ/ وَأَعُوْذُبِكَ مِنْ فِتْنَةِ الْمَحْيَا وَالْمَمَاتِ");
            txtArti.setText("Allaahumma innii a-‘uudzu bika minasy syakki wasy syirki wasy syiqaaqi wan nifaaqi wa suu’il akhlaaqi was su’li manzhari wal munqalabi fil maali wal ahli wal waladi. Allahumma innii as’aluka ridhaaka wal jannata wa a-‘uudzu bika min sakhatika wan naar, Allaahumma innii a-’uudzu bika min fitnatil mahyaa wal mamaati");
        }else if(no == 4){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_4));
            txtArab.setText("اللَّهُمَّ اجْعَلْهُ حَجَّا مَبْرُوْرًا/ وَسَعْيًا مَشْكُوْرًا/ وَذَنْبًا مَغْفُوْرًا/ وَعَمَلًاصَالِحًا مَقْبُوْلاً/ وَتِجَارَةً لَنْ تَبُوْرًا،/ يَاعَالِمَ مَافِيْ الصُّدُوْرِ/ أَخْرِجْنِيْ يَا أَللّٰهُ/ مِنَ الظُّلُمَاتِ إِلَى النُّوْرِ،/ أَللَّهُمَّ إِنِّيْ أَسْأَلُكَ/ مُوْجِبَاتِ رَحْمَتِكَ/ وَعَزَائِمَ مَغْفِرَتِكَ/ وَالسَّلَامَةَ مِنْ كُلِّ إِثْمٍ/ وَالْغَنِيْمَةَ مِنْ كُلِّ بِرِّ/ وَالْفَوْزَبِالۡجَنَّةِ/ وَالنَّجَاةَ مِنَ النَّارِ،/ رَبِّ قَنِّعْنِيْ بِمَا رَزَقْتَنِيْ/ وَبَارِكْ لِىْ فِيْمَا أَعْطَيْتَنِيْ/ وَاخْلُفْ عَلَيَّ/ كُلَّ غَائِبَةٍ لِيْ مِنْكَ بِخَيْرٍٍ");
            txtArti.setText("Allaahummaj’alhu hajjam mabruuraw was sa’yam masykuuraw wa zdanbam maghfuuraw wa ‘amaalan shaaliham maghbuulaw watijaawatal lantabuura, ya ‘aliimu maa fish-shuduuri akhrijnii ya Allahu munazh-zhulumaati ilan nuuri, Allahumma inii as-aluka muujibaat rahmatika wa ‘azaa-ima maghfiratika wa salaamata min kulli itsmin wal ghaniimata min kulli birrin wal fauza biljannati wan najaati minan naar, rabbi qanni’nii bimaa razaqtanii wa baariklii fiima a’thaitanii wakhluf ‘alayya kulla gha’ibatil lii minka bikhairin");
        }else if(no == 5){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_5));
            txtArab.setText("اللَّهُمَّ أَظِلَنِيْ/ تَحْتَ ظِلِّ عَرْشِكَ/ يَوْمَ لاَظِلَّ اِلاَّظِلُّكَ/ وَلاَبَاقِيَ إِلاَّوَجْهُكَ/ وَأَسْقِنِيْ مِنْ حَوْضِ نَبِيِّكَ/ مُحَمَّدٍ صَلَى اللّٰهُ عَلَيْهِ وَسَلَمَ،/ شُرْبَةً هَنِيْئَةً مَرِيْئَةً/ لاَأَظْمَأُبَعْدَهَا اَبَدًا،/ اللَّهُمَّ إِنِّيْ/ أَسْأَلُكَ مِنْ خَيْرِ/ مَا أَسْأَلُكَ مِنْهُ نَبِيُّكَ مُحَمَّدٌ/ صَلَى اللّٰهُ عَلَيْهِ وَسَلَّمَ/ وَأَعُوْذُبِكَ/ مِنْ شَرِّ/ مَا اسْتَعَاذَكَ مِنْهُ نَبِيِّكَ مُحَمَّدٌ/ صَلَى اللّٰهُ عَلَيْهِ وَسَلَّمَ/ اللَّهُمَّ أِنِّيْ/ أَسْأَلُكَ الْجَنَّةَ/ وَنَعِيْمَهَا/ وَمَا يُقَرِّبُنِيْ اِلَيْهَا/ مِنْ قَوْلٍ أَوْفِعْلٍ أَوْعَمَلٍ/ وَأَعُوْذُبِكَ مِنَ النَّارِ/ وَمَا يُقَرِّبُنِيْ اِلَيْهَا/ مِنْ قَوْلٍ اَوْ فِعْلٍ اَوۡعَمَالٍ");
            txtArti.setText("Allaahumma azhillanii tahta zhilli ‘arsyika yauma laa zhilli ila zhilluka walaa baaqiya illaa wajhuka wa asqinii min haudhi nabiyyika Muhammadin shalallaahu ‘alaihi wa sallam syurbatan hanii-atan marii-atan laa azma-u ba’dahaa abadaa, Allaahumma innii as-aluka min khairi maa as-alaka minhu nabiyyuka Muhammadun shalallaahu ‘alaihi wa aalihi, wa a-‘uudzu bika min syarri mas ta’aadzaka minhu nabiyyuka Muhammadun Shalallaahu ‘alaihi wa salama, Allaahumma inni as-alukal jannata wana’iimaha wamma yuqarribunii ilaihaa min qaulin au fi’lin au ‘amalin wa a’uudzu bika minan naari wamaa yuqarribunii ilaihaa min qaulin au fi’lin au ‘amalin");
        }else if(no == 6){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_6));
            txtArab.setText("اللَّهُمَّ إِنَّ لَكَ عَلَيَّ/ حُقُوْقًا كَثِيْرَةً/ فِيْمَا بَيْنِيْ وَبَيْنَكَ/ وَحُقُوْقًا كَثِيْرَةً/ فِيْمَا بَيْنِيْ وَبَيْنَ خَلْقِكَ/ اللَّهُمَّ مَا كَانَ لَكَ مِنْهَا/ فَاغْفِرْهُ لِيْ/ وَمَا كَانَ لِخَلْقِكَ/ فَتَحَمَّلْهُ عَنِّيْ/ وَأَغْنِنِيْ بِحَلَا لِكَ/ عَنْ حَرَامِكَ/ وَبِطَاعَتِكَ/ عَنْ مَعْصِيَتِكَ/ وَبِفَضْلِكَ/ عَمَّنْ سِوَاكَ/ يَا وَاسِعَ الْمَغْفِرَةِ/ اللَّهُمَّ/ إِنَّ بَيْتَكَ عَظِيْمٌ،/ وَوَجْهَكَ كَرِيْمٌ،/ وَأَنْتَ يَاَاللّٰهُ حَلِيْمٌ كَرِيْمٌ عَظِيْمٌ/ تُحِبُّ الۡعَفْوَ فَاعْفُ عَنِّيْ");
            txtArti.setText("Allaahumma inna laka ‘alayya huquuqan katsiiratan fiiman baini wa bainaka, wa huquqan katsiratan, fiima bainii wa baina khalqika, Allaahumma maa kaana laka minhaa faghfirhu lii wa maa kaana likhalqika fatahammalhu ‘anni wa aghninii bihalaalika ‘an haraamika wa bithaa’atika ‘an ma’siyatika wa bifadhlika ‘amman siwaaka yaa waasi’al maghfirah. Allaahumma inna baitaka azhiim, wa wajhaka kariim, wa anta yaa Allaahu haliimun kariimun ‘azhiimun tuhibbul ‘afwa fa’fu ‘annii");
        }else if(no == 7){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_7));
            txtArab.setText("اللَّهُمَّ إِنِّيْ/ أَسْأَلُكَ إِيْمَانًا كَامِلًا/ وَيَقِيْنًا صَادِقًا/ وَرِزْقًا وَاسِعًا/ وَقَلْبًا خَا شِعًا/ وَلِسَانًا ذَاكِرًا/ وَحَلَالاً طَيِّبًا/ وَتَوْبَةً نَصُوْحًا/ وَتَوْبَةً قَبْلَ الْمَوْتِ/ وَرَاحْةً عِنْدَ الْمَوْتِ/ وَمَغْفِرَةً وَرَحْمَةً بَعْدَ الْمَوْتِ/ وَالْعَفْوَ عِنْدَ الْحِسَابِ/ وَالْفَوْزَبِالْجَنَّةِ/ وَالنَّجَاةَ مِنَ النَّارِ،/ بِرَحْمَتِكَ يَا عَزِيْزُ/ يَا غَفَارُ،/ رَبِّ زِدْنِيْ عِلْمًا/ وَأَلْحِقْنِيْ بِاالصَّا لِحِيْنَ");
            txtArti.setText("Allaahumma innii as-aluka iimaanan kaamilan wa yaqiinan shaadiqan wa rizqan waa-si’an wa qalban khaasyi’an wa lisaanan dzaakiran wa halaalan thayyiban wa taubatan nashuuhahan, wa taubatan qablal mauti, wa rahmatan ‘indal mauti, wa maghfiratan wa rahmatan ba’dal mauti, wal ‘afwa ‘indal hisaab, wal fauza bil jannati wan najaati minan naar, birahmatika yaa ‘aziizu, yaa ghafaar, rabbi zidnii ‘ilaman wa alhiqnii bish-shaalihiin");
        }else{
            progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_0));
            txtArab.setText("");
            txtArti.setText("");
        }

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
        if(play.equals("Pause")){
        txt_play.setText("Mainkan");
        img_play.setImageDrawable(getResources().getDrawable(R.drawable.play));
        }
        if(play2.equals("Pause")) {
            img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
            v_play.setText("Mainkan");
        }
        Toast.makeText(getApplicationContext(), "Selesai", Toast.LENGTH_LONG).show();
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

}
