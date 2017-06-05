package salam.gohajj.id.menu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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


public class sai extends AppCompatActivity {

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

                srcPath="/sdcard/android/data/com.gohajj.id/sai/"+ids+".mp3";
                File cek = new File(srcPath);
                if (!cek.exists()) {
                    srcPath=AppConfig.URL_HOME+"/uploads/panduan/sai/"+ids+".mp3";
                }
                cmdReset();
                cmdSetDataSource(srcPath);

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
                    cmdPause();
                    img_play.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    txt_play.setText("Mainkan");
                }else if(play.equals("Mainkan")) {
//                    mProgressDialog = ProgressDialog.show(sai.this,"","Harap Tunggu...",false,false);
                    cmdPrepare();
                    cmdStart();
                    txt_play.setText("Pause");
                    img_play.setImageDrawable(getResources().getDrawable(R.drawable.stop));
                }
            }
        });

        menu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tply=txt_play.getText().toString();
                if (!tply.equals("Mulai")) {
                cmdStop(); txt_play.setText("Mainkan");
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
                cmdStop(); txt_play.setText("Mainkan");
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
                    cmdReset();cmdPrepare();
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
                srcPath="/sdcard/android/data/com.gohajj.id/sai/"+ids;
                File cek = new File(srcPath);
                if (!cek.exists()) {
                    srcPath=AppConfig.URL_HOME+"/uploads/panduan/sai/"+ids;
                }
                cmdReset();
                cmdSetDataSource(srcPath);

                if(play.equals("Pause")) {
//                    cmdStop();
                    cmdPause();
                    img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    v_play.setText("Mainkan");
                }else if(play.equals("Mainkan")) {
//                    mProgressDialog = ProgressDialog.show(sai.this,"","Harap Tunggu...",false,false);
                    cmdPrepare();
                    cmdStart();
                    v_play.setText("Pause");
                    img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.stop));
                }
            }
        });

