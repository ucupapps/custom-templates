package com.garudatekno.jemaah.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.menu.download;
import com.garudatekno.jemaah.menu.profile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.garudatekno.jemaah.menu.download.DIALOG_DOWNLOAD_PROGRESS;

/**
 * Created by Master on 5/1/2016.
 */
public class CustomListPanduan3 extends SimpleAdapter {

    private Context mContext;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private Button startBtn;
    private ProgressDialog mProgressDialog;
    public LayoutInflater inflater=null;
    public CustomListPanduan3(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_panduan, null);

        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
        TextView no = (TextView)vi.findViewById(R.id.txtNO);
        final TextView name = (TextView)vi.findViewById(R.id.txtNAME);
        final TextView txtaudio = (TextView)vi.findViewById(R.id.txtAudio);

        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/helvetica.ttf");
        name.setTypeface(font);
        txtaudio.setTypeface(font);

        final String strID = (String) data.get(AppConfig.KEY_ID);
        String strName = (String) data.get(AppConfig.KEY_NAME);
        no.setText(strID);
        name.setText(strName);

        File file = new File("/sdcard/android/data/com.garudatekno.jemaah/panduan3/"+strID+".mp3");

        if (!file.exists()) {
            txtaudio.setText("Download");
            txtaudio.setBackgroundResource(R.drawable.button_blue);
            txtaudio.setPadding(10,10,10,10);
        }else{
            txtaudio.setText("Buka");
        }

        return vi;
    }

}