package com.garudatekno.jemaah.menu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.BackgroundService;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.app.AppController;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.readystatesoftware.viewbadger.BadgeView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import me.anwarshahriar.calligrapher.Calligrapher;


public class sai extends AppCompatActivity {

    private SQLiteHandler db;
    private SessionManager session;

    private SQLiteDatabase database;

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private TextView state,txt_play,txt_back,txt_next,
            circle,txtArab,txtArti,txt_doa1,txt_doa2,txt_doa3,
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


        circle = (TextView) findViewById(R.id.circle);
        txt_back = (TextView) findViewById(R.id.txt_back);
        txt_play = (TextView) findViewById(R.id.txt_play);
        txt_next = (TextView) findViewById(R.id.txt_next);
        txtArab = (TextView) findViewById(R.id.txtArab);
        txtArti = (TextView) findViewById(R.id.txtArti);
        txt_doa1 = (TextView) findViewById(R.id.txt_doa1);
        txt_doa2 = (TextView) findViewById(R.id.txt_doa2);
        txt_doa3 = (TextView) findViewById(R.id.txt_doa3);

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
        startService(new Intent(sai.this, BackgroundService.class));


        menu_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ids = circle.getText().toString().trim();
                final String play = txt_play.getText().toString().trim();
//                media/doa_harian/01_doa_tawaf_1.mp3

