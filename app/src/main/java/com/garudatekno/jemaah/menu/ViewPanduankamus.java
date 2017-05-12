package com.garudatekno.jemaah.menu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.CustomListPanduan1;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;

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

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;

import static java.lang.Boolean.FALSE;

public class ViewPanduankamus extends AppCompatActivity implements View.OnClickListener,ListView.OnItemClickListener {


    private TextView txtname,txtid,txtFile, state,txtdesc,txtarab,txtIndonesia;
    private Button buttonStart,buttonSave;
    private ListView listView;
    private String id,file,uid;
    private SeekBar timeLine;
    LinearLayout timeFrame;
    TextView timePos, timeDur;
    final static int RQS_OPEN_AUDIO_MP3 = 1;

    private boolean playPause;
    private String JSON_STRING;
    private MediaPlayer mediaPlayer;

    private SQLiteHandler db;
    private SessionManager session;
    String srcPath = null;
    private boolean intialStage = true;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_panduan_kamus);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        session = new SessionManager(getApplicationContext());
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


        Intent intent = getIntent();
        id = intent.getStringExtra(AppConfig.EMP_ID);

        txtdesc = (TextView) findViewById(R.id.txtDesc);
        txtid= (TextView) findViewById(R.id.txtid);
        txtname= (TextView) findViewById(R.id.txtName);
        txtarab= (TextView) findViewById(R.id.txtArab);
        txtIndonesia= (TextView) findViewById(R.id.txtIndonesia);
        txtFile= (TextView) findViewById(R.id.txtFile);
        getData();

        buttonStart = (Button) findViewById(R.id.btnPlay);
        buttonSave = (Button) findViewById(R.id.btnSimpan);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        buttonStart.setOnClickListener(this);
        buttonSave.setOnClickListener(this);

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        //useri mage
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/com.garudatekno.jemaah/images/profile.png");
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
        Intent intent = new Intent(ViewPanduankamus.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void getData(){
        class GetData extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewPanduankamus.this,"","Tunggu...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showData(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_ID, id);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(AppConfig.URL_KAMUS, data);
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
            String id = c.getString(AppConfig.KEY_ID);
            String name = c.getString(AppConfig.KEY_NAME);
            String arti = c.getString(AppConfig.KEY_TRANSLATION);
            String arab = c.getString(AppConfig.KEY_ARAB);
            String ina = c.getString(AppConfig.KEY_INDONESIA);
            String file = c.getString(AppConfig.KEY_FILE);
            txtid.setText(id);
            txtname.setText("X  "+name);
            txtdesc.setText(arti);
            txtarab.setText(arab);
            txtIndonesia.setText(ina);
            txtFile.setText(file);

            getJSON(id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        if(v == buttonStart){
            if(buttonStart.getText().toString().trim().equals("Mainkan Audio")){
                mediaPlayer.reset();
                file=txtFile.getText().toString().trim();
                srcPath= AppConfig.URL_HOME+"/uploads/panduan/kamus/"+file;
                Log.d("PATH", srcPath);
                 new Player()
                            .execute(srcPath);
                    mediaPlayer.start();
                buttonStart.setText("Stop");
                playPause = true;
            }else{
                buttonStart.setText("Mainkan Audio");
                mediaPlayer.stop();
                mediaPlayer.reset();
                playPause = false;
            }
        }


    }

    class Player extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progress;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {

                mediaPlayer.setDataSource(params[0]);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        intialStage = true;
                        playPause=false;
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Log.d("IllegarArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progress.isShowing()) {
                progress.cancel();
            }
            Log.d("Prepared", "//" + result);
            mediaPlayer.start();

            intialStage = false;
        }

        public Player() {
            progress = new ProgressDialog(ViewPanduankamus.this);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
//            this.progress.setMessage("Playing...");
//            this.progress.show();

        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
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
                String jenis = "Kamus";
                String file = jo.getString(AppConfig.KEY_FILE);
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_ID,id);
                data.put(AppConfig.KEY_NAME,name);
                data.put(AppConfig.KEY_JENIS,jenis);
                data.put(AppConfig.KEY_FILE,file);
                list.add(data);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomListPanduan1 adapter = new CustomListPanduan1(this, list,
                R.layout.list_panduan, new String[] { AppConfig.KEY_ID,AppConfig.KEY_NAME,AppConfig.KEY_JENIS,AppConfig.KEY_FILE },
                new int[] { R.id.txtNO,R.id.txtNAME,R.id.txtImg });
        listView.setAdapter(adapter);

        listView.setAdapter(adapter);
        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
    }

    private void getJSON(final String Ids){
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
                data.put(AppConfig.KEY_ID, Ids);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(AppConfig.URL_LIST_KAMUS, data);
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
        Intent i = new Intent(getApplicationContext(), ViewPanduankamus.class);
        i.putExtra(AppConfig.EMP_ID,strID);
        finish();
        startActivity(i);
    }

}
