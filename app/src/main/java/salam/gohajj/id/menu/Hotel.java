package salam.gohajj.id.menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import salam.gohajj.id.R;
import salam.gohajj.id.activity.LoginActivity;
import salam.gohajj.id.activity.RequestHandler;
import salam.gohajj.id.app.AppConfig;
import salam.gohajj.id.helper.SQLiteHandler;
import salam.gohajj.id.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;

public class Hotel extends AppCompatActivity implements ListView.OnItemClickListener {

    private static final String TAG = "MyUser";

    private LinearLayout doaLinear;

    private ListHotel adapter;

    private ArrayList<items_hotel> list = new ArrayList<>();

    private EditText EditTextCariHotel;

    private Button buttonCariHotel,buttonAddHotel;

    private LinearLayout HotelLinear,NotFoundLinear;

    private ListView listView;
    private String JSON_STRING,uid;
    private SQLiteHandler db;
    private SessionManager session;

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hotel);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        session = new SessionManager(getApplicationContext());
        listView = (ListView) findViewById(R.id.listViewHotel);
        listView.destroyDrawingCache();
        listView.setOnItemClickListener(this);

//        HotelLinear = (LinearLayout) findViewById(R.id.linearHotel);

        //HEADER
        TextView txt_emergency=(TextView) findViewById(R.id.txt_emergency);
        TextView txt_thowaf=(TextView) findViewById(R.id.txt_thowaf);
        TextView txt_sai=(TextView) findViewById(R.id.txt_sai);
        txt_thowaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), thawaf.class);
                startActivity(i);
                finish();
            }
        });
        txt_sai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), sai.class);
                startActivity(i);
                finish();
            }
        });
        txt_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), emergency.class);
                startActivity(i);
                finish();
            }
        });

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

        ImageView img = (ImageView) findViewById(R.id.img_navigasi);
        img.setBackgroundResource(R.drawable.circle_green_active);
        img.setPadding(22,22,22,22);
        img.setImageDrawable(getResources().getDrawable(R.drawable.navigasi_active));

        menu_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), profile.class);
                startActivity(i);
                finish();
            }
        });
        menu_panduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
                finish();
            }
        });
        menu_doa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Hotel.class);
                startActivity(i);
                finish();
            }
        });
        menu_navigasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), navigasi.class);
                startActivity(i);
                finish();
            }
        });
        menu_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), inbox.class);
                startActivity(i);
                finish();
            }
        });

        final ImageView img_home=(ImageView) findViewById(R.id.img_home);
        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
                finish();
            }
        });
        final  ImageView img_setting=(ImageView) findViewById(R.id.img_setting);
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), setting.class);
                startActivity(i);
                finish();
            }
        });

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        createDatabase();

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

        EditTextCariHotel = (EditText) findViewById(R.id.et_hotel);
        buttonCariHotel = (Button) findViewById(R.id.btn_find_hotel);
        buttonAddHotel = (Button) findViewById(R.id.btnAddHotel);

        NotFoundLinear = (LinearLayout) findViewById(R.id.LinearHotelNotFound);

        //useri mage
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/com.gohajj.id/images/profile.png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }
        getJSON();

        buttonCariHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cariHotel = EditTextCariHotel.getText().toString().trim();
                if(EditTextCariHotel.getText().toString().equals("")){
                    list.clear();
                    getJSON();
                }else {
                    list.clear();
                    cariNamaHotel(cariHotel);
                }

            }
        });

        buttonAddHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Hotel.this, add_hotel.class);
                startActivity(intent);
                finish();
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void showData(){
        JSONObject jsonObject = null;
//        ArrayList<items> list = new ArrayList<>();

        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(AppConfig.TAG_JSON_ARRAY);
                for (int i = 0; i < result.length(); i++) {
//                ArrayList<items> list = new ArrayList<>();
                    JSONObject jo = result.getJSONObject(i);
                    String id = jo.getString(AppConfig.KEY_ID);
                    String nama = jo.getString(AppConfig.KEY_NAME);
                    String alamat = jo.getString(AppConfig.KEY_ALAMAT);
                    String ratings = jo.getString(AppConfig.KEY_RATING);
                    String lat_hotel = jo.getString(AppConfig.KEY_LAT);
                    String lng_hotel = jo.getString(AppConfig.KEY_LNG);
//                HashMap<String,String> data = new HashMap<>();
//                data.put(AppConfig.KEY_ID,id);
//                data.put(AppConfig.KEY_MESSAGE,message);
//                data.put(AppConfig.KEY_MESSAGE,message);
//                data.put(AppConfig.KEY_FROM,from);
//                data.put(AppConfig.KEY_JUMLAH,jum);
                    Log.e("tesShow : ", lat_hotel);
                    list.add(new items_hotel(id, nama, alamat, ratings, lat_hotel, lng_hotel));
                }
//                listView.setVisibility(View.GONE);
//                NotFoundLinear.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            Log.e("error SHOW :", e.getMessage());
            e.printStackTrace();
        }

