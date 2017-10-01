package salam.gohajj.custom.menu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import salam.gohajj.custom.GenericPopup;
import salam.gohajj.custom.R;


public class AndroidLaunchSlider extends AppCompatActivity {
    private Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_image);
        mActivity = this;
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPageAndroid);
        GenericPopup.setId_pref(null);
        AndroidImageAdapter adapterView = new AndroidImageAdapter(this);
        mViewPager.setAdapter(adapterView);

    }
}