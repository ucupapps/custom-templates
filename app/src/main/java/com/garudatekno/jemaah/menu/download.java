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
import android.widget.Button;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.app.AppConfig;

public class download extends Activity {

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private Button unduh_doa,unduh_thawaf,unduh_sai;
    private ProgressDialog mProgressDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download);
        unduh_doa =(Button) findViewById(R.id.unduh_doa);
        unduh_sai=(Button) findViewById(R.id.unduh_sai);
        unduh_thawaf =(Button) findViewById(R.id.unduh_thawaf);
        unduh_doa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int id = 1;
                final NotificationManager mNotifyManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                mBuilder.setContentTitle("Download Panduan Doa")
                        .setContentText("Download in progress")
                        .setSmallIcon(R.drawable.logo_apps);
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                int incr;
                                // Do the "lengthy" operation 20 times
                                for (incr = 0; incr <= 100; incr+=5) {
                                    mBuilder.setProgress(100, incr, false);
                                    mNotifyManager.notify(id, mBuilder.build());
                                    try {
                                        // Sleep for 5 seconds
                                        Thread.sleep(5*1000);
                                    } catch (InterruptedException e) {
                                        Log.d("ERROR", "sleep failure");
                                    }
                                }
                                // When the loop is finished, updates the notification
                                mBuilder.setContentText("Download complete")
                                        // Removes the progress bar
                                        .setProgress(0,0,false);
                                mNotifyManager.notify(id, mBuilder.build());
                            }
                        }
                ).start();
            }
        });

        unduh_sai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int id = 1;
                final NotificationManager mNotifyManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                mBuilder.setContentTitle("Download Doa Sai")
                        .setContentText("Download in progress")
                        .setSmallIcon(R.drawable.logo_apps);
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                int incr;
                                // Do the "lengthy" operation 20 times
                                for (incr = 0; incr <= 100; incr+=5) {
                                    mBuilder.setProgress(100, incr, false);
                                    mNotifyManager.notify(id, mBuilder.build());
                                    try {
                                        // Sleep for 5 seconds
                                        Thread.sleep(5*1000);
                                    } catch (InterruptedException e) {
                                        Log.d("ERROR", "sleep failure");
                                    }
                                }
                                // When the loop is finished, updates the notification
                                mBuilder.setContentText("Download complete")
                                        // Removes the progress bar
                                        .setProgress(0,0,false);
                                mNotifyManager.notify(id, mBuilder.build());
                            }
                        }
                ).start();
            }
        });

        unduh_thawaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int id = 1;
                final NotificationManager mNotifyManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                mBuilder.setContentTitle("Download Doa Thawaf")
                        .setContentText("Download in progress")
                        .setSmallIcon(R.drawable.logo_apps);
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                int incr;
                                // Do the "lengthy" operation 20 times
                                for (incr = 0; incr <= 100; incr+=5) {
                                    mBuilder.setProgress(100, incr, false);
                                    mNotifyManager.notify(id, mBuilder.build());
                                    try {
                                        // Sleep for 5 seconds
                                        Thread.sleep(5*1000);
                                    } catch (InterruptedException e) {
                                        Log.d("ERROR", "sleep failure");
                                    }
                                }
                                // When the loop is finished, updates the notification
                                mBuilder.setContentText("Download complete")
                                        // Removes the progress bar
                                        .setProgress(0,0,false);
                                mNotifyManager.notify(id, mBuilder.build());
                            }
                        }
                ).start();
            }
        });

//        //tawaf
//        unduh_thawaf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                File folder = new File("/sdcard/android/data/com.garudatekno.jemaah/thawaf");
//                if (!folder.exists()) {
//                    folder.mkdirs();
//                }
//
//                final int id = 1;
//                final NotificationManager mNotifyManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
//                mBuilder.setContentTitle("Download Doa Thawaf")
//                        .setContentText("Download in progress")
//                        .setSmallIcon(R.drawable.logo_apps);
//                new Thread(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                int count;
//                                final String file="1.mp3";
//                                try {
//                                URL url = new URL(AppConfig.URL_HOME+"/uploads/panduan/thawaf/"+file);
//                                URLConnection conexion = null;
//                                    conexion = url.openConnection();
//
//                                conexion.connect();
//
//                                int lenghtOfFile = conexion.getContentLength();
//                                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
//
//                                InputStream input = new BufferedInputStream(url.openStream());
//                                OutputStream output = new FileOutputStream("/sdcard/android/data/com.garudatekno.jemaah/thawaf/"+file);
//
//                                byte data[] = new byte[1024];
//
//                                long total = 0;
//
//                                    while ((count = input.read(data)) != -1) {
//                                        total += count;
//                                        mBuilder.setProgress(100, (int)((total*100)/lenghtOfFile), false);
//                                        mNotifyManager.notify(id, mBuilder.build());
//                                        output.write(data, 0, count);
//                                        try {
//                                            // Sleep for 5 seconds
//                                            Thread.sleep((int)((total*100)/lenghtOfFile));
//                                        } catch (InterruptedException e) {
//                                            Log.d("ERROR", "sleep failure");
//                                        }
//                                    }
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//
//                                // When the loop is finished, updates the notification
//                                mBuilder.setContentText("Download complete")
//                                        // Removes the progress bar
//                                        .setProgress(0,0,false);
//                                mNotifyManager.notify(id, mBuilder.build());
//                            }
//                        }
//                ).start();
//            }
//        });
    }

    private void startDownload() {
        String url = "https://gohajj.id/uploads/1.mp3";
        new DownloadFileAsync().execute(url);
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
                OutputStream output = new FileOutputStream("/sdcard/android/data/com.garudatekno.jemaah/tes.mp3");

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
}
