package salam.gohajj.custom.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import salam.gohajj.custom.GenericPopup;
import salam.gohajj.custom.Interfaces;
import salam.gohajj.custom.R;


public class AndroidImageAdapter extends PagerAdapter {
    Context mContext;
    Activity mActivity;

    AndroidImageAdapter(Context context) {
        this.mContext = context;
    }

    AndroidImageAdapter(Activity activity) {
        this.mActivity = activity;
    }
    @Override
    public int getCount() {
        int slider_img;
        String img_temp = GenericPopup.getId_pref();
        if (img_temp==null){
            img_temp = Interfaces.TEMPLATE_DEFAULT;
        }
        if (img_temp.equals(Interfaces.TEMPLATE_1)) {
            slider_img = sliderTemplate1.length;
        }else if (img_temp.equals(Interfaces.TEMPLATE_2)){
            slider_img = sliderTemplate2.length;
        }else if (img_temp.equals(Interfaces.TEMPLATE_3)){
            slider_img = sliderTemplate3.length;
        }else if (img_temp.equals(Interfaces.TEMPLATE_4)){
            slider_img = sliderTemplate4.length;
        }else
            slider_img = sliderImagesId.length;
        return slider_img;
    }

    private int[] sliderImagesId = new int[]{
            R.drawable.slide1, R.drawable.slide2, R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide6
    };

    private int[] sliderTemplate1 = new int[]{
            R.drawable.slide1, R.drawable.slide2, R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide6
    };

    private int[] sliderTemplate2 = new int[]{
            R.drawable.slide1, R.drawable.slide2, R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide6
    };

    private int[] sliderTemplate3 = new int[]{
            R.drawable.slide1, R.drawable.slide2, R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide6
    };

    private int[] sliderTemplate4 = new int[]{
            R.drawable.slide1, R.drawable.slide2, R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide6
    };

    @Override
    public Object instantiateItem(View collection, int i) {

        LayoutInflater inflater = (LayoutInflater) collection.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pager_konten, null);
        ImageView image = (ImageView) view.findViewById(R.id.gambar);
        TextView tombol = (TextView) view.findViewById(R.id.tombol);

        String img_temp = GenericPopup.getId_pref();
        if (img_temp==null){
            image.setImageResource(sliderImagesId[i]);
            if (i == 5) {
                tombol.setVisibility(View.VISIBLE);
                tombol.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            }
        }else if (img_temp.equals(Interfaces.TEMPLATE_1)) {
            image.setImageResource(sliderTemplate1[i]);
        }else if (img_temp.equals(Interfaces.TEMPLATE_2)) {
            image.setImageResource(sliderTemplate2[i]);
        }else if (img_temp.equals(Interfaces.TEMPLATE_3)) {
            image.setImageResource(sliderTemplate3[i]);
        }else if (img_temp.equals(Interfaces.TEMPLATE_4)) {
            image.setImageResource(sliderTemplate4[i]);
        }else {
            image.setImageResource(sliderImagesId[i]);
        }
        ((ViewPager) collection).addView(view, 0);
        tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(mActivity, panduan.class);
            mActivity.startActivity(i);

            }
        });
//        Button newButton = new Button(mContext);
//        newButton.setText("Go Home");
//        ((ViewPager) container).addView(newButton, 0);
//        Log.e("ID", "instantiateItem: "+i );
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