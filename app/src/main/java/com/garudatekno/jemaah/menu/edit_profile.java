package com.garudatekno.jemaah.menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.LoginActivity;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;

public class edit_profile extends AppCompatActivity implements OnClickListener {
    private TextView formatTxt, contentTxt;
    private EditText txtName, txtPhone,
            txtPassport, editTextuser, txtEmail1,txtEmail2,txtEmail3,
            txtAddress,txtTwon,txtProvince,txtfamily1,txtfamily2,txtfamily3,EditAgentTravel,EditTravelPhone,
            EditPembimbing,EditPembimbingPhone,EditPemimpin,EditPemimpinPhone;
    private Button buttonAdd, scanBtn;
    private Button buttonUpload;
    private CircleImageView imgProfile,imgAgent,imgPemimpin,imgPembimbing;
    private Bitmap bitmapProfile,bitmapAgent,bitmapPemimpin,bitmapPembimbing;
    private RadioGroup rg;
    private static final int PICK_Camera_IMAGE = 2;
    Uri imageUri;
    String created,email,uid,nama;
    private int PICK_IMAGE_REQUEST = 3;
    private Uri filePathProfile,filePathAgent,filePathPemimpin,filePathPembimbing;
    private String selectedProfile,selectedAgent,selectedPemimpin,selectedPembimbing;
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
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

//        session = new SessionManager(getApplicationContext());

