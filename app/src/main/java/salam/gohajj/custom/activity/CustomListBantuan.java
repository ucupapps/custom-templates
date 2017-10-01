package salam.gohajj.custom.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import salam.gohajj.custom.R;
import salam.gohajj.custom.app.AppConfig;
import salam.gohajj.custom.menu.View_PusatBantuan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Master on 5/1/2016.
 */
public class CustomListBantuan extends SimpleAdapter {

    private Context mContext;
    public LayoutInflater inflater=null;
    public CustomListBantuan(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_bantuan, null);

        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
        TextView name = (TextView)vi.findViewById(R.id.txtname);
        LinearLayout linear = (LinearLayout) vi.findViewById(R.id.txtlist);

        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/helvetica.ttf");
        name.setTypeface(font);

        final String strID = (String) data.get(AppConfig.KEY_ID);
        String strTanya = (String) data.get(AppConfig.KEY_PERTANYAAN);
        name.setText(strTanya);

        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(mContext, View_PusatBantuan.class);
                intent.putExtra(AppConfig.EMP_ID,strID);
                mContext.startActivity(intent);
            }


        });

        return vi;
    }



}