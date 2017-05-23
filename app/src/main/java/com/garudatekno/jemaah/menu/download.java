package com.garudatekno.jemaah.menu;

/**
 * Created by bayem on 4/10/2017.
 */

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

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.CustomListPanduan1;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class download extends Activity {

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private Button unduh_doa,unduh_thawaf,unduh_sai,remove,unduh_all;
    private ProgressDialog mProgressDialog;

    private String JSON_STRING;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download);
        unduh_doa =(Button) findViewById(R.id.unduh_doa);
        unduh_sai=(Button) findViewById(R.id.unduh_sai);
        unduh_thawaf =(Button) findViewById(R.id.unduh_thawaf);
        unduh_all =(Button) findViewById(R.id.unduh_all);
        remove =(Button) findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File folder1 = new File("/sdcard/android/data/com.garudatekno.jemaah/doa");
                File folder2 = new File("/sdcard/android/data/com.garudatekno.jemaah/sai");
                File folder3 = new File("/sdcard/android/data/com.garudatekno.jemaah/thawaf");
                deleteDirectory(folder1);
                deleteDirectory(folder2);
                deleteDirectory(folder3);

                Toast.makeText(getApplicationContext(), "Folder berhasil di hapus", Toast.LENGTH_SHORT).show();
            }
        });

        unduh_doa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJSON("doa");
            }
        });

        unduh_sai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJSON("sai");
            }
        });

        unduh_thawaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJSON("thawaf");
            }
        });
//        unduh_all.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getJSON("doa");
//                getJSON("sai");
//                getJSON("thawaf");
//            }
//        });
    }

    private void showJson(final String folder){
        String notif = null;
//        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        if(folder.equals("doa")){
            notif="Download Panduan Doa";
        }else if(folder.equals("thawaf")){
            notif="Download Doa Thawaf";
        }else if(folder.equals("sai")){
            notif="Download Doa Sai";
        }

        try {
            JSONObject jsonObject = null;
            jsonObject = new JSONObject(JSON_STRING);
            final JSONArray result = jsonObject.getJSONArray(AppConfig.TAG_JSON_ARRAY);
            final int jumlah =result.length();
            final int id = 1;
            final NotificationManager mNotifyManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
            mBuilder.setContentTitle(notif)
                    .setSmallIcon(R.drawable.logo_apps);
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            int count;
                            try {
                                for(int i = 0; i<result.length(); i++) {
                                    int no = i+1;
                                    JSONObject jo = result.getJSONObject(i);
                                    String file = jo.getString(AppConfig.KEY_NAME);

                                    mBuilder.setContentText("Download "+no+" dari "+jumlah);

                                    URL url = new URL(AppConfig.URL_HOME + "/uploads/panduan/"+folder+"/"+file);
                                    URLConnection conexion = null;
                                    conexion = url.openConnection();
                                    conexion.connect();

                                    int lenghtOfFile = conexion.getContentLength();
                                    Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                                    InputStream input = new BufferedInputStream(url.openStream());
                                    OutputStream output = new FileOutputStream("/sdcard/android/data/com.garudatekno.jemaah/"+folder+"/"+file);

                                    byte data[] = new byte[1024];

                                    long total = 0;

                                    while ((count = input.read(data)) != -1) {
                                        total += count;
                                        mBuilder.setProgress(100, (int) ((total * 100) / lenghtOfFile), false);
                                        mNotifyManager.notify(id, mBuilder.build());
                                        output.write(data, 0, count);
                                        try {
                                            // Sleep for 5 seconds
                                            Thread.sleep((int) ((total * 100) / lenghtOfFile));
                                        } catch (InterruptedException e) {
                                            Log.d("ERROR", "sleep failure");
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // When the loop is finished, updates the notification
                            mBuilder.setContentText("Download complete")
                                    // Removes the progress bar
                                    .setProgress(0,0,false);
                            mNotifyManager.notify(id, mBuilder.build());
                        }
                    }
            ).start();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getJSON(final String name){
        class GetJSON extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING=s;
                showJson(name);
            }

            @Override
            protected String doInBackground(Void... params) {
                File folder = new File("/sdcard/android/data/com.garudatekno.jemaah/"+name);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(AppConfig.URL_GET_DIR,name);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    public boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }
}