        //HEADER
        TextView txt_emergency=(TextView) findViewById(R.id.txt_emergency);
        TextView txt_thowaf=(TextView) findViewById(R.id.txt_thowaf);
        TextView txt_sai=(TextView) findViewById(R.id.txt_sai);
        txt_thowaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), thawaf.class);
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
        txt_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), emergency.class);
                startActivity(i);
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

        menu_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), profile.class);
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
        menu_doa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TitipanDoa.class);
                startActivity(i);
            }
        });
        menu_navigasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), navigasi.class);
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

        final ImageView img_home=(ImageView) findViewById(R.id.img_home);
        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), panduan.class);
                startActivity(i);
            }
        });
        final  ImageView img_setting=(ImageView) findViewById(R.id.img_setting);
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), setting.class);
                startActivity(i);
            }
        });

        txtName = (EditText) findViewById(R.id.name);
        txtAddress = (EditText) findViewById(R.id.address);
        txtPhone = (EditText) findViewById(R.id.phone);
        txtPassport = (EditText) findViewById(R.id.passport);
        txtProvince = (EditText) findViewById(R.id.province);
        txtTwon = (EditText) findViewById(R.id.town);
        EditAgentTravel = (EditText) findViewById(R.id.edit_travel_agent);
        EditTravelPhone = (EditText) findViewById(R.id.edit_travel_phone);
        EditPembimbing = (EditText) findViewById(R.id.edit_pembimbing);
        EditPembimbingPhone = (EditText) findViewById(R.id.edit_pembimbing_phone);
        EditPemimpin = (EditText) findViewById(R.id.edit_pemimpin);
        EditPemimpinPhone = (EditText) findViewById(R.id.edit_pemimpin_phone);
        txtfamily1 = (EditText) findViewById(R.id.family1);
        txtfamily2 = (EditText) findViewById(R.id.family2);
        txtfamily3 = (EditText) findViewById(R.id.family3);
        txtEmail1 = (EditText) findViewById(R.id.efamily1);
        txtEmail2 = (EditText) findViewById(R.id.efamily2);
        txtEmail3 = (EditText) findViewById(R.id.efamily3);
        editTextuser = (EditText) findViewById(R.id.userid);
        editTextuser.setVisibility(View.GONE);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        nama = user.get("name");
        email = user.get("email");
        created = user.get("created_at");
        //user
        getData();
        editTextuser.setText(uid);

        //useri mage
        CircleImageView imgp = (CircleImageView) findViewById(R.id.img_profile);
        File file = new File("/sdcard/android/data/com.garudatekno.jemaah/images/profile.png");
        if (!file.exists()) {
            imgp.setImageResource(R.drawable.profile);
        }else{
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgp.setImageBitmap(bmp);
        }

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        imgProfile = (CircleImageView) findViewById(R.id.imageProfile);
        imgAgent = (CircleImageView) findViewById(R.id.imageAgent);
        imgPembimbing = (CircleImageView) findViewById(R.id.imagePembimbing);
        imgPemimpin = (CircleImageView) findViewById(R.id.imagePemimpin);

        //buttonChoose.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        imgAgent.setOnClickListener(this);
        imgPembimbing.setOnClickListener(this);
        imgPemimpin.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void logoutUser() {
        if (session.isLoggedIn()) {
            session.setLogin(false);

            db.deleteUsers();
        }

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
                selectedProfile = "selected";
                selectedAgent = null;
                selectedPembimbing = null;
                selectedPemimpin = null;
            Intent inten = new Intent();
            inten.setType("image/*");
            inten.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(inten, "Select Picture"), PICK_IMAGE_REQUEST);
        }
            if(v == imgAgent){
                selectedProfile = null;
                selectedAgent = "selected";
                selectedPembimbing = null;
                selectedPemimpin = null;
                Intent inten = new Intent();
                inten.setType("image/*");
                inten.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(inten, "Select Picture"), PICK_IMAGE_REQUEST);
            }
            if(v == imgPembimbing){
                selectedProfile = null;
                selectedAgent = null;
                selectedPembimbing = "selected";
                selectedPemimpin = null;
                Intent inten = new Intent();
                inten.setType("image/*");
                inten.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(inten, "Select Picture"), PICK_IMAGE_REQUEST);
            }
            if(v == imgPemimpin){
                selectedProfile = null;
                selectedAgent = null;
                selectedPembimbing = null;
                selectedPemimpin = "selected";
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
            String travel = c.getString(AppConfig.KEY_TRAVEL_AGENT);
            String travel_phone = c.getString(AppConfig.KEY_TRAVEL_PHONE);
            String pemimpin = c.getString(AppConfig.KEY_PEMIMPIN);
            String pemimpin_phone = c.getString(AppConfig.KEY_PEMIMPIN_PHONE);
            String pembimbing = c.getString(AppConfig.KEY_PEMBIMBING);
            String pembimbing_phone = c.getString(AppConfig.KEY_PEMIMPIN_PHONE);
            String tfamily1 = c.getString(AppConfig.KEY_PHONE_FAMILY1);
            String tfamily2 = c.getString(AppConfig.KEY_PHONE_FAMILY2);
            String tfamily3 = c.getString(AppConfig.KEY_PHONE_FAMILY3);
            String efamily1 = c.getString(AppConfig.KEY_EMAIL_FAMILY1);
            String efamily2 = c.getString(AppConfig.KEY_EMAIL_FAMILY2);
            String efamily3 = c.getString(AppConfig.KEY_EMAIL_FAMILY3);

//            if(name.equals(NULL) || name.equals("")) {
//                imgProfile.setImageResource(R.drawable.profile);
//            }else{
                File file = new File("/sdcard/android/data/com.garudatekno.jemaah/images/profile.png");
                if (!file.exists()) {
                    imgProfile.setImageResource(R.drawable.profile);
                }else{
                    Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imgProfile.setImageBitmap(bmp);
                }
//            }
//            Picasso.with(this).load(AppConfig.URL_HOME+"/uploads/profile/"+uid+"/images.jpg").error(R.drawable.profile).into(imgProfile);
            Picasso.with(this).load(AppConfig.URL_HOME+"/uploads/profile/"+uid+"/agent.jpg").memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).error(R.drawable.profile).into(imgAgent);
            Picasso.with(this).load(AppConfig.URL_HOME+"/uploads/profile/"+uid+"/pembimbing.jpg").memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).error(R.drawable.profile).into(imgPembimbing);
            Picasso.with(this).load(AppConfig.URL_HOME+"/uploads/profile/"+uid+"/pemimpin.jpg").memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).error(R.drawable.profile).into(imgPemimpin);


            txtName.setText(name);
            txtAddress.setText(address);
            txtPassport.setText(passport);
            txtPhone.setText(phone);
            txtProvince.setText(province);
            txtTwon.setText(town);
            txtfamily1.setText(tfamily1);
            txtfamily2.setText(tfamily2);
            txtfamily3.setText(tfamily3);
            txtEmail1.setText(efamily1);
            txtEmail2.setText(efamily2);
            txtEmail3.setText(efamily3);
            EditAgentTravel.setText(travel);
            EditTravelPhone.setText(travel_phone);
            EditPemimpin.setText(pemimpin);
            EditPemimpinPhone.setText(pemimpin_phone);
            EditPembimbing.setText(pembimbing);
            EditPembimbingPhone.setText(pembimbing_phone);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if(selectedProfile != null) {
                filePathProfile = data.getData();
                try {
                    Bitmap bitmapProfileOri = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathProfile);
                    bitmapProfile = getResizedBitmap(bitmapProfileOri, 500);
                    imgProfile.setImageBitmap(bitmapProfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(selectedAgent != null) {
                filePathAgent = data.getData();
                try {
                    Bitmap bitmapAgentOri = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathAgent);
                    bitmapAgent = getResizedBitmap(bitmapAgentOri, 500);
                    imgAgent.setImageBitmap(bitmapAgent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(selectedPemimpin != null) {
                filePathPemimpin = data.getData();
                try {
                    Bitmap bitmapPemimpinOri = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathPemimpin);
                    bitmapPemimpin = getResizedBitmap(bitmapPemimpinOri, 500);
                    imgPemimpin.setImageBitmap(bitmapPemimpin);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(selectedPembimbing != null) {
                filePathPembimbing = data.getData();
                try {
                    Bitmap bitmapPembimbingOri = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathPembimbing);
                    bitmapPembimbing = getResizedBitmap(bitmapPembimbingOri, 500);
                    imgPembimbing.setImageBitmap(bitmapPembimbing);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
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
        final String efamily1 = txtEmail1.getText().toString().trim();
        final String efamily2 = txtEmail2.getText().toString().trim();
        final String efamily3 = txtEmail3.getText().toString().trim();
        final String travel = EditAgentTravel.getText().toString().trim();
        final String travel_phone = EditTravelPhone.getText().toString().trim();
        final String pemimpin = EditPemimpin.getText().toString().trim();
        final String pemimpin_phone = EditPemimpinPhone.getText().toString().trim();
        final String pembimbing = EditPembimbing.getText().toString().trim();
        final String pembimbing_phone = EditPembimbingPhone.getText().toString().trim();
//        final String radiovalue = ((RadioButton)findViewById(rg.getCheckedRadioButtonId())).getText().toString();
//        final Spinner spinner_house = (Spinner) findViewById(R.id.status);
//        final String spinner_status = spinner_house.getSelectedItem().toString();

        class AddBarcode extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(edit_profile.this,"Menyimpan...","",false,false);
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
                if(bitmapProfile != null) {
                    String uploadImageProfile = getStringImage(bitmapProfile);
                    Log.e("profile", uploadImageProfile);
                    data.put(AppConfig.UPLOAD_KEY, uploadImageProfile);

                    try{
//                    String uploadImageProfile = getStringImage(bitmapProfile);
//                    Log.e("profile",uploadImageProfile);
//                    data.put(AppConfig.UPLOAD_KEY, uploadImageProfile);

                        //save image to sdcard
                        File sdCardDirectory = Environment.getExternalStorageDirectory();
                        File image = new File(sdCardDirectory+"/android/data/com.garudatekno.jemaah/images/", "profile.png");
                        // Encode the file as a PNG image.
                        FileOutputStream outStream;

                        outStream = new FileOutputStream(image);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    /* 100 to keep full quality of the image */
                        outStream.flush();
                        outStream.close();
                    } catch (Exception ex) {
//                    data.put(AppConfig.UPLOAD_KEY, "");
                        Log.i("Bitmap Error", "Tidak ada image");
                    }
                }else{
                    data.put(AppConfig.UPLOAD_KEY, "");
                }
                if(bitmapAgent != null) {
                    String uploadImageAgent = getStringImage(bitmapAgent);
                    Log.e("agent", uploadImageAgent);
                    data.put(AppConfig.UPLOAD_AGENT, uploadImageAgent);
                }else{
                    data.put(AppConfig.UPLOAD_AGENT, "");
                }
                if(bitmapPemimpin != null) {
                    String uploadImagePemimpin = getStringImage(bitmapPemimpin);
                    Log.e("pemimpin", uploadImagePemimpin);
                    data.put(AppConfig.UPLOAD_PEMIMPIN, uploadImagePemimpin);
                }else{
                    data.put(AppConfig.UPLOAD_PEMIMPIN, "");
                }
                if(bitmapPembimbing != null) {
                    String uploadImagePembimbing = getStringImage(bitmapPembimbing);
                    Log.e("pemimpin", uploadImagePembimbing);
                    data.put(AppConfig.UPLOAD_PEMBIMBING, uploadImagePembimbing);
                }else{
                    data.put(AppConfig.UPLOAD_PEMBIMBING, "");
                }

//                try{
////                    String uploadImageProfile = getStringImage(bitmapProfile);
////                    Log.e("profile",uploadImageProfile);
////                    data.put(AppConfig.UPLOAD_KEY, uploadImageProfile);
//
//                    //save image to sdcard
//                    File sdCardDirectory = Environment.getExternalStorageDirectory();
//                    File image = new File(sdCardDirectory+"/android/data/com.garudatekno.jemaah/images/", "profile.png");
//                    // Encode the file as a PNG image.
//                    FileOutputStream outStream;
//
//                    outStream = new FileOutputStream(image);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//                    /* 100 to keep full quality of the image */
//                    outStream.flush();
//                    outStream.close();
//                } catch (Exception ex) {
////                    data.put(AppConfig.UPLOAD_KEY, "");
//                    Log.i("Bitmap Error", "Tidak ada image");
//                }
                //save image to sdcard
//                File sdCardDirectory = Environment.getExternalStorageDirectory();
//                File image = new File(sdCardDirectory+"/android/data/com.garudatekno.jemaah/images/", "profile.png");
//                // Encode the file as a PNG image.
//                FileOutputStream outStream;
//                try {
//                    outStream = new FileOutputStream(image);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//                    /* 100 to keep full quality of the image */
//                    outStream.flush();
//                    outStream.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

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
                data.put(AppConfig.KEY_EMAIL_FAMILY1,efamily1);
                data.put(AppConfig.KEY_EMAIL_FAMILY2,efamily2);
                data.put(AppConfig.KEY_EMAIL_FAMILY3,efamily3);
                data.put(AppConfig.KEY_TRAVEL_AGENT,travel);
                data.put(AppConfig.KEY_TRAVEL_PHONE,travel_phone);
                data.put(AppConfig.KEY_PEMBIMBING,pembimbing);
                data.put(AppConfig.KEY_PEMBIMBING_PHONE,pembimbing_phone);
                data.put(AppConfig.KEY_PEMIMPIN,pemimpin);
                data.put(AppConfig.KEY_PEMIMPIN_PHONE,pemimpin_phone);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(AppConfig.URL_PROFILE, data);
                return res;
            }
        }

        AddBarcode ae = new AddBarcode();
        ae.execute(bitmapProfile,bitmapAgent,bitmapPembimbing,bitmapPemimpin);

        startActivity(new Intent(edit_profile.this, profile.class));
    }

}
