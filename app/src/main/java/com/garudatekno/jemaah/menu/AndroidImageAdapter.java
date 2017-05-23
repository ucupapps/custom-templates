package com.garudatekno.jemaah.menu;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.garudatekno.jemaah.R;

public class AndroidImageAdapter extends PagerAdapter {
    Context mContext;

    AndroidImageAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return sliderImagesId.length;
    }

    private int[] sliderImagesId = new int[]{
            R.drawable.slide1, R.drawable.slide2, R.drawable.slide3
    };


    @Override
    public Object instantiateItem(View collection, int i) {

        LayoutInflater inflater = (LayoutInflater) collection.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pager_konten, null);
        ImageView image = (ImageView) view.findViewById(R.id.gambar);
        Button tombol = (Button) view.findViewById(R.id.tombol);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setImageResource(sliderImagesId[i]);
        ((ViewPager) collection).addView(view, 0);
        if(i==2){
            tombol.setVisibility(View.VISIBLE);
        }
        tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, panduan.class);
                mContext.startActivity(i);
            }
        });
//        Button newButton = new Button(mContext);
//        newButton.setText("Go Home");
//        ((ViewPager) container).addView(newButton, 0);
        Log.e("ID", "instantiateItem: "+i );
        return view;
    }


    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);

    }


    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);

    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}