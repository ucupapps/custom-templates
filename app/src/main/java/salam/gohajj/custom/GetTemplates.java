package salam.gohajj.custom;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by ucup on 25/08/17.
 */

public class GetTemplates{

    public static int ChooseTemplates(String getpref){

        int Layout;
        if (getpref == null || getpref.equals(Interfaces.TEMPLATE_DEFAULT)){
            Layout = R.layout.panduan;
        }else if (getpref.equals(Interfaces.TEMPLATE_1)){
            Layout = R.layout.panduan_custom1;
        }else if (getpref.equals(Interfaces.TEMPLATE_2)){
            Layout = R.layout.panduan_v2;
        }else if (getpref.equals(Interfaces.TEMPLATE_3)){
            Layout = R.layout.panduan_v3;
        }else {
            Layout = R.layout.panduan;
        }
        return Layout;
    }

    public static int GetHeaderTemplates(String getpref){
        int bgHeader;
        if (getpref == null || getpref.equals(Interfaces.TEMPLATE_DEFAULT)){
            bgHeader = R.drawable.background;
        }else if (getpref.equals(Interfaces.TEMPLATE_1)){
            bgHeader = R.drawable.background_orange;
        }else if (getpref.equals(Interfaces.TEMPLATE_2)){
            bgHeader = R.drawable.background_blue;
        }else if (getpref.equals(Interfaces.TEMPLATE_3)){
            bgHeader = R.drawable.background_purple;
        }else {
            bgHeader = R.drawable.background;
        }
        return bgHeader;
    }

    public static int GetListPanduan(String getpref, String strJenis){
        int btnColor=0;
        if(strJenis.equals("Video")){
            if (getpref==null||getpref.equals(Interfaces.TEMPLATE_DEFAULT)) {
                btnColor = R.drawable.circle_blue;
            }else if (getpref.equals(Interfaces.TEMPLATE_1)){
                btnColor = R.drawable.circle_red_custom;
            }else if (getpref.equals(Interfaces.TEMPLATE_2)){
                btnColor = R.drawable.circle_red;
            }else if (getpref.equals(Interfaces.TEMPLATE_3)){
                btnColor = R.drawable.circle_chocolate;
            }else if (getpref.equals(Interfaces.TEMPLATE_4)){
                btnColor = R.drawable.circle_orange_muda;
            }else btnColor = R.drawable.circle_blue;

        }else if(strJenis.equals("Doa")){
            if (getpref==null||getpref.equals(Interfaces.TEMPLATE_DEFAULT)) {
                btnColor = R.drawable.circle_purple;
            }else if (getpref.equals(Interfaces.TEMPLATE_1)){
                btnColor = R.drawable.circle_red;
            }else if (getpref.equals(Interfaces.TEMPLATE_2)){
                btnColor = R.drawable.circle_red_custom;
            }else if (getpref.equals(Interfaces.TEMPLATE_3)){
                btnColor = R.drawable.circle_orange_muda;
            }else if (getpref.equals(Interfaces.TEMPLATE_4)){
                btnColor = R.drawable.circle_chocolate;
            }else btnColor = R.drawable.circle_purple;

        }else if(strJenis.equals("Tips")){
            if (getpref==null||getpref.equals(Interfaces.TEMPLATE_DEFAULT)) {
                btnColor = R.drawable.circle_orange_muda;
            }else if (getpref.equals(Interfaces.TEMPLATE_1)){
                btnColor = R.drawable.circle_purple;
            }else if (getpref.equals(Interfaces.TEMPLATE_2)){
                btnColor = R.drawable.circle_red;
            }else if (getpref.equals(Interfaces.TEMPLATE_3)){
                btnColor = R.drawable.circle_blue;
            }else if (getpref.equals(Interfaces.TEMPLATE_4)){
                btnColor = R.drawable.circle_orange;
            }else btnColor = R.drawable.circle_orange_muda;
        }else if(strJenis.equals("Kamus")){
            if (getpref==null||getpref.equals(Interfaces.TEMPLATE_DEFAULT)) {
                btnColor = R.drawable.circle_chocolate;
            }else if (getpref.equals(Interfaces.TEMPLATE_1)){
                btnColor = R.drawable.circle_red;
            }else if (getpref.equals(Interfaces.TEMPLATE_2)){
                btnColor = R.drawable.circle_purple;
            }else if (getpref.equals(Interfaces.TEMPLATE_3)){
                btnColor = R.drawable.circle_orange;
            }else if (getpref.equals(Interfaces.TEMPLATE_4)){
                btnColor = R.drawable.circle_blue;
            }else btnColor = R.drawable.circle_chocolate;
        }
        return btnColor;
    }

    public static int GetTextJenisPanduan(String strJenis){
        int Jenis = 0;
        if(strJenis.equals("Video")){
            Jenis = R.string.video_tutorial;
        }else if(strJenis.equals("Doa")){
            Jenis = R.string.panduan_doa;
        }else if(strJenis.equals("Tips")){
            Jenis = R.string.tips;
        }else if(strJenis.equals("Kamus")){
            Jenis = R.string.kamus;
        }
        return Jenis;
    }

    public static void GetStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            String id_pref = Utilities.getPref("id_pref",activity);
            if (id_pref == null || id_pref.equals(Interfaces.TEMPLATE_DEFAULT)) {
                window.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            }else if (id_pref.equals(Interfaces.TEMPLATE_1)){
                window.setStatusBarColor(activity.getResources().getColor(R.color.orange));
            }else if (id_pref.equals(Interfaces.TEMPLATE_2)){
                window.setStatusBarColor(activity.getResources().getColor(R.color.blue));
            }else if (id_pref.equals(Interfaces.TEMPLATE_3)){
                window.setStatusBarColor(activity.getResources().getColor(R.color.colorAccent));
            }else if (id_pref.equals(Interfaces.TEMPLATE_4)){
                window.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            }else
                window.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        }
    }
}
