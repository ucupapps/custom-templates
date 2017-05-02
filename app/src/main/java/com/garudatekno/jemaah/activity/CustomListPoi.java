package com.garudatekno.jemaah.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.app.AppConfig;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Master on 5/1/2016.
 */
public class CustomListPoi extends SimpleAdapter {

    private Context mContext;
    public LayoutInflater inflater=null;
    public CustomListPoi(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_poi, null);

        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
        TextView no = (TextView)vi.findViewById(R.id.txtNO);
        TextView name = (TextView)vi.findViewById(R.id.txtNAME);
        TextView desc = (TextView)vi.findViewById(R.id.txtDESC);
        TextView category = (TextView)vi.findViewById(R.id.txtCATEGORY);
        TextView distance = (TextView)vi.findViewById(R.id.txtDISTANCE);

        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/helvetica.ttf");
        name.setTypeface(font);
        desc.setTypeface(font);
        category.setTypeface(font);

        String strID = (String) data.get(AppConfig.KEY_ID);
        String strName = (String) data.get(AppConfig.KEY_NAME);
        String strDesc = (String) data.get(AppConfig.KEY_DESCRIPTION);
        String strCategory = (String) data.get(AppConfig.KEY_CATEGORY);
        String strDist = (String) data.get(AppConfig.KEY_DISTANCE);

        no.setText(strID);
        name.setText(strName);
        desc.setText(strDesc);
        category.setText(strCategory);
        distance.setText(strDist);

        return vi;
    }
}