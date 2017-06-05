package salam.gohajj.id.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import salam.gohajj.id.R;
import salam.gohajj.id.app.AppConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Master on 5/1/2016.
 */
public class CustomListPanduan2 extends SimpleAdapter {

    private Context mContext;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private Button startBtn;
    private ProgressDialog mProgressDialog;
    public LayoutInflater inflater=null;
    public CustomListPanduan2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
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
        final TextView txtImg = (TextView)vi.findViewById(R.id.txtImg);
        final TextView txtJenis = (TextView)vi.findViewById(R.id.txtJenis);

        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/helvetica.ttf");
        name.setTypeface(font);
        txtaudio.setTypeface(font);
        txtJenis.setTypeface(font);
        txtImg.setTypeface(font);

        final String strID = (String) data.get(AppConfig.KEY_ID);
        String strName = (String) data.get(AppConfig.KEY_NAME);
        String strJenis = (String) data.get(AppConfig.KEY_JENIS);
        no.setText(strID);
        name.setText(strName);
//        name.setTypeface(null, Typeface.BOLD);
        txtImg.setText(strJenis);

        if(strJenis.equals("Video")){
            txtImg.setBackgroundResource(R.drawable.circle_blue);
            txtJenis.setText("Video Tutorial");
        }else if(strJenis.equals("Doa")){
            txtImg.setBackgroundResource(R.drawable.circle_purple);
            txtJenis.setText("Panduan Doa");
        }else if(strJenis.equals("Tips")){
            txtImg.setBackgroundResource(R.drawable.circle_orange_muda);
            txtJenis.setText("Tips");
        }else if(strJenis.equals("Kamus")){
            txtImg.setBackgroundResource(R.drawable.circle_chocolate);
            txtJenis.setText("Kamus");
        }

//        File file = new File("/sdcard/android/data/com.gohajj.id/panduan2/"+strID+".mp3");
//
//        if (!file.exists()) {
//            txtaudio.setText("Download");
//            txtaudio.setBackgroundResource(R.drawable.button_blue);
//            txtaudio.setPadding(10,10,10,10);
//        }else{
            txtaudio.setText("Buka");
//        }

        return vi;
    }

}