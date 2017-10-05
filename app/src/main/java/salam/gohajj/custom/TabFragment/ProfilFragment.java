package salam.gohajj.custom.TabFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.readystatesoftware.viewbadger.BadgeView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;
import salam.gohajj.custom.Interfaces;
import salam.gohajj.custom.R;
import salam.gohajj.custom.Utilities;
import salam.gohajj.custom.activity.LoginActivity;
import salam.gohajj.custom.activity.RequestHandler;
import salam.gohajj.custom.app.AppConfig;
import salam.gohajj.custom.app.AppController;
import salam.gohajj.custom.helper.SQLiteHandler;
import salam.gohajj.custom.helper.SessionManager;
import salam.gohajj.custom.menu.edit_profile;


public class ProfilFragment extends Fragment implements View.OnClickListener {
    private TextView txtpemimpin,txtName, txtPhone, txtPassport, editTextuser, txtEmail,txtAddress,txtTwon,txtProvince,txttravel,txtmekkah,txtmadinah,txtpembimbing,
            txtTravelPhone,txtPemimpinPhone,txtPembimbingPhone,txtEmailProfile,pFamily1,eFamily1,pFamily2,eFamily2,pFamily3,eFamily3;
    private Button buttonAdd, buttonLogout,buttonpembimbing,buttonPemimpin;
    private CircleImageView imgProfile,imgAgentProfile,imgPemimpinProfile,imgPembimbingProfile;
    String lat,lng,uid,emailUser;
    //user
    private SQLiteHandler db;
    private SessionManager session;
    View target ;
    BadgeView badge ;
    private SQLiteDatabase database;
    private String TAG = "Profil";

