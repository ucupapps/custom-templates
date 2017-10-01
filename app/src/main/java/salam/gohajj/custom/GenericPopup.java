package salam.gohajj.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import salam.gohajj.custom.activity.Utilities;
import salam.gohajj.custom.menu.panduan;

/**
 * Created by lukes on 12/14/16.
 */

public class GenericPopup {
    private static Activity _activity;
    private static Context _context;
    private static Dialog _dialog;

    private static Button btOk, btnYes, btnNo;
    private static TextView tvTitle, tvDesc;
    private static ImageView ivImage;
    private static LinearLayout rel_btnOK, rel_btnConfirm;
    private static RelativeLayout rel_popup1;
    public static int WARNING = -1;
    public static int EDIT_BUTTON = -2;
    public static int CONFIRM_BUTTON =-3;
    private static String id_pref;
    public static void Init (final Activity activity, final String tittle, String description, int imageResourceID)
    {
        _activity   = activity;
        _context    = ((Context) activity).getApplicationContext();

        _dialog     = new Dialog(_activity);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.popup_generic);

        tvTitle          = (TextView) _dialog.findViewById(R.id.popup_generic_title);
        tvDesc           = (TextView) _dialog.findViewById(R.id.popup_generic_desc);
        btnYes           = (Button)_dialog.findViewById(R.id.popup_confirm_btn_yes);
        btnNo            = (Button)_dialog.findViewById(R.id.popup_confirm_btn_no);
        rel_btnOK        = (LinearLayout)_dialog.findViewById(R.id.ok_button);
        rel_btnConfirm   = (LinearLayout)_dialog.findViewById(R.id.confirm_button);
        rel_popup1       = (RelativeLayout)_dialog.findViewById(R.id.rel_popup1);
        ivImage          = (ImageView) _dialog.findViewById(R.id.popup_generic_image);

        String getpref = Utilities.getPref("id_pref", _activity);
        btnYes.setBackground(_activity.getResources().getDrawable(GetTemplates.GetHeaderTemplates(getpref)));
        btnNo.setBackground(_activity.getResources().getDrawable(GetTemplates.GetHeaderTemplates(getpref)));
        tvTitle.setText(tittle);
        tvDesc.setText(description);
        if (tittle.equals("")){ tvTitle.setVisibility(View.GONE);}
        if (description.equals("")){ tvDesc.setVisibility(View.GONE);}

        rel_popup1.setVisibility(View.VISIBLE);

        if (imageResourceID == WARNING)
        {
            rel_btnOK.setVisibility(View.VISIBLE);
            ivImage.setImageResource(R.drawable.button_green);
        }else if(imageResourceID == EDIT_BUTTON) {
            rel_popup1.setVisibility(View.GONE);
        }else if (imageResourceID == CONFIRM_BUTTON){
            rel_btnOK.setVisibility(View.GONE);
            ivImage.setVisibility(View.GONE);
            rel_btnConfirm.setVisibility(View.VISIBLE);
        }
        else{
            ivImage.setImageResource(imageResourceID);
        }

        btOk        = (Button) _dialog.findViewById(R.id.popup_generic_btn_ok);
        btOk.setBackground(_activity.getResources().getDrawable(GetTemplates.GetHeaderTemplates(getpref)));
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Hide();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tittle.contains("Konfirmasi !") && getId_pref().equals(Interfaces.TEMPLATE_DEFAULT)){

                    Intent i = new Intent(_activity, panduan.class);
                    Utilities.putPref("id_pref", Interfaces.TEMPLATE_DEFAULT, _activity);
                    _activity.startActivity(i);
                }else if (tittle.contains("Konfirmasi !") && getId_pref().equals(Interfaces.TEMPLATE_1)){

                    Intent i = new Intent(_activity, panduan.class);
                    Utilities.putPref("id_pref", Interfaces.TEMPLATE_1, _activity);
                    _activity.startActivity(i);
                }else if (tittle.contains("Konfirmasi !") && getId_pref().equals(Interfaces.TEMPLATE_2)){

                    Intent i = new Intent(_activity, panduan.class);
                    Utilities.putPref("id_pref", Interfaces.TEMPLATE_2, _activity);
                    _activity.startActivity(i);
                }else if (tittle.contains("Konfirmasi !") && getId_pref().equals(Interfaces.TEMPLATE_3)){

                    Intent i = new Intent(_activity, panduan.class);
                    Utilities.putPref("id_pref", Interfaces.TEMPLATE_3, _activity);
                    _activity.startActivity(i);
                }else if (tittle.contains("Konfirmasi !") && getId_pref().equals(Interfaces.TEMPLATE_4)){

                    Intent i = new Intent(_activity, panduan.class);
                    Utilities.putPref("id_pref", Interfaces.TEMPLATE_4, _activity);
                    _activity.startActivity(i);
                }
                else if (tittle.contains("Anda yakin mau membatalkan?"))
                {
                }else {
                }
                Hide();

            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tittle.contains("Apakah Anda ingin keluar?")) {
                    _activity.finish();
                 }
                Hide();

            }
        });


        _dialog.setCancelable(true);
    }

    public static void Show ()
    {
        _dialog.show();
    }

    public static void Hide ()
    {
        _dialog.dismiss();
    }

    public static String getId_pref() {
        return id_pref;
    }

    public static void setId_pref(String id_pref) {
        GenericPopup.id_pref = id_pref;
    }
}