//        rankDialog.getWindow().setLayout(700, 450);
        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmdReset();cmdPrepare();
                rankDialog.dismiss();
            }
        });

        txt_doa1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmdReset();cmdPrepare();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa Mendaki Bukit Safa");
                varab.setText("بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ، أَبْدَأُ بِمَا بَدَأَ اللهُ بِهِ وَرَسُوْلُهُ. إِنَّ الصَّفَا وَالْمَرْوَةَ مِنْ شَعَائِرِ اللَّهِ فَمَنْ حَجَّ الْبَيْتَ أَوِ اعْتَمَرَ فَلَا جُنَاحَ عَلَيْهِ أَنْ يَطَّوَّفَ بِهِمَا وَمَنْ تَطَوَّعَ خَيْرًا فَإِنَّ اللَّهَ شَاكِرٌ عَلِيمٌ");
                varti.setText("Bismillaahir rahmaani rahiim, abda’u bimaa bada-allaahu bihii wa rasuuluhuu, innash shafaa wal marwata min sya-‘aa-irillaah, faman hajjal baita awi’tamara falaa junaaha ‘alaihi ayyatthawwafa bihimma wa man tathawwa’a khairan fainnallaaha syaakirun ‘aliimun");
                vaudio.setText("8 doa mendaki bukit safa.mp3");
                rankDialog.show();
            }
        });

        txt_doa2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmdReset();cmdPrepare();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa Saat di Bukit Safa");
                varab.setText("اَللهُ أَكْبَرُ، اَللهُ أَكْبَرُ، اَللهُ أَكْبَرُ، وَلِلَّهِ الحَمْدُ. اللَّهُ أَكْبَرُ عَلَى مَا هَدَانَا، وَالحَمْدُ لِلَّهِ عَلَى مَا أَوْلاَناَ. لَا إلَهَ إِلاَّ اللهُ ، وَحْدَهُ لَا شَرِيكَ لَهُ ، لَهُ الْمُلْكُ ، وَلَهُ الْحَمْدُ يُحْيي ويُمِيْتُ، بِيَدِهِ الخَيْرُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ. لاَ إِلَهَ إِلاَّ اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، أَنْجَزَ وَعْدَهُ وَنَصَرَ عَبْدَهُ وَهَزَمَ الأَحْزَابَ وَحْدَهُ، لَا إلَهَ إِلاَّ اللهُ ، وَلاَ نَعْبُدُ إِلاَّ إِيَّاهُ مُخْلِصِيْنَ لَهُ الدِّيْنَ، وَلَوْ كَرِهَ الكَافِرُوْنَ");
                varti.setText("Allaahu Akbar, Allaahu Akbar, Allaahu Akbar, walillaahil hamdu, Allaahu Akbar ‘alaa maa hadaana walhamdulillaahi ‘ala maa aulaanaa, laa ilaaha illallaahu wahdahuu la syariikalahuu, lahul mulku wa lahul hamdu yuhyii wa yumiitu biyadihil khairu wa huwa ‘alaa kulli syai-in qadiir, laa ilaaha illallaahu wahdahuu la syariikalahuu anjaza wa’dahuu wa nashara ‘abdahuu wa hazamal ahzaaba wahdahuu laa ilaaha illallaahu walaa na’budu illa iyyahu mukhlishiina lahuddiina walau karihal kaafiruun");
                vaudio.setText("11 doa saat dibukit safa.mp3");
                rankDialog.show();
            }
        });
        txt_doa3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmdReset();cmdPrepare();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa di Antara 2 Pilar Hijau");
                varab.setText("رَبّ اغْفِرْ وَارْحَمْ، وَاعْفُ وَتَكَرَّمْ، وَتَجَاوَزْ عَمَّا تَعْلَمُ، إنَّكَ تَعْلَمُ مَالاَ نَعْلَمْ، إنَّكَ أَنْتَ اللهُ الأَعَزُّ الأَكْرَمُ");
                varti.setText("Rabbighfir warham wa’fu wa takarram ‘amma ta’lam, innaka ta’lamu maa laa na’lamu, innakallahul a-‘azzul akramu");
                vaudio.setText("10 doa diantara 2 pilar hijau.mp3");
                rankDialog.show();
            }
        });
        txt_doa4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmdReset();cmdPrepare();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa Ketika Mendekati Bukit Safa atau Marwah");
                varab.setText("إِنَّ الصَّفَا وَالْمَرْوَةَ مِنْ شَعَائِرِ اللَّهِ فَمَنْ حَجَّ الْبَيْتَ أَوِ اعْتَمَرَ فَلَا جُنَاحَ عَلَيْهِ أَنْ يَطَّوَّفَ بِهِمَا وَمَنْ تَطَوَّعَ خَيْرًا فَإِنَّ اللَّهَ شَاكِرٌ عَلِيمٌ");
                varti.setText("Innash shafaa wal marwata min sya-‘aa-irillaah, faman hajjal baita awi’tamara falaa junaaha ‘alaihi ayyathowwafa bihimma wa man tathawwa’a khairan fainnallaaha syaakirun ‘aliimun");
                vaudio.setText("9 doa ketika mendekati bukit safa marwah.mp3");
                rankDialog.show();
            }
        });
        txt_doa5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmdReset();cmdPrepare();v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                vname.setText("Doa Selesai Sai");
                varab.setText("اللَّهُمَّ رَبَّنَا تَقَبَّلْ مِنَّا وَعَافِنَا وَاعْفُ عَنَّا وَعَلَى طَاعَتِكَ وَشُكْرِكَ أَعِنَّا، وَعَلَى غَيْرِكَ لاَ تَكِلْنَا، وَعَلَى الإِيْمَانِ وَالإِسْلاَمِ الكَامِلِ جَمِيْعًا تَوَفَّنَا وَأَنْتَ رَاضٍ عَنَّا اللَّهُمَّ ارْحَمْنِيْ بِتَرْكِ المَعَاصِيْ أَبَدًا مَا أَبْقَيْتَنِيْ، وَارْحَمْنِيْ أَنْ أَتَكَلَّفَ مَالاَ يَعْنِيْنِيْ، وَارْزُقْنِيْ حُسْنَ النَّظَرِ فِيْمَا يُرْضِيْكَ عَنِّيْ يَا أَرْحَمَ الرَاحِمِيْنَ");
                varti.setText("Allaahumma rabbanaa taqabbal minnaa wa’aafinaa wa’fu ‘annaa wa ‘alaa thaa-‘atika wa syukrika a’innaa wa ‘alaa ghairika laa takilnaa wa ‘alal iimaani wal islaami kaamili jamii’an tawaffanaa wa anta raadhin ‘annaa, Allaahummarhamnii bitarki ma’aashii abadam maa abqaitanii warhamnii warzuqnii husnan nazhari fii maa yurdhiika ‘annii ya arhamar raahimiin");
                vaudio.setText("12 doa selesai sa'i.mp3");
                rankDialog.show();
            }
        });

    }

    private void SetProgess(int no){
        if(no == 1){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_1));
            txtArab.setText("اللهُ اَكْبَرُ/ اللهُ اَكْبَرُاللهُ/ اَكْبَرُ اللهُ اَكْبَرُ كَبِيْرًا وَالْحَمْدُلِلهِ كَثِيْرًا/ وَسُبْحَانَ اللهِ الْعَظِيْمِ/ وَبِحَمْدِهِ الْكَرِيْمِ/ بُكْرَةً وَأَصِيْلًا/ وَمِنَ اللَّيْلِ فَاسْجُدْ لَهُ/ وَسَبِّحْهُ لَيْلًا طَوِيْلًا/ لَااِلَهَ اِلاَاللهُ وَحْدَهُ/ أَنْجَزَ وَعْدَهُ/ وَنَصَرَ عَبْدَهُ/ وَهَزَمَ الْأَحْزَابَ وَحْدَهُ/ لاَشَيْءَ قَبْلَهُ وَلاَ بَعْدَهُ/ يُحْيِيْ وَيُمِيْتُ/ وَهُوَ حَيٌّ دَائِمً/ لاَيَمُوْتُ وَلاَيَفُوْتُ أَبَدًا/ بِيَهْدِهِ الْخَيْرُ/ وَاِلَيْهِ الْمَصِيْرُ/ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيْرُ");
            txtArti.setText("Allaahu akbar Allaahu akbar Allaahu akbar Allaahu akbar kabiraw walhamdulillaahi katsiiraw wa subhaanallaahil ‘adziimi wa bihamdihil kariim bukrataw wa ashiilan wa minal laili fasjud lahuu wa sabbihhu lailan thawiilan laa ilaaha illaahu wahdahuu anjaza wa’dahuu wa nashara ‘abdahuu wa hazamal ahzaaba wahdahuu laa syai-a qablahuu wa laa ba’dahuu yuhyii wa yumiitu wahuwa hayyun daa-imun laa yamuutu wa laa yafuutu Abadan biyadihil khairu wa ilaihil mashiir wa huwa alaa kulli syai-in qadiir");
        }else if(no == 2){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_2));
            txtArab.setText("اللهُ أكْبَرُ اللهُ أكْبَرُ اللهُ أكْبَرُ ولِلَّهِ الحَمْدُ، لاَ إِلهَ إِلاَّ اللهُ الوَاحِدُ الفَرْدُ الصَّمَدُ الذِيْ لَمْ يّتَّخٍذ صَاحِبَةً وَلَا وَلَدًاْ وَلَمْ يَكُنْ لَهُ شَرِيكٌ فِي الْمُلْكِ وَلَمْ يَكُنْ لَهُ وَلِيٌّ مِنَ الذُّلِّ وَكَبِّرْهُ تَكْبِيرًا اللَّهُمَّ إِنَّكَ قُلْتَ فِيْ كِتَابِكَ المُنَزَّلِ اُدْعُوْنِي أسْتَجِبْ لَكُمْ، دَعَوْنَاكَ رَبَّنَا فَاغْفِرْ لَنَا كَمَا أَمَرْتَنَا إِنَّكَ لا تُخْلِفُ المِيْعَادَ رَبَّنَا إِنَّنَا سَمِعْنَا مُنَادِيًا يُنَادِي لِلْإِيمَانِ أَنْ آَمِنُوا بِرَبِّكُمْ فَآَمَنَّا رَبَّنَا فَاغْفِرْ لَنَا ذُنُوبَنَا وَكَفِّرْ عَنَّا سَيِّئَاتِنَا وَتَوَفَّنَا مَعَ الْأَبْرَارِ رَبَّنَا وَآَتِنَا مَا وَعَدْتَنَا عَلَى رُسُلِكَ وَلَا تُخْزِنَا يَوْمَ الْقِيَامَةِ إِنَّكَ لَا تُخْلِفُ الْمِيعَادَ رَبَّنَا عَلَيْكَ تَوَكَّلْنَا وَإِلَيْكَ أَنَبْنَا وَإِلَيْكَ الْمَصِيرُ. رَبَّنَا اغفِر لَنَا وَ لِأِخوانِنَا الَّذِينَ سَبَقُونَا بِالاِيمَانِ وَ لاَ تَجعَل فىِ قُلُوبِنَا غِلاًّ لِلَّذِينَ ءَامَنُوا رَبَّنَا اِنَّكَ رَءُوفٌ رَحِيمٌ");
            txtArti.setText("Allaahu Akbar, Allaahu Akbar, Allaahu akbar, wa lillaahil hamdu, laa ilaaha illallaahul waahidul fardhus shamad, alladzii lam yattakhidz shaahibataw wa laa waladaw wa lam yakul lahuu syariikun fil mulki wa lam yakul lahuu waliyyunm minadz dzulli wa kabbirhu takbiiran, Allaahumma innaka qulta fii kitaabikal minzali ud-‘uunii astajib lakum, da-aunaaka rabbanaa faghfirlanaa kamaa amartanaa innaka laa tukhliful mii’aad, rabbanaa innanaa sami’naa munaadiyay yunaadii lil iimaani an aaminuu birabbikum fa aamannaa, rabbanaa faghfirlanaa dzunuubanaa wa kaffir ‘annaa sayyiaatinaa wa tawaffanaa ma’al abraar, rabbanaa wa aatinaa maa wa’attinaa ‘alaa rusulika wa laa tukhzinaa yaumal qiyaamati innaka laa tukhliful mii’aad,rabbanaa ‘alaika tawakkalnaa wa ilaika anabnaa wa ilaikal mashiir, rabbanaghfirlanaa wa li ikhwaaninal ladziina sabaquunaa bil iimaan wa laa taj’al fii quluubinaa ghillal lillaadziina aamanuu rabbanaa innaka ra’uufur rahiim");
        }else if(no == 3){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_3));
            txtArab.setText("اللهُ أكْبَرُ اللهُ أكْبَرُ اللهُ أكْبَرُ ولِلَّهِ الحَمْدُ، رَبَّنَا أَتْمِمْ لَنَا نُورَنَا وَاغْفِرْ لَنَا إِنَّكَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ اللَّهُمَّ إِنِّيْ أَسْأَلُكَ الخَيْرَ كُلَّهُ عَاجِلَهُ وآجِلَهُ، وَأَسْتَغْفِرُكَ لِذَنْبِيْ، وَأَسْأَلُكَ رَحْمَتَكَ يَا أَرْحَمَ الرَّاحِمِيْنَ");
            txtArti.setText("Allaahu Akbar, Allaahu Akbar, Allaahu Akbar, wa lillaahilhamd. Rabbanaa atmim lanaa nuuranaa waghfirlanaa innaka ‘alaa kuli syai’ingqadiir. Allaahumma inni as’alukalkhaira kullahu ‘aajilahu wa’aajilahu wastaghfiruka lidzambii wa as’aluka rahmataka yaa arhamarraahimiin");
        }else if(no == 4){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_4));
            txtArab.setText("اللهُ أكْبَرُ اللهُ أكْبَرُ اللهُ أكْبَرُ ولِلَّهِ الحَمْدُ، اللَّهُمَّ إِنِّيْ أَسْأَلُكَ مِنْ خَيْرِ مَا تَعْلَمُ، وَأَعُوْذُ بِكَ مِنَ شَرِّ مَا تَعْلَمُ وَأَسْتَغْفِرُكَ مِنْ كُلِّ مَا تَعْلَمُ إِنَّكَ أَنْتَ عَلاَّمُ الغُيُوْب لاَ إِلهَ إِلاَّ اللهُ المَلِكُ الحَقُّ المُبِيْنُ، مُحَمَّدٌ رَسُوْلُ اللهِ صَادِقُ الوَعْدِ الأَمِيْنُ اللَّهُمَّ إِنِّيْ أَسْأَلُكَ كَمَا هَدَيْتَنِيْ لِلإسْلَامِ أَنْ لاَ تَنْزِعَهُ مِنِّيْ حَتَّى تَوَفَّنِيْ وَأَنَا مُسْلِمٌ اللَّهُمَّ اجْعَلْ في قَلْبِي نُوراً، وَفِي لِسَانِي نُورًا، وَاجْعَلْ في سَمْعِي نُوراً، وَفِي بَصَرِيْ نُورًا اللَّهُمَّ اشْرَحْ لِيْ صَدْرِيْ وَيَسِّرْلِيْ أَمْرِيْ، وَأَعُوْذُ بِكَ مِنْ وَسَاوِسِ الصَّدْرِ وَشَتَاتِ الأَمْرِ وَفِتْنَةِ القَبْرِ اللَّهُمَّ إِنِّيْ أَعُوْذُ بِكَ مِنْ شَرِّ مَا يَلِجُ فِي اللَّيْلِ وَمِنْ شَرِّ مَا يَلِجُ فِي النَّهَارِ، وَمِنْ شَرِّ مَا تَهُبُّ بِهِ الرِّيَاحُ، يَا أَرْحَمَ الرَّاحِمِيْنَ سُبْحَانَكَ مَا عَبَدْنَاكَ حَقَّ عِبَادَتِكَ يَا اللهُ، سُبْحَانَكَ مَا ذَكَرْنَاكَ حَقَّ ذِكْرِكَ يَا ا");
            txtArti.setText("Allaahu Akbar, Allaahu Akbar, Allaahu Akbar, wa lillaahil hamdu, Allaahumma innii as-aluka min khairim maa ta’lamu wa a-‘uudzu bika min syarri maa ta’lamu wa astghfiruka min kulli maa ta’lamu innaka anta ‘allaamul ghuyub, laa ilaaha illallaahul malikul haqqul mubiin Muhammadur rasuulullaah, shaadiqul wa’dilamiin, Allaahumma innii as-aluka kamma hadaitanii lil islaami an laa tunzi ‘anhu minnii hattaa tawaffanii wa anaa muslim. Allaahummaj-‘al fi qalbi nuuran, wa fii sam’ii nuuran wa fii basharii nuuran, Allaahummasyraah lii shadrii wa yassir lii amrii wa a-‘uudzubika min wasaawisis shadri wasyasyattaatil amri wafitnatil qabrii, Allaahumma innii a-‘uudzubika min syarri maa yaliju fillaili wa syarri maa yaliju fin nahaari wa min syarri maa tahubbu bihir riyaahu yaa arhamar raahimiin, subhaanaka maa ‘abadnaaka haqqa ‘ibaadatika yaa Allaahu subhaanaka maa dzakarnaaka haqqa dzikrika yaa Allaah");
        }else if(no == 5){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_5));
            txtArab.setText("اللهُ أكْبَرُ اللهُ أكْبَرُ اللهُ أكْبَرُ ولِلَّهِ الحَمْدُ. سُبْحَانَكَ مَا شَكَرْنَاكَ حَقَّ شُكْرِكَ يَا اللهُ، سُبْحَانَكَ مَا أَعْلَى شَأْنِكَ يَا اللهُ اللَّهُمَّ حَبِّبْ إِلَيَّ الإِيمَانَ وَزَيِّنْهُ فِي قَلْبِيْ، وَكَرِّهْ إِلَيَّ الْكُفْرَ وَالْفُسُوقَ وَالْعِصْيَانَ، وَاجْعَلْنِيْ مِنَ الرَّاشِدِينَ");
            txtArti.setText("Allaahu Akbar, Allaahu Akbar, Allaahu Akbar, walillaahil hamdu, subhaanaka maa syakarnaaka haqqa syukrika ya Allaah, Subhaanaka maa a’laa sya’naka yaa Allaah, Allaahumma habbib ilainal iimaana wa zayyinhu fii quluubinaa wa karrih ilainal kufra wal fusuuqa wal ‘ishyaana, waj-‘alnaa minar raasyidiin");
        }else if(no == 6){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_6));
            txtArab.setText("اللهُ أكْبَرُ اللهُ أكْبَرُ اللهُ أكْبَرُ ولِلَّهِ الحَمْدُ، لاَ إِلهَ إِلاَّ اللهُ وَحْدَهُ، صَدَقَ وَعْدَهُ، وَنَصَرَ عَبْدَهُ، وَهَزَمَ الأَحْزَابَ وَحْدَهُ، لاَ إِلَهَ إِلاَّ اللّه مُخْلِصِينَ لَهُ الدِّينَ وَلَوْ كَرِهَ الكافِرُونَ اللَّهُمَّ إنِّي أسألُكَ الهُدَى والتُّقَى والعَفَافَ وَالغِنَى اللَّهُمَّ لَكَ الحَمْدُ كَالَّذِيْ تَقُوْلُ وَخَيْرًا مِمَّا نَقُوْلُ اللَّهُمَّ إنِّي أسألُكَ رِضَاكَ وَالجَنَّةَ وَأَعُوْذُ بِكَ مِنْ سَخَطِكَ وَالنَّارِ وَمَا يُقَرِّبُنِيْ إِلَيْهَا مِنْ قَوْلٍ أَوْ فِعْلٍ أَوْ عَمَلٍ اللَّهُمَّ بِنُوْرِكَ اهْتَدَيْنَا، وَبِفَضْلِكَ اسْتَغْنَيْنَا، وَفِي كَنَفِكَ وَإِنْعَامِكَ وَعَطَائِكَ وَإِحْسَانِكَ أَصْبَحْنَا وَأَمْسَيْنَا أَنْتَ الأوَّلُ فَلاَ قَبْلَكَ شَيْءٌ، وَأَنْتَ الآخِرُ فَلاَ بَعْدَكَ شَيْءٌ، وَالظَّاهِرُ فَلاَ شَيْئَ فَوْقَكَ، وَالبَاطِنُ فَلاَ شَيْءَ دُوْنَكَ نَعُوْذُ بِكَ مِنْ الفَشَلِ وَالكِسَلِ، وَمِنْ عَذَابِ القَبْرِ وَمِنْ فِتْنَةِ الغِنَى، وَنَسْألُكَ الفَوْزَ بِالجَنَّةِ وَالنَّجَاةَ مِنْ النَّارِ");
            txtArti.setText("Allaahu Akbar, Allaahu Akbar, Allaahu Akbar, walillaahil hamdu, laa ilaa illallaahu wahdahu, shadaqa wa’dahu, wa nashara ‘abdahuu, wa hazamal ahzaaba wahdahu, laa ilaaha illallaahu wa laa na’budu illaa iyyaahu, mukhlishiina lahud diin wa lawkarihal kaafiruun, Allaahumma innii asalukal  hudaa wat tuqaa wal ‘afaafa wal ghinaa, Allaahumma lakal hamdu kalladzii naquulu wa khairan mimma naquulu, Allaahumma inni as-aluka ridhaaka wal jannata wa a-‘uudzu bika min sakhatika wan-naar wa maa yuqaarribunii ilaihaa min qaulin wa fi’lin aw ‘amalin, Allaahumma bi nuurikahtadainaa wa bi fadhlikas taghnainaa wa fii kafanika wa in’aamika wa ‘athaa-ika wa ihsaanika ashbahnaa wa amsainaa, antal awwalu falaa qablaka syai-un, wal aakhiru falaa ba’daka syai-un, wadz-dzaahiru falaa syai-a duunaka, na-‘uudzubika minal falasi wal kasali wa ‘adzaabil qabri wa fitnatil ghinaa wan as-alukal fauza bil jannati");
        }else if(no == 7){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_7));
            txtArab.setText("اللهُ أَكْبَرُ، اللهُ أَكْبَرُ، اللهُ أَكْبَرُ كَبِيْرًا، وَالحَمْدُ لِلهِ كَثِيْرًا اللَّهُمَّ حَبِّبْ إِلَيَّ الإِيمَانَ وَزَيِّنْهُ فِي قَلْبِيْ، وَكَرِّهْ إِلَيَّ الْكُفْرَ وَالْفُسُوقَ وَالْعِصْيَانَ، وَاجْعَلْنِيْ مِنَ الرَّاشِدِينَ");
            txtArti.setText("Allaahu Akbar, Allaahu Akbar, Allaahu Akbar, kabiiran, walhamdu lillaahi katsiiran, subhaanaka maa syakarnaaka haqqa syukrika yaa Allaah, subhaanaka maa a’laa sya’naka yaa Allah, Allaahumma habbib ilainal iimaana wa zayyinhu fii quluubinaa wa karrih ilainal kufra wal fusuuqa wal ‘isyaana, waj-‘alnaa minar raasyidiin");
        }else{
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_0));
            txtArab.setText("");
            txtArti.setText("");
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
//            timeLine.setVisibility(View.INVISIBLE);
//            timeFrame.setVisibility(View.INVISIBLE);
        }else{
            if(mediaPlayer.isPlaying()){
                mProgressDialog.dismiss();
//                timeLine.setVisibility(View.VISIBLE);
//                timeFrame.setVisibility(View.VISIBLE);
//
//                int mediaDuration = mediaPlayer.getDuration();
//                int mediaPosition = mediaPlayer.getCurrentPosition();
//                timeLine.setMax(mediaDuration);
//                timeLine.setProgress(mediaPosition);
//                timePos.setText(String.valueOf((float)mediaPosition/1000) + "s");
//                timeDur.setText(String.valueOf((float)mediaDuration/1000) + "s");
            }else{
                txt_play.setText("Mainkan");
                img_play.setImageDrawable(getResources().getDrawable(R.drawable.play));
                v_play.setText("Mainkan");
                img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
//                timeLine.setVisibility(View.INVISIBLE);
//                timeFrame.setVisibility(View.INVISIBLE);
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
        if (!txt_play.getText().toString().equals("Mulai")){
            txt_play.setText("Mainkan");
            img_play.setImageDrawable(getResources().getDrawable(R.drawable.play));
        }
        v_play.setText("Mainkan");
        img_vplay.setImageDrawable(getResources().getDrawable(R.drawable.play));
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
//            Toast.makeText(ViewPanduan.this,
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
//            Toast.makeText(ViewPanduan.this,
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
//            Toast.makeText(ViewPanduan.this,
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

    @Override
    public void onStop()
    {
        super.onStop();
        cmdReset();
    }
}
