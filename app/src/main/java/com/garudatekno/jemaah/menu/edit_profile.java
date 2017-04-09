package com.garudatekno.jemaah.menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.activity.MainActivity;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.sql.Types.NULL;

public class edit_profile extends AppCompatActivity implements OnClickListener {
    private TextView formatTxt, contentTxt;
    private EditText txtName, txtPhone, txtPassport, editTextuser, txtEmail,txtAddress,txtTwon,txtProvince,txtfamily1,txtfamily2,txtfamily3;
    private Button buttonAdd, scanBtn;
    private Button buttonUpload;
    private CircleImageView imgProfile;
    private Bitmap bitmap;
    private RadioGroup rg;
    private static final int PICK_Camera_IMAGE = 2;
    Uri imageUri;
    String created,email,uid,nama;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    //user
    private SQLiteHandler db;
    private SessionManager session;
    //date time
    Calendar cdate = Calendar.getInstance();
    Calendar ctime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit);

        LinearLayout menu_panduan=(LinearLayout) findViewById(R.id.menu_panduan);
        TextView txt_panduan=(TextView) findViewById(R.id.txt_panduan);
        LinearLayout menu_doa=(LinearLayout) findViewById(R.id.menu_doa);
        TextView txt_doa=(TextView) findViewById(R.id.txt_doa);
        LinearLayout menu_emergency=(LinearLayout) findViewById(R.id.menu_emergency);
        TextView txt_emergency=(TextView) findViewById(R.id.txt_emergency);
        LinearLayout menu_profile=(LinearLayout) findViewById(R.id.menu_profile);
        TextView txt_profile=(TextView) findViewById(R.id.txt_profile);
        LinearLayout menu_inbox=(LinearLayout) findViewById(R.id.menu_inbox);
        TextView txt_inbox=(TextView) findViewById(R.id.txt_inbox);
        txt_profile.setTextColor(getResources().getColor(R.color.colorTextActive));
        menu_profile.setBackgroundResource(R.color.colorPrimary);
        ImageView img_doa=(ImageView) findViewById(R.id.img_profile);
        img_doa.setImageDrawable(getResources().getDrawable(R.drawable.profile_active));
        menu_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), profile.class);
                startActivity(i);
            }
        });

        menu_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), emergency.class);
                startActivity(i);
            }
        });

        menu_doa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Doa.class);
                startActivity(i);
            }
        });
        menu_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), inbox.class);
                startActivity(i);
            }
        });
        menu_panduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
            }
        });

        //FOOTER
        TextView txt_thowaf=(TextView) findViewById(R.id.txt_thowaf);
        TextView txt_sai=(TextView) findViewById(R.id.txt_sai);
        final TextView txt_go=(TextView) findViewById(R.id.txt_go);
        txt_thowaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
        txt_sai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), sai.class);
                startActivity(i);
            }
        });
        txt_go.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(edit_profile.this, txt_go);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == R.id.bus) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"BUS");
                            startActivity(i);
                        }
                        if(id == R.id.hotel) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"HOTEL");
                            startActivity(i);
                        }
                        if(id == R.id.pintu) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"NO PINTU MASJID");
                            startActivity(i);
                        }
                        if(id == R.id.meeting) {
                            Intent i = new Intent(getApplicationContext(), go.class);
                            i.putExtra(AppConfig.KEY_NAME,"MEETING POINT");
                            startActivity(i);
                        }
                        if(id == R.id.pin) {
                            Intent i = new Intent(getApplicationContext(), marker.class);
                            startActivity(i);
                        }

                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        txtName = (EditText) findViewById(R.id.name);
        txtAddress = (EditText) findViewById(R.id.address);
        txtPhone = (EditText) findViewById(R.id.phone);
        txtPassport = (EditText) findViewById(R.id.passport);
        txtProvince = (EditText) findViewById(R.id.province);
        txtTwon = (EditText) findViewById(R.id.town);
        txtfamily1 = (EditText) findViewById(R.id.family1);
        txtfamily2 = (EditText) findViewById(R.id.family2);
        txtfamily3 = (EditText) findViewById(R.id.family3);
        editTextuser = (EditText) findViewById(R.id.userid);
        editTextuser.setVisibility(View.GONE);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        nama = user.get("name");
        email = user.get("email");
        created = user.get("created_at");
        //user
        getData();
        editTextuser.setText(uid);

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        imgProfile = (CircleImageView) findViewById(R.id.imageProfile);

        //buttonChoose.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);

    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(edit_profile.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

        public void onClick(View v){
        if(v == buttonAdd){
            if(imgProfile.getDrawable() == null)
            {
                Toast.makeText(this, "Image cannot null", Toast.LENGTH_SHORT).show();
            }else if(txtfamily1.getText().toString().equals(""))
            {
                Toast.makeText(this, "Family Contact cannot null", Toast.LENGTH_SHORT).show();
            }else if(txtName.getText().toString().equals(""))
            {
                Toast.makeText(this, "Name cannot null", Toast.LENGTH_SHORT).show();
            }else if(txtName.getText().toString().equals(""))
            {
                Toast.makeText(this, "Address cannot null", Toast.LENGTH_SHORT).show();
            } else {
                addBarcode();
            }
        }if(v == imgProfile){
            Intent inten = new Intent();
            inten.setType("image/*");
            inten.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(inten, "Select Picture"), PICK_IMAGE_REQUEST);
        }
    }

    private void getData(){
        class GetData extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(edit_profile.this,"Fetching...","Wait...",false,false);
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
            String name = c.getString(AppConfig.KEY_NAME);
            String address = c.getString(AppConfig.KEY_ADDRESS);
            String passport = c.getString(AppConfig.KEY_PASSPORT);
            String phone = c.getString(AppConfig.KEY_PHONE);
            String province = c.getString(AppConfig.KEY_PROVINCE);
            String town = c.getString(AppConfig.KEY_TOWN);
            String tfamily1 = c.getString(AppConfig.KEY_PHONE_FAMILY1);
            String tfamily2 = c.getString(AppConfig.KEY_PHONE_FAMILY2);
            String tfamily3 = c.getString(AppConfig.KEY_PHONE_FAMILY3);

            if(name.equals(NULL) || name.equals("")) {
                imgProfile.setImageResource(R.drawable.profile);
            }else{
                Picasso.with(this)
                        .load(AppConfig.URL_HOME + "/uploads/profile/" + uid + "/images.jpg")
                        .into(imgProfile);
            }


            txtName.setText(name);
            txtAddress.setText(address);
            txtPassport.setText(passport);
            txtPhone.setText(phone);
            txtProvince.setText(province);
            txtTwon.setText(town);
            txtfamily1.setText(tfamily1);
            txtfamily2.setText(tfamily2);
            txtfamily3.setText(tfamily3);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    //Adding an addBarcode
    private void addBarcode(){
//        final String date = txtDate.getText().toString().trim();
//        final String time = txtTime.getText().toString().trim();
        final String idUser = editTextuser.getText().toString().trim();
        final String name = txtName.getText().toString().trim();
        final String address = txtAddress.getText().toString().trim();
        final String passport = txtPassport.getText().toString().trim();
        final String phone = txtPhone.getText().toString().trim();
        final String province = txtProvince.getText().toString().trim();
        final String town = txtTwon.getText().toString().trim();
        final String family1 = txtfamily1.getText().toString().trim();
        final String family2 = txtfamily2.getText().toString().trim();
        final String family3 = txtfamily3.getText().toString().trim();
//        final String radiovalue = ((RadioButton)findViewById(rg.getCheckedRadioButtonId())).getText().toString();
//        final Spinner spinner_house = (Spinner) findViewById(R.id.status);
//        final String spinner_status = spinner_house.getSelectedItem().toString();

        class AddBarcode extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(edit_profile.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(edit_profile.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];

                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_USERID, idUser);
                try{
                    String uploadImage = getStringImage(bitmap);
                    data.put(AppConfig.UPLOAD_KEY, uploadImage);
                } catch (Exception ex) {
                    data.put(AppConfig.UPLOAD_KEY, "");
                    Log.i("Bitmap Error", "Tidak ada image");
                }
                db.deleteUsers();
                db.addUser(uid,nama, email, created,family1+","+family2+","+family3);

                data.put(AppConfig.KEY_NAME,name);
                data.put(AppConfig.KEY_ADDRESS,address);
                data.put(AppConfig.KEY_PASSPORT,passport);
                data.put(AppConfig.KEY_PHONE,phone);
                data.put(AppConfig.KEY_PROVINCE,province);
                data.put(AppConfig.KEY_TOWN,town);
                data.put(AppConfig.KEY_PHONE_FAMILY1,family1);
                data.put(AppConfig.KEY_PHONE_FAMILY2,family2);
                data.put(AppConfig.KEY_PHONE_FAMILY3,family3);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(AppConfig.URL_PROFILE, data);
                return res;
            }
        }

        AddBarcode ae = new AddBarcode();
        ae.execute(bitmap);

        startActivity(new Intent(edit_profile.this, edit_profile.class));
    }

}
