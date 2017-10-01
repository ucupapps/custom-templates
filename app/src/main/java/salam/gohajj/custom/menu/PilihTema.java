package salam.gohajj.custom.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import salam.gohajj.custom.GenericPopup;
import salam.gohajj.custom.GetTemplates;
import salam.gohajj.custom.Interfaces;
import salam.gohajj.custom.R;
import salam.gohajj.custom.Utilities;

/**
 * Created by ucup on 26/08/17.
 */

public class PilihTema extends AppCompatActivity implements View.OnClickListener {
    private Activity activity;
    private RelativeLayout btn_temp1,btn_temp2,btn_temp3,btn_temp4;

    private LinearLayout footerMenu;
    private FloatingActionMenu floatingMenu;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.content_main);
        final RelativeLayout rel_header = (RelativeLayout) findViewById(R.id.header2);
        String getpref = Utilities.getPref("id_pref",activity)!=null? Utilities.getPref("id_pref",activity):"";
        rel_header.setBackground(getResources().getDrawable(GetTemplates.GetHeaderTemplates(getpref)));
        GetTemplates.GetStatusBar(activity);
        ChooseMenu();
        final ImageView img_home=(ImageView) findViewById(R.id.img_home);
        final ImageView img_setting=(ImageView) findViewById(R.id.img_setting);

        btn_temp1 = (RelativeLayout) findViewById(R.id.rel_template1);
        btn_temp2 = (RelativeLayout) findViewById(R.id.rel_template2);
        btn_temp3 = (RelativeLayout) findViewById(R.id.rel_template3);
        btn_temp4 = (RelativeLayout) findViewById(R.id.rel_template4);

        btn_temp1.setOnClickListener(this);
        btn_temp2.setOnClickListener(this);
        btn_temp3.setOnClickListener(this);
        btn_temp4.setOnClickListener(this);

        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
                finish();
            }
        });
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), setting.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.rel_template1:
                GenericPopup.setId_pref(Interfaces.TEMPLATE_DEFAULT);
                Intent i = new Intent(activity, AndroidImageSlider.class);
                startActivity(i);
                break;

            case R.id.rel_template2:
                GenericPopup.setId_pref(Interfaces.TEMPLATE_1);
                Intent j = new Intent(activity, AndroidImageSlider.class);
                startActivity(j);
                break;

            case R.id.rel_template3:
                GenericPopup.setId_pref(Interfaces.TEMPLATE_2);
                Intent k = new Intent(activity, AndroidImageSlider.class);
                startActivity(k);
                break;

            case R.id.rel_template4:
                GenericPopup.setId_pref(Interfaces.TEMPLATE_3);
                Intent l = new Intent(activity, AndroidImageSlider.class);
                startActivity(l);
                break;
        }
    }

    public void ChooseMenu(){
        String getpref = Utilities.getPref("id_pref",activity)!=null? Utilities.getPref("id_pref",activity):"";
        footerMenu = (LinearLayout)findViewById(R.id.menufooter);
        floatingMenu=(FloatingActionMenu)findViewById(R.id.fabmenu);
        if (getpref.equals(Interfaces.TEMPLATE_1)||getpref.equals(Interfaces.TEMPLATE_3)){
            floatingMenu();
        }else footerMenu();

    }

    public void footerMenu(){
        floatingMenu.setVisibility(View.GONE);
        footerMenu.setVisibility(View.VISIBLE);
        // FOOTER
        LinearLayout menu_panduan=(LinearLayout) findViewById(R.id.menu_panduan);
        TextView txt_panduan=(TextView) findViewById(R.id.txt_panduan);
        LinearLayout menu_doa=(LinearLayout) findViewById(R.id.menu_doa);
        TextView txt_doa=(TextView) findViewById(R.id.txt_doa);
        LinearLayout menu_navigasi=(LinearLayout) findViewById(R.id.menu_navigasi);
        TextView txt_navigasi=(TextView) findViewById(R.id.txt_emergency);
        LinearLayout menu_profile=(LinearLayout) findViewById(R.id.menu_profile);
        TextView txt_profile=(TextView) findViewById(R.id.txt_profile);
        LinearLayout menu_inbox=(LinearLayout) findViewById(R.id.menu_inbox);
        TextView txt_inbox=(TextView) findViewById(R.id.txt_inbox);
    }

    public void floatingMenu(){
        footerMenu.setVisibility(View.GONE);
        floatingMenu.setVisibility(View.VISIBLE);
        // New FAB
        FloatingActionButton fab1=(FloatingActionButton)findViewById(R.id.fabpanduan);
        FloatingActionButton fab2=(FloatingActionButton)findViewById(R.id.fabdoa);
        FloatingActionButton fab3=(FloatingActionButton)findViewById(R.id.fabnavigation);
        FloatingActionButton fab4=(FloatingActionButton)findViewById(R.id.fabinbox);
        FloatingActionButton fab5=(FloatingActionButton)findViewById(R.id.fabprofile);
    }

}
