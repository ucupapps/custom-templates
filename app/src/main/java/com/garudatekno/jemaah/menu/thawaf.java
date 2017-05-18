package com.garudatekno.jemaah.menu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
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


public class thawaf extends AppCompatActivity {

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
    LinearLayout menu_play,menu_next,menu_back,judul,isi,vplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thawaf);
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
        startService(new Intent(thawaf.this, BackgroundService.class));


        menu_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ids = circle.getText().toString().trim();
                final String play = txt_play.getText().toString().trim();
//                media/doa_harian/01_doa_tawaf_1.mp3

                srcPath="/sdcard/android/data/com.garudatekno.jemaah/thawaf/"+ids+".mp3";
                File cek = new File(srcPath);
                if (!cek.exists()) {
                    srcPath=AppConfig.URL_HOME+"/media/doa_harian/0"+ids+"_doa_tawaf_"+ids+".mp3";
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
        final Dialog rankDialog = new Dialog(thawaf.this);
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
                srcPath="/sdcard/android/data/com.garudatekno.jemaah/thawaf/"+ids;
                File cek = new File(srcPath);
                if (!cek.exists()) {
                    srcPath=AppConfig.URL_HOME+"/media/doa_harian/"+ids;
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
                cmdStop();
                rankDialog.dismiss();
            }
        });

        txt_doa1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vname.setText("Doa Istilam");
                varab.setText("");
                varti.setText("");
                vaudio.setText("");
                rankDialog.show();
            }
        });

        txt_doa2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vname.setText("Doa diantara rukun yamani dan hajar aswad");
                varab.setText("رَبَّنَا اَتِنَا فِى الدُّنْيَا حَسَنَةً وَفِى الْأَخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ وَاَدْ خِلْنَا الْجَنَّةَ مَعَ الْأَبْرَارِ. يَاعَزِيْزُ يَاغَفَّارُ يَارَبَّ الْعَالَمِيْنَ");
                varti.setText("Wahai Tuhan kami, berilah kami kebaikan di dunia dan kebaikan di akhirat dan hindarkanlah kami dari siksa neraka Dan masukkanlah kami ke dalam surga bersama orang-orang yang berbuat baik, wahai Tuhan Yang Maha Perkasa, Maha Pengampun dan Tuhan yang menguasai seluruh alam");
                vaudio.setText("08_doa_diantara_rukun_yamani_dan_hajar_aswad.mp3");
                rankDialog.show();
            }
        });
        txt_doa3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vname.setText("Doa saat di Multazam");
                varab.setText("اَللّٰهُمَّ يَا رَبَّ الْبَيْتَ الْعَتِيْقِ أَعْتِقْ رِقَابَنَا وَرِقَابَ اٰبَآئِنَا وَأُمَّهَاتِنَا وَإِخْوَانِنَا وَأَوْلَادِنَا مِنَ النَّارِ يَاذَا اْلجُوْدِ وَالْكَرَمِ وَالْفَضْلِ وَاْلمَنِّ وَالْعَطَاءِ وَاْلإِحْسَانِ. اَللّٰهُمَّ أَحْسِنْ عَا قِبَتَنَا فِى اْلأُمُوْرِ كُلِّهَا وَأَجِرْنَا مِنْ خِزْيِ الدُّنْيَا وَعَذَابِ اْلاٰخِرَةِ. اَللّٰهُمَّ إِنِّيْ عَبْدُكَ وَابْنُ عَبْدِكَ وَاقِفٌ تَحْتَ بَابِكَ مُلْتَزِمٌ بِِأَعْتَا بِكَ مُتَذَلِّلٌ بَيْنَ يَدَيْكَ أَرْجُوْ رَحْمَتَكَ وَأَخْشَى عَذَابَكَ يَا قَدِيْمَ اْلإِحْسَانِ. اَللّٰهُمَّ إِنِّيْ أَسْأَلُكَ أَنْ تَرْفَعَ ذِكْرِيْ وَتَضَعَ وِزْرِيْ وَ تُصْلِحَ أَمْرِيْ وَتُطَهِّرَ قَلْبِيْ وَتُنَوِّرَ لِيْ فِى قَبْرِيْ وَتَغْفِرَ لِيْ ذَنْبِيْ وَأَسْأَلُكَ الدَّرَجَاتِ الْعُلَى ىفِ الْجَنَّةِ");
                varti.setText("Ya Allah Tuhanku yang memelihara Ka’bah ini, merdekakanlah diri kami, bapak dan ibu kami, saudara-saudara dan anak-anak kami dari siksa neraka. Wahai Tuhan Yang Maha Pemurah, yang mempunyai keutamaan, kelebihan, anugerah dan kebaikan. Ya Allah Tuhanku sesungguhnya aku ini hambaMu, anak dari hambaMu tegak berdiri di bawah pintu Ka’bahMu, menundukan diri di hadapanMu, sambil mengharapkan rahmatMu, kasih sayangMu, aku takut akan siksaMu, wahai Tuhan Yang Maha Tahu yang punya segala kebaikan. Ya Allah, Tuhanku, aku mohon padaMu, agar engkau tinggikan namaku, hapuskan dosaku, perbaiki segala urusanku, bersihkan hatiku, berilah cahaya kelak dalam kuburku. Berilah ampun dosaku dan aku mohon padaMu martabat yang tinggi di dalam syurga.");
                vaudio.setText("10_doa_saat_di_Multazam.mp3");
                rankDialog.show();
            }
        });
        txt_doa4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vname.setText("Doa mencium hajar aswad");
                varab.setText("بِسْمِ اللهِ ، وَاللهُ أَكْبَر اللَّهُمَّ إِيمَاناً بِكَ ، وَتَصْدِيقًا بِكِتَابِكَ ، وَوَفَاءً بِعَهْدِكَ ، وَاتِّبَاعاً لِسُنَّةِ نَبِيِّكَ مُحَمَّدٍ صَلَّى الله عليه وسلم");
                varti.setText("Dengan menyebut nama Allah, Allah maha besar. Ya Allah, dengan keimanan kepadaMu, membenarkan kitabMu, menepati janji kepadaMu, serta mengikuti sunah nabiMu shallallahu alaihi wa sallam.");
                vaudio.setText("09_doa_mencium_hajar_aswad.mp3");
                rankDialog.show();
            }
        });
        txt_doa5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vname.setText("Doa setelah shalat sunnah tawaf");
                varab.setText("اللَّهُمَّ إِنَّكَ تَعْلَمُ سِرِّيْ وَعَلَانِيَتِيْ فَاقْبَلْ مَعْذِرَتِيْ، وَتَعْلَمُ حَاجَتِيْ فَاعْطِنِيْ سُؤْلِيْ، وَتَعْلَمُ مَا فِيْ نَفْسِيْ فَاغْفِرْ لِيْ ذُنُوْبِيْ اللَّهُمَّ إِنِّيْ أَسْألُكَ إِيْمَانًا دَائِمًا يُبَاشِرُ قَلْبِيْ، وَيَقِيْنًا صَادِقاً حَتَّى أَعْلَمَ أَنَّهُ لاَ يُصِيْبُنِيْ إِلاَّ مَاكَتَبْتَ لِيْ رِضًا مِنْكَ بِمَا قَسَمْتَ لِيْ، أَنْتَ وَلِيِّ فِيْ الدُنْيَا وَالآخِرَةِ. تَوَفَّنِيْ مُسْلِمِينَ وَأَلْحِقْنِى اللَّهُمَّ لَا تَدَعْ لَنَا فِي مَقَامِنَا هَذَا ذَنْبًا إلَّا غَفَرْتَهُ، وَلَا هَمًّا إلَّا فَرَّجْتَهُ،، وَلَا حَاجَةً إلَّا قَضَيْتَهَا وَيَسَّرْتَهَا، فَيَسِّرْ أُمُوْرَنَا وَاشْرَحْ صُدُوْرَنَا، وَنَوِّرْ قُلُوْبَنَا، وَاخْتِمْ بِالصَّالِحَاتِ أَعْمَالَنَا اللَّهُمَّ تَوَفَّنَا مُسْلِمِينَ وَأَحْيِنَا مُسْلِمِينَ وَأَلْحِقْنَا بِالصَّالِحِينَ غَيْرَ خَزَايَا وَلَا مَفْتُوْنِينَ");
                varti.setText("Ya Allah, sesungguhnya Engkau Maha Mengetahui rahasiaku yang tersembunyi dan amal perbuatanku yang nyata, maka terimalah ratapanku. Engkau Maha Mengetahui keperluanku, kabulkanlah permohonanku. Engkau Maha Mengetahui apapun yang terkandung dalam hatiku, maka ampunilah dosaku. Ya Allah, aku ini mohon pada-Mu iman yang tetap yang melekat terus di hati, keyakinan yang sungguh-sungguh sehingga aku dapat mengetahui bahwa tiada suatu yang menimpa daku selain dari yang Engkau tetapkan bagiku. Jadikanlah aku rela terhadap apapun yang Engkau bagikan padaku. Wahai Tuhan yang Maha Pengasih dari segala yang pengasih. Engkau adalah pelindungku di dunia dan di akhirat. Wafatkanlah aku dalam keadaan muslim dan gabungkanlah kami kedalam orang-orang shaleh. Ya Allah, janganlah Engkau biarkan di tempat kami itu suatu dosapun kecuali Engkau ampunkan, tiada suatu kesusahan hati, kecuali Engkau lapangkan, tiada satu hajat keperluan kecuali Engkau penuhi dan mudahkan, maka mudahkanlah segenap urusan kami dan lapangkanlah dada kami, terangkan hati kami dan sudahilah semua amal perbuatan kami dengan amal yang shaleh. Ya Allah matikanlah kami dalam keadaan muslim, hidupkanlah kami dalam keadaan muslim, dan masukkanlah kami kedalam golongan orang-orang yang shaleh tanpa kenistaan dan fitnah.");
                vaudio.setText("11_doa_setelah_shalat_sunnah_tawaf.mp3");
                rankDialog.show();
            }
        });
        txt_doa6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vname.setText("Doa minum air zam zam");
                varab.setText("اَللّٰهُمَّ إِنِّيْ أْسْأَلُكَ عِلْمً نَافِعًا وَرِزْقًا وَاسِعًا وَشِفَآءً مِنْ كُلِّ دَآءٍ وَسَقَمٍ بِرَحْمَتِكَ يَآ أَرْحَمَ الرَّاحِمِيْنَ");
                varti.setText("Ya Allah, aku mohon kepadaMu ilmu pengetahuan yang bermanfaat, rizki yang luas dan sembuh dari segala sakit dan penyakit pikun dengan rahmatMu ya Allah yang Maha Pengasih lagi Maha Penyayang.");
                vaudio.setText("12_doa_minum_air_zam_zam.mp3");
                rankDialog.show();
            }
        });
        txt_doa7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vname.setText("Doa tawaf wada");
                varab.setText("بِسْمِ اللهِ اللهُ أكْبَرُ، سُبْحَانَ اللَّهِ، والحَمْدُ لِلَّهِ، وَلَا إِلهَ إِلاَّ اللَّهُ، وَاللَّهُ أَكْبَرُ، وَلَا حَوْلَ وَلاَ قُوَّةَ إِلاَّ بِاللّهِ العَلِيِّ العَظِيمِ. وَالصَّلَاةُ وَالسَّلَامُ عَلَى رَسُوْلِ اللّهِ صَلَّى اللّهُ عَلَيْهِ وَسَلَّمَ . اللَّهُمَّ إِيْمَاناً بِكَ وَتَصْدِيقاً بِكِتَابِكَ، وَوَفَاءً بِعَهْدِكَ وَاتِّبَاعاً لِسُنَّةِ نَبِيِّكَ مُحَمَّدٍ صَلَّى اللّهُ عَلَيْهِ وَسَلَّمَ. {إِنَّ الَّذِي فَرَضَ عَلَيْكَ الْقُرْآنَ لَرَادُّكَ إِلَى مَعَادٍ} يَا مُعِيْدُ أَعِدْنِيْ، يَا سَمِيْعُ أَسْمِعْنِيْ، يَا جَبَّارُ اجْبُرْنِيْ، يَا سَتَّارُ اسْتُرْنِيْ، يَا رَحْمَنُ ارْحَمْنِيْ، يَا رَدَّادُ ارْدُدْنِيْ إِلَى بَيْتِكَ هَذَا، وَارْزُقْنِيَ العَوْدَةَ ثُمَّ العَوْدَ كَرَّاتٍ بَعْدَ مَرَّاتٍ، تَائِبُوْنَ عَابِدُوْنَ سَائِحُوْنَ لِرَبِّنَا حَامِدُوْنَ. صَدَقَ اللهُ وَعْدَهُ، وَنَصَرَ عَبْدَهُ، وَهَزَمَ الأَحْزَابَ وَحْدَهُ. اللَّهُمَّ احْفَظْنِيْ عَنْ يَمِيْنِيْ وَعَنْ يَسَارِيْ، وَمِنْ قُدَّامِيْ وَمِنْ وَرَاءِ ظَهْرِيْ، وَمِنْ فَوْقِيْ وَمِنْ تَحْتِيْ حَتَّى تُوَصِّلَنِيْ إِلَى أَهْلِيْ وَبَلَدِي. اللَّهُمَّ هَوِّنْ عَلَيْنَا السَّفَرَ وَأَطْوِ لَنَا بُعْدَ الْأَرْضِ، اللَّهُمَّ اصْحَبْنَا فِى سَفَرِنَا وَاخْلُفْنَا فِى أَهْلِنَا يَا أَرْحَمَ الرَّاحِمِيْنَ، وَ يَا رَبَّ العَالَمِيْنَ");
                varti.setText("Dengan nama Allah, Allah Maha Besar, Maha Suci Allah dan segala puji hanya kepada Allah tidak ada Tuhan selain Allah Yang Maha Besar, tidak daya (untuk memperoleh manfaat) dan tiada kekuatan (untuk menolak kesulitan) kecuali dengan pertolongan dari Allah Yang Maha Tinggi lagi Maha Besar. Shalawat dan salam bagi junjungan Rasulullah Saw. Ya Allah, aku datang kemari karena iman kepada-Mu, membenarkan kitab-Mu, memenuhi janji-Mu dan karena menuruti sunnah Nabi-Mu Muhammad Saw. Sesungguhnya Tuhan yang menurunkan al-Qur’an kepadamu niscaya memulangkanmu ke tempat kembali, wahai Tuhan yang Kuasa mengembalikan, kembalikan aku ke tempatku, wahai Tuhan yang Maha Mendengar, dengarlah (kabulkanlah) permohonanku wahai Tuhan Yang Maha Memperbaiki, perbaikilah aku, wahai Tuhan Yang Maha Pelindung, tutupilah aibku, wahai Tuhan Yang Maha Kasih Sayang, sayangilah aku, wahai Tuhan Yang Maha Kuasa Mengembalikan, kembalikanlah aku ke Ka’bah ini dan berilah aku rizqi untuk mengulanginya berkali-kali, dalam keadaan bertaubat dan beribadat, berlayar menuju Tuhan kami sambil memuji, Allah Maha menepati janji-Nya membantu hamba-hamba-Nya, yang menghancurkan sendiri musuh-musuh-Nya. Ya Allah, peliharalah aku dari kanan, kiri, depan dan belakang, dari sebelah atas dan bawah sampai Engkau mengembalikan aku kepada keluarga dan tanah airku. Ya Allah, mundahkanlah perjalanan kami, lipatkan bumi untuk kami. Ya Allah sertailah kami dalam perjalanan, dan gantilah kedudukan kami dalam keluarga yang ditinggal, wahai Tuhan Yang Maha Pengasih melebihi segala pengasih, wahai Tuhan Yang Memelihara seluruh alam.");
                vaudio.setText("13_doa_tawaf_wada.mp3");
                rankDialog.show();
            }
        });
        txt_doa8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vname.setText("Doa setelah selesai tawaf wada");
                varab.setText("للَّهُمَّ إِنَّ البَيْتَ بَيْتُكَ، وَالعَبْدَ عَبْدُكَ، وَابْنُ عَبْدِكَ وَابْنُ أَمَتِكَ، حَمَلْتَنِيْ عَلى مَا سَخَّرْتَ لِىْ مِنْ خَلْقِكَ حَتَّى سَيَّرْتَنِيْ إِلَى بِلاَدِكَ، وَبَلَّغْتَنِيْ بِنِعْمَتِكَ حَتَّى أَعَنْتَنِيْ عَلَى قَضَاءِ مَنَاسِكِكَ. فَإِنْ كُنْتَ رَضِيْتَ عَنِّيْ فَازْدَدْ عَنِّيْ رِضًا، وَإلاَّ فَمُنَّ الآنَ عَلَيَّ قَبْلَ تَبَاعُدِيْ عَنْ بَيْتِكَ. هَذَا أَوَانُ انْصِرَافِيْ إِنْ أَذَنْتَ لِيْ غَيْرَ مُسْتَبْدَلٍ بِكَ وَلاَ بِبَيْتِكَ وَلاَ رَاغِبًا عَنْكَ وَلاَ عَن بَيْتكَ. اللَّهُمَّ اصْحِبْنِيْ بِالعَافِيَةِ فِيْ بَدَنِيْ وَالعِصْمَةَ فِي دِيْنِيْ، وَأَحْسِنْ مُنْقَلَبِيْ وَارْزُقْنِيْ طَاعَتَكَ مَا أَبْقَيْتَنِي. وَاجْمَعْ لِيْ خَيْرَ الدُّنْيَا وَالآخِرَةِ إنَّكَ عَلَى كُلِّ شَيْءٍ قَدِيْرٌ. اللَّهُمَّ لاَ تَجْعَلْ هَذَا آخِرَ العَهْدِ بِبَيْتِكَ الحَرَامِ، وَإِنْ جَعَلْتَهُ آخِرَ العَهْدِ فَعَوِّضْنِيْ عَنْهُ الجَنَّةَ بِرَحْمَتِكَ يَا أَرْحَمَ الرَّاحِمِيْنَ. امِيْنَ يَا رَبَّ الْعَالَمِيْنَ");
                varti.setText("Ya Allah, rumah ini adalah rumah-Mu, aku ini hamba-Mu anak hamba-Mu yang lelaki dan anak hamba-Mu yang perempuan. Engkau telah membawa aku di dalam hal yang Engkau sendiri memudahkan untukku sehingga Engkau jalankan aku negeri-Mu ini Engkau telah menyampaikan aku dengan nikmat-Mu juga, sehingga Engkau menolong aku untuk menunaikan ibadah haji Kalau Engkau rela padaku, maka tambahkanlah keridahaan itu padaku, maka tambahkanlah keridhaan itu padaku. Jika tidak maka tuntaskan sekarang sebelum aku jauh dari rumah-Mu ini. Sekarang sudah waktunya aku pulang, jika Engkau izinkan aku dengan tidak menukar sesuatu dengan Engkau (Dzat-Mu) ataupun rumah-Mu tidak benci pada-Mu dan tidak juga benci pada rumah-Mu Ya Allah, berikanlah aku ini dengan afiat pada tubuhku, tetap menjaga agamaku, baik kepulanganku, dan berilah aku taat setia pada-Mu selama-lamanya selama Engkau membiarkan aku hidup dan kumpulkanlah bagiku kebajikan dunia dan akhirat. Sesungguhnya Engkau Maha Kuasa atas segala sesuatu Ya Allah, janganlah Engkau jadikan waktu ini masa terakhir bagiku dengan rumah-Mu. Sekiranya Engkau jadikan bagiku masa terakhir, maka gantilah surga untukku, dengan rahmat-Mu, wahai Tuhan yang Maha Pengasih dari segala yang pengasih Amin, wahai Tuhan Pemelihara seru sekalian alam");
                vaudio.setText("14_doa_setelah_selesai_tawaf_wada.mp3");
                rankDialog.show();
            }
        });

    }

    private void SetProgess(int no){
      if(no == 1){
          progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_1));
          txtArab.setText("سُبْحَانَ اللَّهِ، والحَمْدُ لِلَّهِ، وَلا إِلَهَ إِلاَّ اللَّهُ، وَاللَّهُ أَكْبَرُ، وَلاَ حَوْلَ وَلاَ قُوَّةَ إِلاَّ باللَّهِ العَلِيِّ العَظِيمِ وَالصَّلاَةُ وَِِالسَلاَمُ عَلَى رَسُوْلِ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ اللَّهُمَّ إيمَاناً بِكَ وَتَصدِيقاً بِكِتابِكَ، وَوَفاءً بِعَهْدِكَ وَاتِّباعاً لِسُنَّةِ نَبِيِّكَ مُحَمَّدٍ صَلَّى اللّهُ عَلَيْهِ وَسَلَّمَ اللَّهُمَّ إنِّي أَسألُكَ العَفْوَ وَالعَافِيَةَ، وَالمُعَافَاةَ الدَّائِمَةَ فِى الدِّيْنِ وَالدُّنْيَا وَالْآخِرَةِ، وَالْفَوْزَ بِالْجَنَّةِ، وَالنَّجَاةَ مِنَ النَّارِ");
          txtArti.setText("Maha suci Allah, segala puji bagi Allah, sagala puji bagi Allah, tidak ada Tuhan selain Allah, Allah Maha Besar Tiada daya (untuk memperoleh manfaat) dan tiada kemampuan (untuk menolak bahaya) kecuali dengan pertolongan Allah Yang Maha Mulia dan Maha Agung. Shalawat dan salam bagi Rasulullah Saw. Ya Allah, aku thawaf ini karena beriman kepada-Mu membenarkan kitab-Mu dan memenuhi janji-Mu dan mengikuti sunnah Nabi-Mu, Muhammad Saw. Ya Allah sesungguhnya aku mohon kepada-Mu ampunan, kesehatan, dan perlindungan yang kekal dalam menjalankan agama, di dunia dan akhirat dan beruntung memperoleh surga dan terhindar dari siksa neraka.");
      }else if(no == 2){
            progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_2));
            txtArab.setText("اَللَّهُمَّ إِنَّ هَذَا الْبَيْتَ بَيْتُكَ، وَالْحَرَمَ حَرَمُكَ، وَالْأَمْنَ أَمْنُكَ، وَالْعَبْدَ عَبْدُكَ وَأَنَا عَبْدُكَ، وَابْنُ عَبْدُكَ وَهَذَا مَقَامُ الْعَائِذِ بِك مِنْ النَّارِ. فَحَرِّمْ لُحُومَنَا وَبَشَرَتَنَا عَلَى النَّارِ");
            txtArti.setText("Ya Allah, sesungguhnya Bait ini rumah-Mu, tanah mulia ini tanah-Mu, negeri aman ini negeri-Mu, hamba ini hamba-Mu anak dari hamba-Mu dan tempat ini adalah tempat orang berlindung pada-Mu dari siksa neraka, maka haramkanlah daging dan kulit kami dari siksa api neraka.");
      }else if(no == 3){
          progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_3));
          txtArab.setText("اَللَّهُمَّ إِنِّيْ أَعُوْذُ بِكَ مِنَ الشَّكِّ وَالشِّرْكِ، وَالشِّقَاقِ وَالنِّفَاقِ، وَسُوْءِ الْأَخْلَاقِ، وَسُوءِ المَنْظَرِ وَالْمُنْقَلَبِ، فِي الْمَالِ وَالْأَهْلِ وَالْوَلَدِ اَللَّهُمَّ إِنَّيْ اَسْأَلُكَ رِضَاكَ وَالجَنَّةَ وَاَعُوذُ بِكَ مِنْ سَخَطِكَ وَالنَّارِ اَللَّهُمَّ إِنِّي أَعُوذُ بِك مِنْ فِتْنَةِ الْقَبْرِ وَأَعُوْذُ بِكَ مِنْ فِتْنَةِ الْمَحْيَا وَالْمَمَاتِ");
          txtArti.setText("Ya Allah aku berlindung kepada-MU dari keraguan, syirik, percekcokan, kemunafikan, buruk budi pekerti dan penampilan dan kepulangan yang jelek dalam hubungan dengan harta benda, keluarga dan anak-anak. Ya Allah, sesungguhnya aku mohon kepada-Mu keridhaan-Mu dan surga. Dan aku berlindung pada-Mu dari pada murka-Mu dan siksa neraka. Ya Allah aku berlindung pada-Mu dari fitnah kubur, dan aku berlindung pada-Mu dari fitnah kehidupan dan derita kematian.");
      }else if(no == 4){
          progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_4));
          txtArab.setText("اللَّهُمَّ اجْعَلْهُ حَجًّا مَبْرُوْرًا، وَسَعْيًا مَشْكُوْرًا، وَذَنْبًا مَغْفُوْرًا، وَعَمَلاً صَالِحًا مَقْبُوْلاً، وَتِجَارَةً لَنْ تَبُوْرَ، يَا عَالِمَ مَا فِيْ الصُّدُوْرِ أَخْرِجْنِيْ يَا اللهُ مِنْ الظُلُمَاتِ إِلَى النُوْرِ اللَّهُمَّ إنِّي أَسألُكَ مُوجِبَاتِ رَحْمَتِكَ، وَعَزَائِمَ مَغْفِرَتِكَ، وَالسَّلامَةَ مِنْ كُلِّ إثْمٍ، وَالفَوْزَ بالجَنَّةِ، وَالنَّجاةَ مِنَ النَّارِ اللَّهُمَّ قَنِّعْنِيْ بِمَا رَزَقْتَنِيْ، وَبَارِكْ لِيْ فِيْمَا أَعْطَيْتَنِيْ، وَاخْلُفْ عَلَيَّ كُلَّ غَائِبَةٍ لِيْ مِنْكَ بِخَيْرٍ");
          txtArti.setText("Ya Allah karuniakanlah haji yang mabrur, sa‘i yang diterima, dosa yang diampuni, amal saleh yang diterima dan usaha yang tidak akan mengalami rugi. Wahai Tuhan yang Maha Mengetahui apa-apa yang terkandung dalam hati sanubari. Keluarkanlah aku dari kegelapan ke cahaya yang terang benderang. Ya Allah aku mohon kepada–Mu segala hal yang mendatangkan rahmat-Mu dan keteguhan ampunan-Mu selamat dari segala dosa dan beruntung dengan mendapat berbagai kebaikan, beruntung memperoleh surga, terhindar dari siksa neraka. Tuhanku, puaskanlah aku dengan anugerah yang telah Engkau berikan, berkatilah untukku atas semua yang Engkau anugerahkan kepadaku dan gantilah apa-apa yang ghaib dari pandanganku dengan kebajikan dari-Mu.");
      }else if(no == 5){
          progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_5));
          txtArab.setText("اللَّهُمَّ أَظِلَّنِيْ تَحْتَ ظِلِّ عَرْشِكَ يَوْمَ لاَ ظِلَّ إلاَّ ظِلُّكَ، وَلاَ بَاقِيَ إلاَّ وَجْهُكَ، وَاسْقِنِيْ مِنْ حَوْضِ نَبِيَّكَ مُحَمَّدٍ شُرْبَةً هَنِيْئَةً مَرِيْئَةً لاَ أَظْمَأُ بَعْدَهَا أبَدًا اللَّهُمَّ إِنِّيْ أَسْألُكَ مِنْ خَيْرِ مَا سَألَكَ منْهُ نَبِيُّكَ مُحَمَّدٌ صلى اللّه عليه وسلم، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا اسْتَعاذَكَ مِنْهُ نَبِيُّكَ مُحَمَّدٌ صلى اللّه عليه وسلم اللَّهُمَّ إِنِّيْ أَسْألُكَ الجَنَّةَ وَنَعِيْمَهَا وَمَا يُقَرِّبُنِيْ إِلَيْهَا مِنْ قَوْلٍ أَوْ فِعْلٍ أوْ عَمَلٍ، وأعُوذُ بِكَ مِنَ النَّارِ وَمَا يُقَرِّبُنِيْ إلَيْها مِنْ قَوْلٍ أَوْ فِعْلٍ أوْ عَمَلٍ");
          txtArti.setText("Ya Allah, lindungilah aku di bawah naungan singgasana-Mu pada hari yang tidak ada naungan selain naungan-Mu, dan tidak ada yang kekal kecuali Zat-Mu, dan berikanlah aku minuman dari telaga Nabi Muhammad Saw. dengan suatu minuman yang lezat, segar dan nyaman, sesudah itu aku tidak akan haus untuk selamanya. Ya Allah, aku mohon pada-Mu kebaikan yang dimohonkan oleh Nabi-Mu Muhammad Saw. dan aku berlindung pada-Mu dari kejahatan yang dimintakan perlindungan oleh Nabi-Mu , Muhammad Saw. Ya Allah, aku mohon pada-Mu surga serta ni'matnya dan apapun yang dapat mendekatkan aku kepadanya baik ucapan maupun amal perbuatan dan aku berlindung kepada-Mu dari neraka serta apapun yang mendekatkan aku kepadanya baik ucapan ataupun amal perbuatan.");
      }else if(no == 6){
          progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_6));
          txtArab.setText("اللَّهُمَّ إنَّ لَكَ عَلَيَّ حُقُوْقاً كَثِيْرَةً فِيْمَا بَيْنِيْ وَبَيْنَكَ، وَحُقُوْقاً كَثِيْرَةً فِيْمَا بَيْنِيْ وَبَيْنَ خَلْقِكَ");
          txtArti.setText("Ya Allah sesungguhnya Engkau mempunyai hak kepadaku banyak sekali dalam hubunganku dengan Engkau. Dan Engkau juga mempunyai hak banyak sekali dalam hubunganku dengan mahluk-Mu. Ya Allah apa yang menjadi hak-Mu kepada mahluk-Mu maka tanggunglah diriku, cukupkanlah aku dengan rizki-Mu yang halal, terhindar dari pada haram, dengan ta'at kapada-Mu terhindar dari kemaksiatan, dengan anugerah-Mu terhindar dari pada mengharapkan dari orang lain selain dari pada-Mu, Wahai Tuhan yang Maha Pengampun. Ya Allah, sesungguhnya rumah-Mu (Baitullah) ini agung. Zat-Mu pun Mulia, Engkau Maha Penyabar, Maha Pemurah, Maha Agung yang sangat suka memberi ampun, maka ampunilah aku.");
      }else if(no == 7){
          progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_7));
          txtArab.setText("اللَّهُمَّ إِنِّيْ أَسْألُكَ إيْمَانًا كَامِلاً، وَيَقِيْنًا صَادِقاً، وَرِزْقاً وَاسِعاً، وَقَلْباً خَاشِعاً، وَلِسَاناً ذَاكِرًا، وَحَلاَلاً طَيِّباً، وَتَوْبَةً نَصُوْحاً، وَتَوْبَةً قَبْلَ المَوْتِ، وَرَاحَةً عِنْدَ المَوْتِ، وَمَغْفِرَةً وَرَحْمَةً بَعْدَ المَوْتِ، وَالفَعْوَ عِنْدَ الحِسَابِ، وَالفَوْزَ بِالجَنَّةِ وَالنَّجَاةَ مِنَ النَّارِ، بِرَحْمَتِكَ يَا عَزِيْزُ يَا غَفَّارُ. رَبِّ زِدْنِيْ عِلْماً، وَألْحِقْنِيْ بِالصَّالِحِيْنَ");
          txtArti.setText("Ya Allah, aku mohon pada-Mu iman yang sempurna, keyakinan yang benar, rizki yang luas, hati yang khusyu', lidah yang selalu berdzikir (menyebut nama Allah), rizki yang halal dan baik, taubat yang diterima dan taubat sebelum mati, ketenangan ketika mati, keampunan dan rahmat sesudah mati, keampunan ketika dihisab, keberuntungan memperoleh surga dan terhindar dari neraka dengan rahmat kasih sayang-Mu, wahai Tuhan Yang Maha Perkasa, Yang Maha Pengampun. Tuhanku, berilah aku tambahan ilmu Pengetahuan dan gabungkanlah aku ke dalam golongan orang-orang yang shaleh.");
      }else{
          progress.setImageDrawable(getResources().getDrawable(R.drawable.thawaf_0));
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

            mediaPlayerState = thawaf.MP_State.Error;
            showMediaPlayerState();

            return false;
        }};


    private void cmdReset(){
        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnErrorListener(mediaPlayerOnErrorListener);
        }
        mediaPlayer.reset();
        mediaPlayerState = thawaf.MP_State.Idle;
        showMediaPlayerState();
    }

    private void cmdSetDataSource(String path){
        if(mediaPlayerState == thawaf.MP_State.Idle){
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayerState = thawaf.MP_State.Initialized;
            } catch (IllegalArgumentException e) {
//                Toast.makeText(thawaf.this,
//                        e.toString(), Toast.LENGTH_LONG).show();
//                e.printStackTrace();
            } catch (IllegalStateException e) {
//                Toast.makeText(thawaf.this,
//                        e.toString(), Toast.LENGTH_LONG).show();
//                e.printStackTrace();
            } catch (IOException e) {
//                Toast.makeText(thawaf.this,
//                        e.toString(), Toast.LENGTH_LONG).show();
//                e.printStackTrace();
            }
        }else{
//            Toast.makeText(thawaf.this,
//                    "Invalid State@cmdSetDataSource - skip",
//                    Toast.LENGTH_LONG).show();
        }

        showMediaPlayerState();
    }

    private void cmdPrepare(){

        if(mediaPlayerState == thawaf.MP_State.Initialized
                ||mediaPlayerState == thawaf.MP_State.Stopped){
            try {
                mediaPlayer.prepare();
                mediaPlayerState = thawaf.MP_State.Prepared;
            } catch (IllegalStateException e) {
                Toast.makeText(thawaf.this,
                        e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(thawaf.this,
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
        if(mediaPlayerState == thawaf.MP_State.Prepared
                ||mediaPlayerState == thawaf.MP_State.Started
                ||mediaPlayerState == thawaf.MP_State.Paused
                ||mediaPlayerState == thawaf.MP_State.PlaybackCompleted){
            mediaPlayer.start();
            mediaPlayerState = thawaf.MP_State.Started;
        }else{
//            Toast.makeText(ViewPanduan.this,
//                    "Invalid State@cmdStart() - skip",
//                    Toast.LENGTH_LONG).show();
        }

        showMediaPlayerState();
    }

    private void cmdPause(){
        if(mediaPlayerState == thawaf.MP_State.Started
                ||mediaPlayerState == thawaf.MP_State.Paused){
            mediaPlayer.pause();
            mediaPlayerState = thawaf.MP_State.Paused;
        }else{
//            Toast.makeText(ViewPanduan.this,
//                    "Invalid State@cmdPause() - skip",
//                    Toast.LENGTH_LONG).show();
        }
        showMediaPlayerState();
    }

    private void cmdStop(){

        if(mediaPlayerState == thawaf.MP_State.Prepared
                ||mediaPlayerState == thawaf.MP_State.Started
                ||mediaPlayerState == thawaf.MP_State.Stopped
                ||mediaPlayerState == thawaf.MP_State.Paused
                ||mediaPlayerState == thawaf.MP_State.PlaybackCompleted){
            mediaPlayer.stop();
            mediaPlayerState = thawaf.MP_State.Stopped;
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