    private View footerView;
    public ProfilFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Utilities.ShowLog(TAG,TAG);
        database = getActivity().openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.tab_profile, container, false);
        session = new SessionManager(getContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        File folder = new File("/sdcard/android/data/salam.gohajj.custom/images");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        Calligrapher calligrapher=new Calligrapher(getContext());
        calligrapher.setFont(getActivity(),"fonts/helvetica.ttf",true);
        final LinearLayout konten= (LinearLayout) vi.findViewById(R.id.konten);
        final TextView txtkoneksi= (TextView) vi.findViewById(R.id.txtkoneksi);

        if (!Utilities.cek_status(getActivity()))
        {
            txtkoneksi.setVisibility(View.VISIBLE);
        }else{
            txtkoneksi.setVisibility(View.GONE);
        }

        //tracker
        AppController application = (AppController) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        // [START screen_view_hit]
        mTracker.setScreenName("Profile");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
        // SqLite database handler
        db = new SQLiteHandler(getContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        database = getActivity().openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
//        CountInbox();
//        session = new SessionManager(getApplicationContext());

        emailUser = user.get("email");

        footerView = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, null, false);
        //CONTENT
                txtName = (TextView) vi.findViewById(R.id.name);
        txtAddress = (TextView) vi.findViewById(R.id.address);
        txtPhone = (TextView) vi.findViewById(R.id.phone);
        txtPassport = (TextView) vi.findViewById(R.id.passport);
        txtProvince = (TextView) vi.findViewById(R.id.province);
        txttravel = (TextView) vi.findViewById(R.id.travel_agent);
        txtTravelPhone = (TextView) vi.findViewById(R.id.travel_agent_phone);
//        txtmekkah = (TextView) vi.findViewById(R.id.hotel_mekkah);
//        txtmadinah = (TextView) vi.findViewById(R.id.hotel_madinah);
        txtpembimbing = (TextView) vi.findViewById(R.id.pembimbing);
        txtPembimbingPhone = (TextView) vi.findViewById(R.id.pembimbing_phone);
        txtTwon = (TextView) vi.findViewById(R.id.town);
        editTextuser = (TextView) vi.findViewById(R.id.userid);
        txtpemimpin = (TextView) vi.findViewById(R.id.pemimpin);
        txtPemimpinPhone = (TextView) vi.findViewById(R.id.pemimpin_phone);
        txtEmailProfile = (TextView) vi.findViewById(R.id.emailProfile);
        pFamily1 = (TextView) vi.findViewById(R.id.pFamily1);
        eFamily1 = (TextView) vi.findViewById(R.id.eFamily1);
        pFamily2 = (TextView) vi.findViewById(R.id.pFamily2);
        eFamily2 = (TextView) vi.findViewById(R.id.eFamily2);
        pFamily3 = (TextView) vi.findViewById(R.id.pFamily3);
        eFamily3 = (TextView) vi.findViewById(R.id.eFamily3);

        if (session.isLoggedIn()) {
            getData();
        }
        buttonAdd = (Button) vi.findViewById(R.id.buttonAdd);
        buttonLogout = (Button) vi.findViewById(R.id.buttonLogout);
//        buttonpembimbing = (Button) vi.findViewById(R.id.btnpembimbing);
//        buttonPemimpin = (Button) vi.findViewById(R.id.btnpemimpin);

        imgProfile = (CircleImageView) vi.findViewById(R.id.imageProfile);
        imgAgentProfile = (CircleImageView) vi.findViewById(R.id.imageAgentProfile);
        imgPembimbingProfile = (CircleImageView) vi.findViewById(R.id.imagePembimbingProfile);
        imgPemimpinProfile = (CircleImageView) vi.findViewById(R.id.imagePemimpinProfile);

//        buttonPemimpin.setOnClickListener(this);
//        buttonpembimbing.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

        return vi;
    }

    public void onClick(View v){
        if(v == buttonAdd){
            Intent i = new Intent(getContext(), edit_profile.class);
            startActivity(i);
        }if(v == buttonLogout){
            logoutUser();
        }
//        if(v == buttonpembimbing){
//            Intent i = new Intent(getApplicationContext(), PenilaianPembimbing.class);
//            startActivity(i);
//        }if(v == buttonPemimpin){
//            Intent i = new Intent(getApplicationContext(), PenilaianPemimpinTur.class);
//            startActivity(i);
//        }

    }

    private void logoutUser() {
        if (session.isLoggedIn()) {
            session.setLogin(false);
            Utilities.putPref("id_pref", Interfaces.TEMPLATE_DEFAULT,getContext());
            db.deleteUsers();
        }

        // Launching the login activity
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    private void getData(){
        class GetData extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getContext(),"",getResources().getString(R.string.mohon_tunggu)+"...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showData(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(AppConfig.URL_GETPROFILE,uid);
                return s;
            }
        }
        GetData ge = new GetData();
        ge.execute();
    }

    private void showData(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(AppConfig.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            Log.e(AppConfig.TAG_JSON_ARRAY, c.getString(AppConfig.KEY_NAME));
            String name = c.getString(AppConfig.KEY_NAME);
            String address = c.getString(AppConfig.KEY_ADDRESS);
            String passport = c.getString(AppConfig.KEY_PASSPORT);
            String phone = c.getString(AppConfig.KEY_PHONE);
            String province = c.getString(AppConfig.KEY_PROVINCE);
            String town = c.getString(AppConfig.KEY_TOWN);
            String travel = c.getString(AppConfig.KEY_TRAVEL_AGENT);
            String travel_phone = c.getString(AppConfig.KEY_TRAVEL_PHONE);
            String mekkah = c.getString(AppConfig.KEY_HOTEL_MEKKAH);
            String madinah = c.getString(AppConfig.KEY_HOTEL_MADINAH);
            String pembimbing = c.getString(AppConfig.KEY_PEMBIMBING);
            String pembimbing_phone = c.getString(AppConfig.KEY_PEMBIMBING_PHONE);
//            String nilai_pemb = c.getString(AppConfig.KEY_NILAI_PEMBIMBING);
            String pemimpin = c.getString(AppConfig.KEY_PEMIMPIN_TUR);
            String pemimpin_phone = c.getString(AppConfig.KEY_PEMIMPIN_PHONE);
            String tfamily1 = c.getString(AppConfig.KEY_PHONE_FAMILY1);
            String tfamily2 = c.getString(AppConfig.KEY_PHONE_FAMILY2);
            String tfamily3 = c.getString(AppConfig.KEY_PHONE_FAMILY3);
            String efamily1 = c.getString(AppConfig.KEY_EMAIL_FAMILY1);
            String efamily2 = c.getString(AppConfig.KEY_EMAIL_FAMILY2);
            String efamily3 = c.getString(AppConfig.KEY_EMAIL_FAMILY3);
//            String nilai_pemim = c.getString(AppConfig.KEY_NILAI_PEMIMPIN_TUR);
//            if(!nilai_pemb.equals("-")){
//                buttonpembimbing.setVisibility(View.GONE);
//            }else{
//                buttonpembimbing.setVisibility(View.VISIBLE);
//            }
//
//            if(!nilai_pemim.equals("-")){
//                buttonPemimpin.setVisibility(View.GONE);
//            }else{
//                buttonPemimpin.setVisibility(View.VISIBLE);
//            }

//            if(name.equals(NULL) || name.equals("")) {
//                imgProfile.setImageResource(R.drawable.profile);
//            }else{
            File file = new File("/sdcard/android/data/salam.gohajj.custom/images/"+uid+".png");
            if (!file.exists()) {
                imgProfile.setImageResource(R.drawable.profile);
            }else{
                Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                imgProfile.setImageBitmap(bmp);
            }
//            }

//            Picasso.with(this).load(AppConfig.URL_HOME+"/uploads/profile/"+uid+"/images.jpg").error(R.drawable.profile).into(imgProfile);
            Picasso.with(getContext()).load(AppConfig.URL_HOME+"/uploads/profile/"+uid+"/agent.jpg").memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).error(R.drawable.profile).into(imgAgentProfile);
            Picasso.with(getContext()).load(AppConfig.URL_HOME+"/uploads/profile/"+uid+"/pembimbing.jpg").memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).error(R.drawable.profile).into(imgPembimbingProfile);
            Picasso.with(getContext()).load(AppConfig.URL_HOME+"/uploads/profile/"+uid+"/pemimpin.jpg").memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).error(R.drawable.profile).into(imgPemimpinProfile);

            txtName.setText(name);
            txtAddress.setText(address);
            txtPassport.setText(passport);
            txtPhone.setText(phone);
            txtProvince.setText(province);
            txtTwon.setText(town);
            txttravel.setText(travel);
//            txtmekkah.setText(mekkah);
//            txtmadinah.setText(madinah);
            txtpembimbing.setText(pembimbing);
            txtpemimpin.setText(pemimpin);
            txtPemimpinPhone.setText(pemimpin_phone);
            txtPembimbingPhone.setText(pembimbing_phone);
            txtTravelPhone.setText(travel_phone);
            txtEmailProfile.setText(emailUser);
            pFamily1.setText(tfamily1);
            pFamily2.setText(tfamily2);
            pFamily3.setText(tfamily3);
            eFamily1.setText(efamily1);
            eFamily2.setText(efamily2);
            eFamily3.setText(efamily3);

        } catch (JSONException e) {
//            e.printStackTrace();
        }
    }


}
