package com.garudatekno.jemaah.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.app.AppConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Master on 5/1/2016.
 */
public class CustomListDoa extends SimpleAdapter {

    private Context mContext;
    public LayoutInflater inflater=null;
    public CustomListDoa(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_doa, null);

        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
        TextView no = (TextView)vi.findViewById(R.id.txtNO);
        TextView message = (TextView)vi.findViewById(R.id.txtMESSAGE);
        TextView time = (TextView)vi.findViewById(R.id.txtTIME);
        TextView from = (TextView)vi.findViewById(R.id.txtFROM);
        TextView jum = (TextView)vi.findViewById(R.id.txtJumlah);
        TextView titip = (TextView)vi.findViewById(R.id.txttitip);
        Button btndoakan = (Button)vi.findViewById(R.id.btndoakan);

        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/helvetica.ttf");
        message.setTypeface(font);
        time.setTypeface(font);
        from.setTypeface(font,Typeface.BOLD);
        jum.setTypeface(font);
        titip.setTypeface(font);
        btndoakan.setTypeface(font);

        String strID = (String) data.get(AppConfig.KEY_ID);
        String strMessage = (String) data.get(AppConfig.KEY_MESSAGE);
        String strTime = (String) data.get(AppConfig.KEY_TIME);
        String strFrom = (String) data.get(AppConfig.KEY_FROM);
        String strJum = (String) data.get(AppConfig.KEY_JUMLAH);
        no.setText(strID);
        message.setText(strMessage);
        time.setText(strTime);
        from.setText(strFrom);
        jum.setText(strJum);

        return vi;
    }
}