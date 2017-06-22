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


public class sai extends AppCompatActivity implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private SQLiteHandler db;
    private SessionManager session;

    private SQLiteDatabase database;

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private TextView state,txt_play,txt_back,txt_next,
            circle,txtArab,txtArti,txt_doa1,txt_doa2,txt_doa3,txt_doa4,txt_doa5,
            vname,varab,varti,vaudio,v_play;
    ImageView img_back,img_next,progress,img_play,arrow_back,img_vplay;
    String srcPath = null;
    enum MP_State {
        Idle, Initialized, Prepared, Started, Paused,
        Stopped, PlaybackCompleted, End, Error, Preparing}

    sai.MP_State mediaPlayerState;
    MediaPlayer mp;
    ProgressDialog pd;
    private String id,uid;
    private SeekBar timeLine;
    LinearLayout timeFrame;
    TextView timePos, timeDur;
    final static int RQS_OPEN_AUDIO_MP3 = 1;
    MediaPlayer mediaPlayer;
    LinearLayout menu_play,menu_next,menu_back,judul,isi,vplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sai);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        //tracker
        AppController application = (AppController) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        // [START screen_view_hit]
        mTracker.setScreenName("Sai");
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
//        startService(new Intent(sai.this, BackgroundService.class));
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

                srcPath="/sdcard/android/data/salam.gohajj.id/sai/"+ids+".mp3";
                File cek = new File(srcPath);
                if (!cek.exists()) {
                    srcPath=AppConfig.URL_HOME+"/uploads/panduan/sai/"+ids+".mp3";
                }

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
                    if(mp.isPlaying()){
                        mp.pause();
                    }
                    img_play.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    txt_play.setText("Mainkan");
                }else if(play.equals("Mainkan")) {
                    if (!cek_status(getApplicationContext()))
                    {
                        Toast.makeText(sai.this,
                                "Mohon Cek Koneksi Anda",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        stopPlaying();
                        try {
                            pd = new ProgressDialog(sai.this);
                            pd.setMessage("Mempersiapkan Audio.....");
                            pd.show();
                            mp = new MediaPlayer();
                            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mp.setOnPreparedListener(sai.this);
                            mp.setOnErrorListener(sai.this);
                            mp.setDataSource(srcPath);
                            mp.prepareAsync();
                            mp.setOnCompletionListener(sai.this);
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
                    stopPlaying(); txt_play.setText("Mainkan");
                img_play.setImageDrawable(getResources().getDrawable(R.drawable.play));
                final String ids = circle.getText().toString();
                if(Integer.parseInt(ids) > 1){
                    int no= Integer.parseInt(ids) - 1;  circle.setText(""+no+""); SetProgess(no);
                }else{
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
                    stopPlaying(); txt_play.setText("Mainkan");
                img_play.setImageDrawable(getResources().getDrawable(R.drawable.play));
                final String ids = circle.getText().toString();
                if(Integer.parseInt(ids) < 7){
                    int no= Integer.parseInt(ids) + 1;  circle.setText(""+no+""); SetProgess(no);
                }else{

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

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(sai.this);
                    alertDialogBuilder.setMessage("Ibadah Sa'i sudah selesai, silakan melanjutkan Tahallul");
                            alertDialogBuilder.setPositiveButton("Ya",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
//                                            finish();
                                        }
                                    });

//                    alertDialogBuilder.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                }
            }
        });

        //popup
        final Dialog rankDialog = new Dialog(sai.this);
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
                srcPath="/sdcard/android/data/salam.gohajj.id/sai/"+ids;
                File cek = new File(srcPath);
                if (!cek.exists()) {
                    srcPath=AppConfig.URL_HOME+"/uploads/panduan/sai/"+ids;
                }

                if(play.equals("Pause")) {
                    if(mp.isPlaying()){
                        mp.pause();
                    }
                    img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    v_play.setText("Mainkan");
                }else if(play.equals("Mainkan")) {
                    if (!cek_status(getApplicationContext()))
                    {
                        Toast.makeText(sai.this,
                                "Mohon Cek Koneksi Anda",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        stopPlaying();
                        try {
                            pd = new ProgressDialog(sai.this);
                            pd.setMessage("Mempersiapkan Audio...");
                            pd.show();
                            mp = new MediaPlayer();
                            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mp.setOnPreparedListener(sai.this);
                            mp.setOnErrorListener(sai.this);
                            mp.setDataSource(srcPath);
                            mp.prepareAsync();
                            mp.setOnCompletionListener(sai.this);
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
                vname.setText("Doa Mendaki Bukit Safa");
                varab.setText("بِسْمِ اللّٰهُ الرَّحْمَنِ الرَّحِيْمِ أَبْدَأُ/ بِمَا بَدَأَ اللّٰهُ بِهِ وَرَسُوْلُهُ/ إِنَّ الصَّفَا وَالْمَرْوَةَ/ مِنْ شَعَائؤِرِ اللّٰهِ/فَمَنْ حَجَّ الْبَيْتَ/ أَوِعْتَمَرَ/ فَلَا جُنَاحَ عَلَيْهِ/ أَنْ يَّطَّوَّفَ بِهِمَا/ وَمَنْ تَطَوَّعَ خَيْرًا/ فَإِنَّ اللهَ شَاكِرٌعَلِيْمٌ");
                varti.setText("Bismillaahir rahmaani rahiim, abda’u bimaa bada-allaahu bihii wa rasuuluhuu, innash shafaa wal marwata min sya-‘aa-irillaah, faman hajjal baita awi’tamara falaa junaaha ‘alaihi ayyatthawwafa bihimma wa man tathawwa’a khairan fainnallaaha syaakirun ‘aliimun");
                vaudio.setText("8 doa mendaki bukit safa.mp3");
                rankDialog.show();
            }
        });

        txt_doa2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa Saat di Bukit Safa");
                varab.setText("اَللّٰهُ اَكْبَرُ،/ اَللّٰهُ اَكْبَرُ،/ اَللّٰهُ اَكْبَرُ،/ وَلِلّٰهِ الْحَمْدُ،/ اَللّٰهُ اَكْبَرُ/ عَلَى مَا هَدَانَا/ وَالْحَمْدُ لِلّٰهِ/ عَلَى مَا أَوْلَانَا،/ لَااِلَهَ اِلَّاللّٰهُ وَحْدَهُ/ لَاشَرِيْكَ لَهُ،/ لَهُ الْمُلْكُ/ وَلَهُ الْحَمْدُ/ يُحْيِيْ وَيُمِيْتُ/ بِيَهْدِهِ الْخَيْرُ/ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيْرٌ،/ لَااِلَهَ اِلَّاللّٰهُ وَحْدَهُ/ لَاشَرِيْكَ لَهُ/ أَنْجَزَ وَعْدَهُ/ وَنَصَرَعَبْدَهُ/ وَهَزَمَ الْأَحْزَابَ وَحْدَهُ،/ لَااِلَهَ اِلَّااللّٰهُ/ وَلَا نَعْبُدُ اِلَّا إِيَّاهُ/ مُخْلِصِيْنَ لَهُ ادِّيْنَ/ وَلَوْ كَرِهَ الْكَافِرُوْنَ ");
                varti.setText("Allaahu Akbar, Allaahu Akbar, Allaahu Akbar, walillaahil hamdu, Allaahu Akbar ‘alaa maa hadaana walhamdulillaahi ‘ala maa aulaanaa, laa ilaaha illallaahu wahdahuu la syariikalahuu, lahul mulku wa lahul hamdu yuhyii wa yumiitu biyadihil khairu wa huwa ‘alaa kulli syai-in qadiir, laa ilaaha illallaahu wahdahuu la syariikalahuu anjaza wa’dahuu wa nashara ‘abdahuu wa hazamal ahzaaba wahdahuu laa ilaaha illallaahu walaa na’budu illa iyyahu mukhlishiina lahuddiina walau karihal kaafiruun");
                vaudio.setText("11 doa saat dibukit safa.mp3");
                rankDialog.show();
            }
        });
        txt_doa3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa di Antara 2 Pilar Hijau");
                varab.setText("رَبِّ اغْفِرْوَارْحَمْ/ وَاعْفُ وَتَكَرَّمْ/ وَتَجَاوَزْعَمَّا تَعْلَمُ/ إِنَّكَ تَعْلَمُ مَا لَانَعْلَمُ/ إِنَّكَ أَنْتَ اللّٰهُ الْأَعَزُّالْأَكْرَمُ");
                varti.setText("Rabbighfir warham wa’fu wa takarram ‘amma ta’lam, innaka ta’lamu maa laa na’lamu, innakallahul a-‘azzul akramu");
                vaudio.setText("10 doa diantara 2 pilar hijau.mp3");
                rankDialog.show();
            }
        });
        txt_doa4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa Ketika Mendekati Bukit Safa atau Marwah");
                varab.setText("إِنَّ الصَّفَا وَالْمَرْوَةَ/ مِنْ شَعَائِرِ اللّٰهِ/ فَمَنْ حَجَّ الْبَيْتَ/ أَوِاعْتَمَرَ/ فَلَا جُنَاحَ عَلَيْهِ/ أَنْ يَّطَّوَّفَ بِهِمَا/ وَمَنْ تَطَوَّعَ خَيْرًا/ فَإِنَّ اللهَ شَاكِرٌعَلِيْمٌ");
                varti.setText("Innash shafaa wal marwata min sya-‘aa-irillaah, faman hajjal baita awi’tamara falaa junaaha ‘alaihi ayyathowwafa bihimma wa man tathawwa’a khairan fainnallaaha syaakirun ‘aliimun");
                vaudio.setText("9 doa ketika mendekati bukit safa marwah.mp3");
                rankDialog.show();
            }
        });
        txt_doa5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa Selesai Sai");
                varab.setText("اللَّهُمَّ رَبَّنَا/ تَقَبَّلْ مِنَا/ وَعَافِنَا وَاعْفُ عَنَّا/ وَعَلَى طَاعَتِكَ وَشُكْرِكَ أَعِنَّا/ وَعَلَى غَيْرِكَ/ لَاتَكِلْنَا/ وَعَلَى الإِيْمَانِ وَالإِسْلَامِ الكَامِلِ جَمِيْعًا/ تَوَفَّنَا وَأَنْتَ رَاضٍ عَنَّا/ اللَّهُمَّ ارْحَمْنِيْ/ بِتَرْكِ المَعَاصِيْ/ أَبَدًامَاأَبْقَيْتَنِيْ/ وَارْحَمْنِيْ/ اَنْ اَتَكَلَّفَ مَا لَايَعْنِيْنِيْ/ وَارْزُقْنِيْ حُسْنَ النَّظَرِ/ فِيْمَا يُرْضِيْكَ عَنِّيْ/ يَااَرْحَمَ الرَّاحِمِيْنَ");
                varti.setText("Allaahumma rabbanaa taqabbal minnaa wa’aafinaa wa’fu ‘annaa wa ‘alaa thaa-‘atika wa syukrika a’innaa wa ‘alaa ghairika laa takilnaa wa ‘alal iimaani wal islaami kaamili jamii’an tawaffanaa wa anta raadhin ‘annaa, Allaahummarhamnii bitarki ma’aashii abadam maa abqaitanii warhamnii warzuqnii husnan nazhari fii maa yurdhiika ‘annii ya arhamar raahimiin");
                vaudio.setText("12 doa selesai sa'i.mp3");
                rankDialog.show();
            }
        });

    }

    private void SetProgess(int no){
        if(no == 1){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_1));
            txtArab.setText("اللّٰهُ اَكْبَرُ/ اللّٰهُ اَكْبَرُاللّٰهُ/ اَكْبَرُ اللّٰهُ اَكْبَرُ كَبِيْرًا وَالْحَمْدُلِلّٰهِ كَثِيْرًا/ وَسُبْحَانَ اللّٰهِ الْعَظِيْمِ/ وَبِحَمْدِهِ الْكَرِيْمِ/ بُكْرَةً وَأَصِيْلًا/ وَمِنَ اللَّيْلِ فَاسْجُدْ لَهُ/ وَسَبِّحْهُ لَيْلًا طَوِيْلًا/ لَااِلَهَ اِلاَاللّٰهُ وَحْدَهُ/ أَنْجَزَ وَعْدَهُ/ وَنَصَرَ عَبْدَهُ/ وَهَزَمَ الْأَحْزَابَ وَحْدَهُ/ لاَشَيْءَ قَبْلَهُ وَلاَ بَعْدَهُ/ يُحْيِيْ وَيُمِيْتُ/ وَهُوَ حَيٌّ دَائِمً/ لاَيَمُوْتُ وَلاَيَفُوْتُ أَبَدًا/ بِيَهْدِهِ الْخَيْرُ/ وَاِلَيْهِ الْمَصِيْرُ/ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيْرُ");
            txtArti.setText("Allaahu akbar Allaahu akbar Allaahu akbar Allaahu akbar kabiraw walhamdulillaahi katsiiraw wa subhaanallaahil ‘adziimi wa bihamdihil kariim bukrataw wa ashiilan wa minal laili fasjud lahuu wa sabbihhu lailan thawiilan laa ilaaha illaahu wahdahuu anjaza wa’dahuu wa nashara ‘abdahuu wa hazamal ahzaaba wahdahuu laa syai-a qablahuu wa laa ba’dahuu yuhyii wa yumiitu wahuwa hayyun daa-imun laa yamuutu wa laa yafuutu Abadan biyadihil khairu wa ilaihil mashiir wa huwa alaa kulli syai-in qadiir");
        }else if(no == 2){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_2));
            txtArab.setText("اللّٰهُ اَكْبَرُ/ اللّٰهُ اَكْبَرُ/ اللّٰهُ اَكْبَرُ،/ وَلِلّٰهِ الْحَمْدُ،/ لاَاِلَهَ اِلاَّاللّٰهُ/ الوَاحِدُ الْفَرْدُ الصَّمَدُ،/ الَّذِيْ لَمْ يَتَّخِذْ صَاحِبَةً/ وَلاَوَلَدًا/ وَلَمْ يَكُنْ لَهُ/ شَرِيْكٌ فِيْ الْمُلْكِ/ وَلَمْ يَكُنْ لَهُ/ وَلِيٌّ مِنَ الذُّلِّ/ وَكَبِّرْهُ تَكْبِيْرًا،/ اللَّهُمَّ/ إِنَّكَ قُلْتَ فِيْ كِتَابِكَ المُنْزَلِ/ أُدْعُوْنِيْ اَسْتَجِبْ لَكُمْ/ دَعَوْنَكَ/ رَبَّنَا فَاغْفِرْلَنَا/ كَمَا أَمَرْتَنَا/ إِنَّكَ لاَتُخْلِفُ الْمِيْعَادَ،/ رَبَّنَا/ إِنَّنَا سَمِعْنَا مُنَادِيًا/ يُنَادِيْ لِلْإِيْمَانِ/ أَنْ آمِنُوْابِرَبِّكُمْ فَآمَنَّا/ رَبَّنَافَاغْفِرْلَنَا ذُنُوْبَنَا/ وَكَفِّرْ عَنَّا سَيِّئَاتِنَا/ وَتَوَفَّنَا مَعَ الْأَبْرَارِ/ رَبَّنَاوَآتِنَا/ مَاوَعَدْتَنَاعَلَى رَسُلِكَ/ وَلاَتُخْزِنَا يَوْمَ الْقِيَامَةِ،/ إِنَّكَ لاَتُخْلِفُ الْمِيْعَادَ،/ رَبَّنَا عَلَيْكَ تَوَكَّلْنَا/ وَاِلَيْكَ أَنَبْنَا/ وَاِلَيْكَ الْمَصِيْرُ/ رَبَّنَا اغْفِرْلَنَا/ وَلِإِخْوَانِنَا الَّذِيْنَ سَبَقُوْنَابِالْإِيْمَاِن/ وَلاَتَجْعَلْ فِيْ قُلُوْبِنَا/ غِلَّا لِلَّذِيْنَ أَمَنُوْا/ رَبَّنَا إِنَّكَ رَأُوْفُ الرَّحِيْمِ");
            txtArti.setText("Allaahu Akbar, Allaahu Akbar, Allaahu akbar, wa lillaahil hamdu, laa ilaaha illallaahul waahidul fardhus shamad, alladzii lam yattakhidz shaahibataw wa laa waladaw wa lam yakul lahuu syariikun fil mulki wa lam yakul lahuu waliyyunm minadz dzulli wa kabbirhu takbiiran, Allaahumma innaka qulta fii kitaabikal minzali ud-‘uunii astajib lakum, da-aunaaka rabbanaa faghfirlanaa kamaa amartanaa innaka laa tukhliful mii’aad, rabbanaa innanaa sami’naa munaadiyay yunaadii lil iimaani an aaminuu birabbikum fa aamannaa, rabbanaa faghfirlanaa dzunuubanaa wa kaffir ‘annaa sayyiaatinaa wa tawaffanaa ma’al abraar, rabbanaa wa aatinaa maa wa’attinaa ‘alaa rusulika wa laa tukhzinaa yaumal qiyaamati innaka laa tukhliful mii’aad,rabbanaa ‘alaika tawakkalnaa wa ilaika anabnaa wa ilaikal mashiir, rabbanaghfirlanaa wa li ikhwaaninal ladziina sabaquunaa bil iimaan wa laa taj’al fii quluubinaa ghillal lillaadziina aamanuu rabbanaa innaka ra’uufur rahiim");
        }else if(no == 3){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_3));
            txtArab.setText("اللّٰهُ اَكْبَرُ/ اَللّٰهُ اَكْبَرُ/ اَللّٰهُ اَكْبَرُ/ وَلِلّٰهِ الْحَمْدُ،/ رَبَّنَا اَتْمِمْ لَنَا نُوْرَنَا/ وَغْفِرْلَنَا/ إِنَّكَ عَلَى كُلِّ شَيْئٍ قَدِيْرٌ،/ اللَّهُمَّ إِنِّيْ/ أَسْأَلُكَ الْخَيْرَ كُلَّهُ/ عَاجَلَهُ وَ آجْلَهُ/ وَأَسْتَغْفِرُكَ لِذَنْبِيْ/ وَأَسْأَلُكَ رَحْمَتَكَ/ يَا أَرْحَمَ الرَّاحِمِيْنَ");
            txtArti.setText("Allaahu Akbar, Allaahu Akbar, Allaahu Akbar, wa lillaahilhamd. Rabbanaa atmim lanaa nuuranaa waghfirlanaa innaka ‘alaa kuli syai’ingqadiir. Allaahumma inni as’alukalkhaira kullahu ‘aajilahu wa’aajilahu wastaghfiruka lidzambii wa as’aluka rahmataka yaa arhamarraahimiin");
        }else if(no == 4){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_4));
            txtArab.setText("اللّٰهُ اَكْبَرُ/ اَللّٰهُ اَكْبَرُ/ اَللّٰهُ اَكْبَرُ،/ وَلِلّٰهِ الْحَمْدُ،/ اللَّهُمَّ إِنِّيْ أَسْأَلُكَ/ مِنْ خَيْرٍ مَا تَعْلَمُ/ وَأَعُوْذُبِكَ/ مِنْ شَرِّ مَا تَعْلَمُ/ وَأَسْتَغْفِرُكَ/ مِنْ كُلِّ مَا تَعْلَمُ/ إِنَّكَ أَنْتَ عَلَّامُ الْغُيُوْبِ،/ لاَاِلَهَ اِلَّااللهُ/ اَلْمَلِكَ الْحَقُّ الْمُئْمِيْنُ،/ مُحَمَّدٌ رَسُوْلُ اللّٰهِ/ صَادَقَ الْوَعْدِ الْأَمِيْنِ،/ اللَّهُمَّ إِنِّيْ أَسْأَلُكَ/ كَمَا هَدَيْتَنِيْ لِلْإِسْلَامِ/ اَنْ لاَ تَنْزِعَهُ مِنِّيْ/ حَتَّى تَوَفِّنِيْ/ وَأَنَا مُسْلِمٌ،/ اللَّهُمَّ اجْعَلْ فِيْ قَلْبِيْ نُوْرًا،/ وَفِيْ سَمْعِيْ نُوْرًا،/ وَفِيْ بَصْرِيْ نُوْرًا،/ اللَّهُمَّ اشْرَحْ لِيْ صَدْرِيْ/ وَيَسِّرْلِيْ أَمْرِيْ/ وَأَعُوْذُبِكَ/ مِنْ وَسَا وَسِ الْصَدْرِ/ وَشَتَاتِ الْأَمْرِ/ وَفِتْنَةِ الْقَبْرِ،/ اللَّهُمَّ إِنِّيْ أَعُوْذُبِكَ/ مِنْ شَرِّمَا يَلِجُ فِيْ اللَّيْلِ/ وَشَرِّمَا يَلِجُ فِيْ النَّهَارِ/وَمِنْ شَرِّمَا تَهُبُّ بِهِ الرِّيَاحُ/ يَا اَرْحَمَ الرَّاحِمِيْنَ،/ سُبْحَنَكَ/ مَا عِبَادَتِاكَ/ حَقَّ عِبَادَتِكَ يَااَللّٰهُ،/ سُبْحَانَكَ/ مَاذَكَرْنَاكَ/ حَقَّ ذِكْرِكَ يَا اَللّٰهُُ");
            txtArti.setText("Allaahu Akbar, Allaahu Akbar, Allaahu Akbar, wa lillaahil hamdu, Allaahumma innii as-aluka min khairim maa ta’lamu wa a-‘uudzu bika min syarri maa ta’lamu wa astghfiruka min kulli maa ta’lamu innaka anta ‘allaamul ghuyub, laa ilaaha illallaahul malikul haqqul mubiin Muhammadur rasuulullaah, shaadiqul wa’dilamiin, Allaahumma innii as-aluka kamma hadaitanii lil islaami an laa tunzi ‘anhu minnii hattaa tawaffanii wa anaa muslim. Allaahummaj-‘al fi qalbi nuuran, wa fii sam’ii nuuran wa fii basharii nuuran, Allaahummasyraah lii shadrii wa yassir lii amrii wa a-‘uudzubika min wasaawisis shadri wasyasyattaatil amri wafitnatil qabrii, Allaahumma innii a-‘uudzubika min syarri maa yaliju fillaili wa syarri maa yaliju fin nahaari wa min syarri maa tahubbu bihir riyaahu yaa arhamar raahimiin, subhaanaka maa ‘abadnaaka haqqa ‘ibaadatika yaa Allaahu subhaanaka maa dzakarnaaka haqqa dzikrika yaa Allaah");
        }else if(no == 5){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_5));
            txtArab.setText("َللّٰهُ اَكْبَرُ/ اَللهُ اَكْبَرُ/ اَللّٰهُ اضكْبَرُ،/ وَلِلّٰهِ الْحَمْدُ،/ سُبْحَانَكَ مَا شَكَرْنَاكَ/ حَقَّ شُكْرِكَ يَا اَللّٰهُ/ سُبْحَانَكَ/ مَا أَعْلَى شَأْنَكَ يَا اَللّٰهُ،/ اللَّهُمَّ/ حَبِّبْ اِلَيْنَا الْإِيْمَانَ/ وَزَيِّنْهُ فِيْ قُلُوْبِنَا/ وَكَرِّهْ إِلَيْنَا الْكُفْرَ/ وَالْفُسُوْقَ/ وَالْعِصْيَانَ،/ وَاجْعَلْنَا مِنَ الرَّاشِدِيْنَ");
            txtArti.setText("Allaahu Akbar, Allaahu Akbar, Allaahu Akbar, walillaahil hamdu, subhaanaka maa syakarnaaka haqqa syukrika ya Allaah, Subhaanaka maa a’laa sya’naka yaa Allaah, Allaahumma habbib ilainal iimaana wa zayyinhu fii quluubinaa wa karrih ilainal kufra wal fusuuqa wal ‘ishyaana, waj-‘alnaa minar raasyidiin");
        }else if(no == 6){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_6));
            txtArab.setText("َاَللّٰهُ اَكْبَرُ/ اَللّٰهُ اَكْبَرُ/ اَللّٰهُ اَكْبَرُ،/ وَلِلّٰهِ الْحَمْدُ،/ لَااِلَهَ اِلَّا اللّٰهُ وَحْدَهُ،/ صَدَقَ وَعْدَهُ،/ وَنَصَرَ عَبْدَهُ،/ وَهَزَمَ الْأَحْزَابَ وَحْدَهُ،/ لَااِلَهَ اِلَّااللّٰهُ/ وَلَا نَعْبُدُ اِلَّاإِيَّاهُ/ مُخْلِصِيْنَ لَهُ الدِّيْنُ/ وَلَوْكَرِهَ الْكَاِفُرُوْنَ،/ اللَّهُمَّ إِنِّيْ/ أَسْأَلُكَ الْهُدَى/ وَالتُّقَى/ وَالْعَفَافَ/ وَالْغِنِى،/ اللَّهُمَّ لَكَ الْحَمْدُ/ كَالَّذِيْ نَقُوْلُ/ وَخَيْرًا مِمَّا نَقُوْلُ،/ اللَّهُمَّ إِنِّيْ/ أَسْأَلُكَ رِضَاكَ وَالْجَنَّةَ/ وَأَعُوْذُبِكَ مِنْ سَخَطِكَ وَالنَّارِ/ وَمَا يُقَرِّبُنِيْ اِلَيْهَا/ بِنُوْرِكَ اهْتَدَيْنَا/ وَبِفَضْلِكَ اسْتَغْنَيْنَا/ وَفِيْ كَنَفِكَ/ وَإِنْعَا مِكَ/ وَعَطَائِكَ/ وَإِحْسَانِكَ/ اَصْبَحْنَا وَأَمْسَيْنَا،/ أَنْتَ الْأُوْلَ/ فَلَا قَبْلَكَ شَيْءٌ،/ وَالْآخِرُ/ فَلَا بَعْدَكَ شَيْءٌ،/ وَالظَّاهِرُ/ فَلَا شَيْءَ دُوْنَكَ،/ نَعُوْذُ بِكَ مِنَ الْفَلَسِ وَالْكَسَلِ/ وَعَذَابِ الْقَبْرِ/ وَفِتْنَةِ الغِنَي/ وَنَسْأَلُكَ الْفَوْزَ بِالْجَنَّةِ");
            txtArti.setText("Allaahu Akbar, Allaahu Akbar, Allaahu Akbar, walillaahil hamdu, laa ilaa illallaahu wahdahu, shadaqa wa’dahu, wa nashara ‘abdahuu, wa hazamal ahzaaba wahdahu, laa ilaaha illallaahu wa laa na’budu illaa iyyaahu, mukhlishiina lahud diin wa lawkarihal kaafiruun, Allaahumma innii asalukal  hudaa wat tuqaa wal ‘afaafa wal ghinaa, Allaahumma lakal hamdu kalladzii naquulu wa khairan mimma naquulu, Allaahumma inni as-aluka ridhaaka wal jannata wa a-‘uudzu bika min sakhatika wan-naar wa maa yuqaarribunii ilaihaa min qaulin wa fi’lin aw ‘amalin, Allaahumma bi nuurikahtadainaa wa bi fadhlikas taghnainaa wa fii kafanika wa in’aamika wa ‘athaa-ika wa ihsaanika ashbahnaa wa amsainaa, antal awwalu falaa qablaka syai-un, wal aakhiru falaa ba’daka syai-un, wadz-dzaahiru falaa syai-a duunaka, na-‘uudzubika minal falasi wal kasali wa ‘adzaabil qabri wa fitnatil ghinaa wan as-alukal fauza bil jannati");
        }else if(no == 7){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_7));
            txtArab.setText("اَللّٰهُ اَكْبَرُ/ اَللّٰهُ اَكْبَرُ،/ اَللّٰهُ اَكْبَرُكَبِيْرًا/ وَالْحَمْدُ لِلّٰهِ كَثِيْرًا،/ اللَّهُمَّ حَبِّبْ اِلَيَّ الإِيْمَانَ،/ وَزَيِّنْهُ فِيْ قَلْبِيْ/ وَكَرِّهْ اِلَيَّ الْكُفْرَ/ وَالْفُسُوْقَ/ وَالْعِصْيَانَ/ وَاجْعَلْنِيْ مِنَ الرَّاشِدِيْنَ");
            txtArti.setText("Allaahu Akbar, Allaahu Akbar, Allaahu Akbar, kabiiran, walhamdu lillaahi katsiiran, subhaanaka maa syakarnaaka haqqa syukrika yaa Allaah, subhaanaka maa a’laa sya’naka yaa Allah, Allaahumma habbib ilainal iimaana wa zayyinhu fii quluubinaa wa karrih ilainal kufra wal fusuuqa wal ‘isyaana, waj-‘alnaa minar raasyidiin");
        }else{
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_0));
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

