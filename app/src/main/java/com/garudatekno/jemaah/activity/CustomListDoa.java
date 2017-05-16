package com.garudatekno.jemaah.activity;


import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.menu.TitipanDoa;
import com.garudatekno.jemaah.menu.items;

import java.util.ArrayList;

/**
 * Created by Master on 5/1/2016.
 */
public class CustomListDoa extends ArrayAdapter<items> {

    private Context mContext;
    public LayoutInflater inflater=null;
    ArrayList<items> listData = new ArrayList<>();
    public CustomListDoa(Context context, int textViewResourceId, ArrayList<items> objects) {
        super(context, textViewResourceId, objects);
        this.mContext = context;
//        mContext = context;
        listData = objects;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
//    public CustomListDoa(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
//        super(context, data, resource, from, to);
//        mContext = context;
//        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null) {
            vi = View.inflate(mContext,R.layout.list_doa, null);
        }

//        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
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

        final String strID = listData.get(position).getId();
        String strMessage = listData.get(position).getMessage();
        String strTime = listData.get(position).getTime();
        String strFrom = listData.get(position).getFrom();
        String strJum = listData.get(position).getJum();
        no.setText(strID);
        message.setText(strMessage);
        time.setText(strTime);
        from.setText(strFrom);
        jum.setText(strJum);

        btndoakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                notifyDataSetChanged();
                TitipanDoa d= new TitipanDoa();
                d.addDoakan(strID);
//                Intent intent= new Intent(mContext, TitipanDoa.class);
//                ((Activity)mContext).finish();
//                mContext.startActivity(intent);
            }


        });

        return vi;
    }



}