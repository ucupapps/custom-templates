package com.garudatekno.jemaah.activity;

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
            vi = inflater.inflate(R.layout.list_item, null);

        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
//        TextView uid = (TextView)vi.findViewById(R.id.uid);
        TextView from = (TextView)vi.findViewById(R.id.from);
        TextView to = (TextView)vi.findViewById(R.id.to);
        TextView date = (TextView)vi.findViewById(R.id.date);
        String id = (String) data.get(AppConfig.KEY_ID);
        String strFrom = (String) data.get(AppConfig.KEY_FROM);
        String strTo = (String) data.get(AppConfig.KEY_TO);
        String StrDate = (String) data.get(AppConfig.KEY_DATE);
//        uid.setText(id);
        from.setText(strFrom);
        to.setText(strTo);
        date.setText(StrDate);
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