//        adapter = new CustomListDoa(TitipanDoa.this, R.layout.list_doa,list);
//        HotelLinear.removeAllViews();
        adapter = new ListHotel(list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
//        HotelLinear.addView(listView);
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Hotel.this,"Mengambil Data","",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                Log.e("testError : ....", s);
                listView.setVisibility(View.VISIBLE);
                NotFoundLinear.setVisibility(View.GONE);
                showData();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(AppConfig.URL_LIST_HOTEL);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void logoutUser() {
        if (session.isLoggedIn()) {
            session.setLogin(false);
            db.deleteUsers();
        }

        // Launching the login activity
        Intent intent = new Intent(Hotel.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void cariNamaHotel(final String cariHotel){
        class CariNamaHotel extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Hotel.this,"Mengambil Data","",false,false);
            }

            @Override
            protected void onPostExecute(String m) {
                super.onPostExecute(m);
                loading.dismiss();
                Log.e("nilaiM : ", m);
                System.out.println("Variable var is: " + m);

                if(m.trim().equals("Gagal")){
                    Log.e("dataCari : ", m);
                    listView.setVisibility(View.GONE);
                    NotFoundLinear.setVisibility(View.VISIBLE);
                }else {
                    listView.setVisibility(View.VISIBLE);
                    NotFoundLinear.setVisibility(View.GONE);
                    JSON_STRING = m;
                    showData();
                }

//                Toast.makeText(Hotel.this, "Terimakasih telah mendoakan", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
//                HashMap<String,String> data = new HashMap<>();
//                data.put(AppConfig.KEY_NAME, cariHotel);
                Log.e("stingCari",cariHotel );
                RequestHandler rh = new RequestHandler();
                String res = rh.sendGetRequestParam(AppConfig.URL_FIND_HOTEL, cariHotel);
                return res;
            }
        }

        CariNamaHotel ae = new CariNamaHotel();
        ae.execute();
//        startActivity(new Intent(TitipanDoa.this, TitipanDoa.class));

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
//        final String empId = map.get(AppConfig.TAG_ID).toString();
//        final Button btndoakan = (Button)view.findViewById(R.id.btndoakan);
//        btndoakan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Toast.makeText(getApplicationContext(),"Download "+empId, Toast.LENGTH_LONG).show();
//                addDoakan(empId);
//            }
//        });
    }

    private class ListHotel extends ArrayAdapter<items_hotel> {
        public ListHotel(ArrayList<items_hotel> ItemList) {
            super(Hotel.this, R.layout.list_hotel, ItemList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.list_hotel, parent, false);
            }


            CircleImageView imgHotel = (CircleImageView) itemView.findViewById(R.id.imgprofile_Hotel);
            TextView no = (TextView)itemView.findViewById(R.id.txtNO);
            TextView alamat = (TextView)itemView.findViewById(R.id.txtalamatHotel);
            TextView nama = (TextView)itemView.findViewById(R.id.txtNamaHotel);
            RatingBar ratingHotel = (RatingBar) itemView.findViewById(R.id.hotel_ratingbar_list);

            Button btnSetHotel = (Button)itemView.findViewById(R.id.btnSetHotel);

            Typeface font = Typeface.createFromAsset(Hotel.this.getAssets(), "fonts/helvetica.ttf");
            alamat.setTypeface(font);
            nama.setTypeface(font,Typeface.BOLD);

            btnSetHotel.setTypeface(font);

            final String strID = list.get(position).getId();
            String strNama = list.get(position).getNama();
            String strAlamat = list.get(position).getAlamat();
            String strRating = list.get(position).getRating();
            final String strLat = list.get(position).getLat();
            final String strLng = list.get(position).getLng();
            no.setText(strID);
            nama.setText(strNama);
            alamat.setText(strAlamat);
            float rating = Float.parseFloat(strRating);
//            ratingHotel.setNumStars(Integer.parseInt(strRating));
            ratingHotel.setRating(rating);

            if(strRating.equals("0")){
                ratingHotel.setVisibility(View.GONE);
            }else{
                ratingHotel.setVisibility(View.VISIBLE);
            }

//            Picasso.with(getContext()).load(AppConfig.URL_HOME+"/uploads/profile/"+strIdUser+"/agent.jpg").memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).error(R.drawable.profile).into(imgDoa);

            btnSetHotel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                notifyDataSetChanged();
//                    TitipanDoa d= new TitipanDoa();
//                    d.addDoakan(strID);
//                    list.clear();
//                    addDoakan(strID);
                    Log.e("lat :", strLat);
                    if(!strLat.equals("null") && !strLng.equals("null")){
                        insertIntoDB("HOTEL",strLat,strLng);
                        Intent intent = new Intent(getContext(), navigasi.class);
                        getContext().startActivity(intent);
                    }else {
                        Intent intent = new Intent(getContext(), MapsActivity.class);
                        intent.putExtra(AppConfig.KEY_ID, strID);
                        intent.putExtra(AppConfig.KEY_LAT, strLat);
                        intent.putExtra(AppConfig.KEY_LNG, strLng);
                        getContext().startActivity(intent);
                        finish();
                    }
                }


            });

            return itemView;

        }
    }

    protected void createDatabase(){
        database=openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS locations(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR,lat VARCHAR,lng VARCHAR);");
    }

    protected void insertIntoDB(String name,String lat,String lng){

        Cursor mCount= database.rawQuery("select count(*) from locations where name='" + name + "'", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        if(count > 0) {
            String query = "UPDATE locations SET lat='" + lat + "',lng='" + lng + "' WHERE name='" + name + "';";
            database.execSQL(query);
        }else {
            String query = "INSERT INTO locations (name,lat,lng) VALUES('" + name + "', '" + lat + "', '" + lng + "');";
            database.execSQL(query);
        }
        Toast.makeText(getApplicationContext(),"Location "+name+ " Berhasil di simpan", Toast.LENGTH_LONG).show();
        Cursor c = database.rawQuery("SELECT * FROM locations WHERE name='" + name + "'", null);

        c.moveToFirst();
        String nama=c.getString(1);
        String lats=c.getString(2);
        String lngs=c.getString(3);
        Log.d("MyDataShow", "Name: " + nama+"Lat: " + lats+"Lng: " + lngs);
    }
}
