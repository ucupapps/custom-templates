package com.garudatekno.jemaah.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class CustomList extends SimpleAdapter {

    private Context mContext;
    public LayoutInflater inflater=null;
    public CustomList(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_chat, null);

        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
        TextView no = (TextView)vi.findViewById(R.id.txtNO);
        TextView message = (TextView)vi.findViewById(R.id.txtMESSAGE);
        TextView time = (TextView)vi.findViewById(R.id.txtTIME);
        TextView from = (TextView)vi.findViewById(R.id.txtFROM);

        String strID = (String) data.get(AppConfig.KEY_ID);
        String strMessage = (String) data.get(AppConfig.KEY_MESSAGE);
        String strTime = (String) data.get(AppConfig.KEY_TIME);
        String strFrom = (String) data.get(AppConfig.KEY_FROM);
        no.setText(strID);
        no.setVisibility(View.GONE);;
        message.setText(strMessage);
        time.setText(strTime);
        from.setText(strFrom);
//        ImageView imageView = (ImageView) vi.findViewById(R.id.uid);
            // Set a background color for ListView regular row/item
//        if (id.equals("1")) {
//            imageView.setImageResource(R.drawable.ok);
//            text.setClickable(true);
//            imageView.setClickable(true);
//            text.setTextColor(Color.BLACK);
//        } else if (id.equals("2")) {
//            imageView.setImageResource(R.drawable.arrow);
//            text.setClickable(false);
//            imageView.setClickable(true);
//            text.setTextColor(Color.BLACK);
//        } else {
//            imageView.setImageResource(R.drawable.gradient_bg);
//            text.setClickable(true);
//            imageView.setClickable(true);
//        }
//        TextView text2 = (TextView)vi.findViewById(R.id.usertext);
//        String code = (String) data.get(AppConfig.KEY_BARCODE);
//        text2.setText(code);
//        ImageView image=(ImageView)vi.findViewById(R.id.userIcon);
//        String img = (String) data.get(AppConfig.UPLOAD_KEY);
//        Picasso.with(mContext).load(img).into(image);
        return vi;
    }
}