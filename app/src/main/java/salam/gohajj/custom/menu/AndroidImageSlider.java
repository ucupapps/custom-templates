package salam.gohajj.custom.menu;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import salam.gohajj.custom.GenericPopup;
import salam.gohajj.custom.R;


public class AndroidImageSlider extends AppCompatActivity {
    private Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_image);
        mActivity = this;
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPageAndroid);
        AndroidImageAdapter adapterView = new AndroidImageAdapter(this);
        mViewPager.setAdapter(adapterView);

        final TextView tombol = (TextView) findViewById(R.id.choose_template);

            tombol.setVisibility(View.VISIBLE);
            tombol.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            tombol.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GenericPopup.Init(mActivity, getResources().getString(R.string.konfirmasi), getResources().getString(R.string.konfirmasi_tema), GenericPopup.CONFIRM_BUTTON);
                    GenericPopup.Show();
                }
            });



    }
}