                srcPath="/sdcard/android/data/com.garudatekno.jemaah/sai/"+ids+".mp3";
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
        });

        menu_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                }
            }
        });

        //popup
        final Dialog rankDialog = new Dialog(sai.this);
        rankDialog.setContentView(R.layout.view_dialog);
        rankDialog.setCancelable(false);
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

        vplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ids = vaudio.getText().toString().trim();
                final String play = v_play.getText().toString().trim();
                srcPath="/sdcard/android/data/com.garudatekno.jemaah/sai/"+ids;
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
                vname.setText("Doa mendaki bukit safa");
                varab.setText("بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ، أَبْدَأُ بِمَا بَدَأَ اللهُ بِهِ وَرَسُوْلُهُ. إِنَّ الصَّفَا وَالْمَرْوَةَ مِنْ شَعَائِرِ اللَّهِ فَمَنْ حَجَّ الْبَيْتَ أَوِ اعْتَمَرَ فَلَا جُنَاحَ عَلَيْهِ أَنْ يَطَّوَّفَ بِهِمَا وَمَنْ تَطَوَّعَ خَيْرًا فَإِنَّ اللَّهَ شَاكِرٌ عَلِيمٌ");
                varti.setText("Dengan nama Allah Yang Maha Pengasih lagi Maha Penyayang. Aku mulai dengan apa yang telah dimulai oleh Allah dan Rasul-Nya. Sesungguhnya Shafa dan Marwah sebagian dari syiar-syiar (tanda kebesaran) Allah. Maka barang siapa yang beribadah haji ke Baitullah ataupun berumrah, maka tidak ada dosa baginya mengerjakan sa'i antara keduanya. Dan barang siapa yang mengerjakan suatu kebajikan dengan rela hati, maka sesungguhnya Allah Maha Penerima Kebaikan lagi Maha Mengetahui.");
                vaudio.setText("8 doa mendaki bukit safa.mp3");
                rankDialog.show();
            }
        });

        txt_doa2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vname.setText("Doa ketika mendekati bukit safa marwa");
                varab.setText("إِنَّ الصَّفَا وَالْمَرْوَةَ مِنْ شَعَائِرِ اللَّهِ فَمَنْ حَجَّ الْبَيْتَ أَوِ اعْتَمَرَ فَلَا جُنَاحَ عَلَيْهِ أَنْ يَطَّوَّفَ بِهِمَا وَمَنْ تَطَوَّعَ خَيْرًا فَإِنَّ اللَّهَ شَاكِرٌ عَلِيمٌ");
                varti.setText("Sesungguhnya Shafa dan Marwah sebagian dari syiar-syiar (tanda kebesaran) Allah. Maka barang siapa yang beribadah haji ke Baitullah ataupun berumrah, maka tidak ada dosa baginya mengerjakan sa'i antara keduanya. Dan barang siapa yang mengerjakan suatu kebajikan dengan rela hati, maka sesungguhnya Allah Maha Penerima Kebaikan lagi Maha Mengetahui.");
                vaudio.setText("9 doa ketika mendekati bukit safa marwah.mp3");
                rankDialog.show();
            }
        });
        txt_doa3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vname.setText("Doa diantara 2 pilar hijab");
                varab.setText("رَبّ اغْفِرْ وَارْحَمْ، وَاعْفُ وَتَكَرَّمْ، وَتَجَاوَزْ عَمَّا تَعْلَمُ، إنَّكَ تَعْلَمُ مَالاَ نَعْلَمْ، إنَّكَ أَنْتَ اللهُ الأَعَزُّ الأَكْرَمُ");
                varti.setText("Ya Allah ampunilah, sayangilah, ma’afkanlah, bermurah hatilah dan hapuskanlah apa-apa yang Engkau ketahui. Sesungguhnya Engkau Maha Mengetahui apa-apa yang kami sendiri tidak tahu. Sesungguhnya Engkau Ya Allah Maha Mulia dan Maha Pemurah. ");
                vaudio.setText("10 doa diantara 2 pilar hijau.mp3");
                rankDialog.show();
            }
        });

    }

    private void SetProgess(int no){
        if(no == 1){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_1));
            txtArab.setText("اللهُ اَكْبَرُ/ اللهُ اَكْبَرُاللهُ/ اَكْبَرُ اللهُ اَكْبَرُ كَبِيْرًا وَالْحَمْدُلِلهِ كَثِيْرًا/ وَسُبْحَانَ اللهِ الْعَظِيْمِ/ وَبِحَمْدِهِ الْكَرِيْمِ/ بُكْرَةً وَأَصِيْلًا/ وَمِنَ اللَّيْلِ فَاسْجُدْ لَهُ/ وَسَبِّحْهُ لَيْلًا طَوِيْلًا/ لَااِلَهَ اِلاَاللهُ وَحْدَهُ/ أَنْجَزَ وَعْدَهُ/ وَنَصَرَ عَبْدَهُ/ وَهَزَمَ الْأَحْزَابَ وَحْدَهُ/ لاَشَيْءَ قَبْلَهُ وَلاَ بَعْدَهُ/ يُحْيِيْ وَيُمِيْتُ/ وَهُوَ حَيٌّ دَائِمً/ لاَيَمُوْتُ وَلاَيَفُوْتُ أَبَدًا/ بِيَهْدِهِ الْخَيْرُ/ وَاِلَيْهِ الْمَصِيْرُ/ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيْرُ");
            txtArti.setText("Allah Maha Besar, Allah Maha Besar, Allah Maha Besar, dengan segala kebesaran-Nya, segala puji bagi Allah yang Maha Agung dengan segala pujian-Nya yang tidak terhingga, Maha Suci Allah dengan pujian yang Maha Mulia di waktu pagi dan petang, bersujud dan bertasbih pada-Nya sepanjang malam, tidak ada Tuhan selain Allah yang Maha Esa, yang menepati janji-Nya, membela hamba-hamba-Nya, yang menghancurkan musuh-musuh-Nya dengan ke-Esa-an-Nya, tidak ada sesuatu sebelum-Nya ataupun sesudah-Nya, Dia-lah yang menghidupkan dan mematikan, dan Dia adalah Maha Hidup Kekal tiada mati dan tiada musnah untuk selam-lamanya, hanya di tengah-Nya lah terletak kebaikan dan kepada-Nya lah tempat kembali dan hanya Dia-lah yang Maha Kuasa atas segala sesuatu");
        }else if(no == 2){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_2));
            txtArab.setText("اللهُ أكْبَرُ اللهُ أكْبَرُ اللهُ أكْبَرُ ولِلَّهِ الحَمْدُ، لاَ إِلهَ إِلاَّ اللهُ الوَاحِدُ الفَرْدُ الصَّمَدُ الذِيْ لَمْ يّتَّخٍذ صَاحِبَةً وَلَا وَلَدًاْ وَلَمْ يَكُنْ لَهُ شَرِيكٌ فِي الْمُلْكِ وَلَمْ يَكُنْ لَهُ وَلِيٌّ مِنَ الذُّلِّ وَكَبِّرْهُ تَكْبِيرًا اللَّهُمَّ إِنَّكَ قُلْتَ فِيْ كِتَابِكَ المُنَزَّلِ اُدْعُوْنِي أسْتَجِبْ لَكُمْ، دَعَوْنَاكَ رَبَّنَا فَاغْفِرْ لَنَا كَمَا أَمَرْتَنَا إِنَّكَ لا تُخْلِفُ المِيْعَادَ رَبَّنَا إِنَّنَا سَمِعْنَا مُنَادِيًا يُنَادِي لِلْإِيمَانِ أَنْ آَمِنُوا بِرَبِّكُمْ فَآَمَنَّا رَبَّنَا فَاغْفِرْ لَنَا ذُنُوبَنَا وَكَفِّرْ عَنَّا سَيِّئَاتِنَا وَتَوَفَّنَا مَعَ الْأَبْرَارِ رَبَّنَا وَآَتِنَا مَا وَعَدْتَنَا عَلَى رُسُلِكَ وَلَا تُخْزِنَا يَوْمَ الْقِيَامَةِ إِنَّكَ لَا تُخْلِفُ الْمِيعَادَ رَبَّنَا عَلَيْكَ تَوَكَّلْنَا وَإِلَيْكَ أَنَبْنَا وَإِلَيْكَ الْمَصِيرُ. رَبَّنَا اغفِر لَنَا وَ لِأِخوانِنَا الَّذِينَ سَبَقُونَا بِالاِيمَانِ وَ لاَ تَجعَل فىِ قُلُوبِنَا غِلاًّ لِلَّذِينَ ءَامَنُوا رَبَّنَا اِنَّكَ رَءُوفٌ رَحِيمٌ");
            txtArti.setText("Allah Maha Besar, Allah Maha Besar, Allah Maha Besar, hanya bagi Allahlah segala pujian. Tidak ada Tuhan selain Allah yang Maha Esa, tunggal dan tumpuan segala maksud dan hajat, yang tidak beristri dan tidak beranak, tidak bersekutu dalam kekuasaan. Tidak menjadi pelindung kehinaan. Agungkanlah Dia dengan segenap kebesaran.");
        }else if(no == 3){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_3));
            txtArab.setText("اللهُ أكْبَرُ اللهُ أكْبَرُ اللهُ أكْبَرُ ولِلَّهِ الحَمْدُ، رَبَّنَا أَتْمِمْ لَنَا نُورَنَا وَاغْفِرْ لَنَا إِنَّكَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ اللَّهُمَّ إِنِّيْ أَسْأَلُكَ الخَيْرَ كُلَّهُ عَاجِلَهُ وآجِلَهُ، وَأَسْتَغْفِرُكَ لِذَنْبِيْ، وَأَسْأَلُكَ رَحْمَتَكَ يَا أَرْحَمَ الرَّاحِمِيْنَ");
            txtArti.setText("Allah Maha Besar, Allah Maha Besar, Allah Maha Besar. Hanya bagi Allah semua pujian. Ya Allah, sempurnakanlah cahaya terang bagi kami, ampunilah kami, sesungguhnya Engkau Maha Kuasa atas segala sesuatu. Ya Allah, sesungguhnya aku mohon pada-Mu segala kebaikan yang sekarang dan masa yang akan datang dan aku mohon ampunan pada-Mu dan dosaku serta aku mohon pada-Mu rahmat-Mu Wahai Tuhan Yang Maha Pengasih dari segala yang pengasih.");
        }else if(no == 4){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_4));
            txtArab.setText("اللهُ أكْبَرُ اللهُ أكْبَرُ اللهُ أكْبَرُ ولِلَّهِ الحَمْدُ، اللَّهُمَّ إِنِّيْ أَسْأَلُكَ مِنْ خَيْرِ مَا تَعْلَمُ، وَأَعُوْذُ بِكَ مِنَ شَرِّ مَا تَعْلَمُ وَأَسْتَغْفِرُكَ مِنْ كُلِّ مَا تَعْلَمُ إِنَّكَ أَنْتَ عَلاَّمُ الغُيُوْب لاَ إِلهَ إِلاَّ اللهُ المَلِكُ الحَقُّ المُبِيْنُ، مُحَمَّدٌ رَسُوْلُ اللهِ صَادِقُ الوَعْدِ الأَمِيْنُ اللَّهُمَّ إِنِّيْ أَسْأَلُكَ كَمَا هَدَيْتَنِيْ لِلإسْلَامِ أَنْ لاَ تَنْزِعَهُ مِنِّيْ حَتَّى تَوَفَّنِيْ وَأَنَا مُسْلِمٌ اللَّهُمَّ اجْعَلْ في قَلْبِي نُوراً، وَفِي لِسَانِي نُورًا، وَاجْعَلْ في سَمْعِي نُوراً، وَفِي بَصَرِيْ نُورًا اللَّهُمَّ اشْرَحْ لِيْ صَدْرِيْ وَيَسِّرْلِيْ أَمْرِيْ، وَأَعُوْذُ بِكَ مِنْ وَسَاوِسِ الصَّدْرِ وَشَتَاتِ الأَمْرِ وَفِتْنَةِ القَبْرِ اللَّهُمَّ إِنِّيْ أَعُوْذُ بِكَ مِنْ شَرِّ مَا يَلِجُ فِي اللَّيْلِ وَمِنْ شَرِّ مَا يَلِجُ فِي النَّهَارِ، وَمِنْ شَرِّ مَا تَهُبُّ بِهِ الرِّيَاحُ، يَا أَرْحَمَ الرَّاحِمِيْنَ سُبْحَانَكَ مَا عَبَدْنَاكَ حَقَّ عِبَادَتِكَ يَا اللهُ، سُبْحَانَكَ مَا ذَكَرْنَاكَ حَقَّ ذِكْرِكَ يَا ا");
            txtArti.setText("Allah Maha Besar, Allah Maha Besar, Allah Maha Besar. Segala puji hanya bagi Allah. Ya Allah, Tuhanku, aku mohon pada-Mu dari kebaikan yang Engkau tahu dan berlindung pada-Mu dari kejahatan yang Engkau tahu, dan aku mohon ampun pada-Mu dari segala kesalahan yang Engkau ketahui, sesungguhnya Engkau Maha Mengetahui yang gaib. Tidak ada Tuhan selain Allah Maha Raja yang sebenar-benarnya. Muhammad utusan Allah yang selalu menempati janji lagi terpercaya. Ya Allah sebagaimana Engkau telah menunjuki aku memilih Islam, maka aku mohon kepada-Mu untuk mencabutnya, sehingga aku meninggal dalam keadaan Muslim. Ya Allah, berilah cahaya terang dalam hati, telinga dan penglihatanku. Ya Allah lapangkanlah dadaku dan mudahkanlah bagiku segala urusan. Dan Aku berlindung pada-Mu dari godaan bisikan hati, kekacauan urusan dan fitnah kubur. Ya Allah, aku berlindung pada-Mu dari kejahatan yang tersembunyi di waktu malam dan siang hari, serta kejahatan yang dibawa angin lalu, Wahai Tuhan Yang Maha Pengasih dari segenap yang pengasih. Ya Allah, Maha Suci Engkau, kami tidak bisa menyembah-Mu dengan pengabdian yang semestinya. Ya Allah, Maha Suci Engkau, kami tidak bisa menyebut-Mu (dzikir) dengan semestinya.");
        }else if(no == 5){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_5));
            txtArab.setText("اللهُ أكْبَرُ اللهُ أكْبَرُ اللهُ أكْبَرُ ولِلَّهِ الحَمْدُ. سُبْحَانَكَ مَا شَكَرْنَاكَ حَقَّ شُكْرِكَ يَا اللهُ، سُبْحَانَكَ مَا أَعْلَى شَأْنِكَ يَا اللهُ اللَّهُمَّ حَبِّبْ إِلَيَّ الإِيمَانَ وَزَيِّنْهُ فِي قَلْبِيْ، وَكَرِّهْ إِلَيَّ الْكُفْرَ وَالْفُسُوقَ وَالْعِصْيَانَ، وَاجْعَلْنِيْ مِنَ الرَّاشِدِينَ");
            txtArti.setText("Allah Maha Besar, Allah Maha Besar, Allah Maha Besar. Segala puji hanya untuk Allah Maha Suci Engkau, kami tidak mensyukuri-Mu dengan syukur yang semestinya. Ya Allah Maha Suci Engkau. Alangkah Agung Zat-Mu Ya Allah. Ya Allah, cintakanlah kami kepada iman dan hiaskanlah dihati kami, tanamkanlah kebencian pada diri kami pada perbuatan kufur, fasik dan durhaka. Jadikanlah kami dari golongan orang-orang yang mendapat petunjuk.");
        }else if(no == 6){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_6));
            txtArab.setText("اللهُ أكْبَرُ اللهُ أكْبَرُ اللهُ أكْبَرُ ولِلَّهِ الحَمْدُ، لاَ إِلهَ إِلاَّ اللهُ وَحْدَهُ، صَدَقَ وَعْدَهُ، وَنَصَرَ عَبْدَهُ، وَهَزَمَ الأَحْزَابَ وَحْدَهُ، لاَ إِلَهَ إِلاَّ اللّه مُخْلِصِينَ لَهُ الدِّينَ وَلَوْ كَرِهَ الكافِرُونَ اللَّهُمَّ إنِّي أسألُكَ الهُدَى والتُّقَى والعَفَافَ وَالغِنَى اللَّهُمَّ لَكَ الحَمْدُ كَالَّذِيْ تَقُوْلُ وَخَيْرًا مِمَّا نَقُوْلُ اللَّهُمَّ إنِّي أسألُكَ رِضَاكَ وَالجَنَّةَ وَأَعُوْذُ بِكَ مِنْ سَخَطِكَ وَالنَّارِ وَمَا يُقَرِّبُنِيْ إِلَيْهَا مِنْ قَوْلٍ أَوْ فِعْلٍ أَوْ عَمَلٍ اللَّهُمَّ بِنُوْرِكَ اهْتَدَيْنَا، وَبِفَضْلِكَ اسْتَغْنَيْنَا، وَفِي كَنَفِكَ وَإِنْعَامِكَ وَعَطَائِكَ وَإِحْسَانِكَ أَصْبَحْنَا وَأَمْسَيْنَا أَنْتَ الأوَّلُ فَلاَ قَبْلَكَ شَيْءٌ، وَأَنْتَ الآخِرُ فَلاَ بَعْدَكَ شَيْءٌ، وَالظَّاهِرُ فَلاَ شَيْئَ فَوْقَكَ، وَالبَاطِنُ فَلاَ شَيْءَ دُوْنَكَ نَعُوْذُ بِكَ مِنْ الفَشَلِ وَالكِسَلِ، وَمِنْ عَذَابِ القَبْرِ وَمِنْ فِتْنَةِ الغِنَى، وَنَسْألُكَ الفَوْزَ بِالجَنَّةِ وَالنَّجَاةَ مِنْ النَّارِ");
            txtArti.setText("Allah Maha Besar, Allah Maha Besar, Allah Maha Besar. Segala puji hanya untuk Allah. Tidak ada Tuhan selain Allah Yang Maha Esa, yang menempati janji-Nya, menolong hamba-Nya dan mengalahkan sendiri musuh-musuh-Nya. Tiada Tuhan selain Allah. Dan kami tidak menyembah selain Dia dengan memurnikan kepatuhan kepada-Nya, sekalipun orang-orang kafir membenci. Ya Allah, aku memohon pada-Mu petunjuk, ketakwaan, pengendalian diri dan kekayaan. Ya Allah pada-Mu-lah segala puji seperti yang kami ucapkan dan bahkan lebih baik dari yang kami ucapkan. Ya Allah, aku mohon pada-Mu ridha-Mu dan surga, aku berlindung pada-Mu dari murka-Mu dan siksa neraka dan apapun yang dapat mendekatkan daku padanya (neraka), baik ucapan atau pun amal perbuatan. Ya Allah hanya dengan nur cahaya-Mu kami ini mendapat petunjuk, dengan pemberian-Mu kami merasa cukup, dan dalam naungan-Mu, nikmat-Mu, anugerah-Mu dan kebajikan-Mu jauhlah kami berada diwaktu pagi dan petang. Engkau-lah yang mula pertama, tidak ada sesuatu pun yang ada sebelum-Mu dan Engkau pulalah yang paling akhir dan tidak ada sesuatupun yang ada dibelakang (sesudah)-Mu, Engkaulah yang lahir (nyata), maka tidak ada sesuatu pun yang ada di atas Engkau. Engkau pulalah yang batin, maka tidak ada sesuatupun dibawah-Mu. Kami berlindung pada-Mu dari pailit, malas, siksa kubur dan fitnah kekayaan serta kami mohon pada-Mu kemenangan memperoleh surga, dan keselamatan dari api neraka.");
        }else if(no == 7){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.sai_7));
            txtArab.setText("اللهُ أَكْبَرُ، اللهُ أَكْبَرُ، اللهُ أَكْبَرُ كَبِيْرًا، وَالحَمْدُ لِلهِ كَثِيْرًا اللَّهُمَّ حَبِّبْ إِلَيَّ الإِيمَانَ وَزَيِّنْهُ فِي قَلْبِيْ، وَكَرِّهْ إِلَيَّ الْكُفْرَ وَالْفُسُوقَ وَالْعِصْيَانَ، وَاجْعَلْنِيْ مِنَ الرَّاشِدِينَ");
            txtArti.setText("Allah Maha Besar, Allah Maha Besar, Allah Maha Besar. Segala puji bagi Allah dengan pujian yang tidak terhingga. Ya Allah, cintakanlah aku kepada iman dan hiaskanlah ia di kalbuku. Tanamkanlah kebencian padaku perbuatan kufur fasiq dan durhaka. Dan jadikanlah pula aku dari golongan orang yang mendapat petunjuk.");
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
}
