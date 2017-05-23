package com.garudatekno.jemaah.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.menu.Poi;
import com.garudatekno.jemaah.menu.navigasi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Master on 5/1/2016.
 */
public class CustomListSejarah extends SimpleAdapter {

    private Context mContext;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private Button startBtn;
    private ProgressDialog mProgressDialog;
    public LayoutInflater inflater=null;
    public CustomListSejarah(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_poi_cat, null);

        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
        TextView no = (TextView)vi.findViewById(R.id.txtIDPoi);
        final TextView name = (TextView)vi.findViewById(R.id.txtNamaPoi);
        final TextView alamat = (TextView)vi.findViewById(R.id.txtalamatPoi);
        Button btnSetPoi = (Button)vi.findViewById(R.id.btnSetPoi);

        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/helvetica.ttf");
        name.setTypeface(font);
        alamat.setTypeface(font);

        final String strID = (String) data.get(AppConfig.KEY_ID);
        String strName = (String) data.get(AppConfig.KEY_NAME);
        String strAlamat = (String) data.get(AppConfig.KEY_ALAMAT);
        String strCat = (String) data.get(AppConfig.KEY_CATEGORY);
        final String strLatPoi = (String) data.get(AppConfig.KEY_LAT);
        final String strLngPoi = (String) data.get(AppConfig.KEY_LNG);
        no.setText(strID);
        name.setText(strName);
        name.setTypeface(null, Typeface.BOLD);
        alamat.setText(strAlamat);

//        if(strJenis.equals("Video")){
//            txtImg.setBackgroundResource(R.drawable.circle_blue);
//            txtJenis.setText("Video Tutorial");
//        }else if(strJenis.equals("Doa")){
//            txtImg.setBackgroundResource(R.drawable.circle_purple);
//            txtJenis.setText("Panduan Doa");
//        }else if(strJenis.equals("Tips")){
//            txtImg.setBackgroundResource(R.drawable.circle_orange_muda);
//            txtJenis.setText("Tips");
//        }else if(strJenis.equals("Kamus")){
//            txtImg.setBackgroundResource(R.drawable.circle_chocolate);
//            txtJenis.setText("Kamus");
//        }


//        File file = new File("/sdcard/android/data/com.garudatekno.jemaah/panduan1/"+strID+".mp3");

//        if (!file.exists()) {
//            txtaudio.setText("Download");
//            txtaudio.setBackgroundResource(R.drawable.button_blue);
//            txtaudio.setPadding(10,10,10,10);
//        }else{
//            txtaudio.setText("Buka");
//        }

        btnSetPoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDataSetChanged();
//                    TitipanDoa d= new TitipanDoa();
//                    d.addDoakan(strID);
//                    list.clear();
//                    addDoakan(strID);
                if(!strLatPoi.equals("null") && !strLngPoi.equals("null")){
                    Poi.insertIntoDB("POI",strLatPoi,strLngPoi);
                    Intent intent = new Intent(mContext, navigasi.class);
                    mContext.startActivity(intent);
                }
//                else {
//                    Intent intent = new Intent(getContext(), com.garudatekno.jemaah.menu.MapsActivity.class);
//                    intent.putExtra(AppConfig.KEY_ID, strID);
//                    intent.putExtra(AppConfig.KEY_LAT, strLat);
//                    intent.putExtra(AppConfig.KEY_LNG, strLng);
//                    getContext().startActivity(intent);
//                }
            }


        });

        return vi;
    }

}