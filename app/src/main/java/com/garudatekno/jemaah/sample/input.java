package com.garudatekno.jemaah.sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
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

public class input extends AppCompatActivity implements OnClickListener {
    private TextView formatTxt, contentTxt;
    private EditText txtName, txtPhone, txtPassport, editTextuser, txtEmail,txtAddress,txtTwon,txtProvince;
    private Button buttonAdd, scanBtn;
    private Button buttonUpload;
    private ImageView imageView;
    private Bitmap bitmap;
    private RadioGroup rg;
    private static final int PICK_Camera_IMAGE = 2;
    Uri imageUri;
    String lat,lng,uid;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
     // UI Widgets.
    protected EditText mLatitudeTextView;
    protected EditText mLongitudeTextView;
    //user
    private SQLiteHandler db;
    private SessionManager session;
    //date time
    Calendar cdate = Calendar.getInstance();
    Calendar ctime = Calendar.getInstance();
//    DatePickerDialog.OnDateSetListener d =
//            new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker view, int year, int month,
//                                      int day) {
//                    // TODO Auto-generated method stub
//                    cdate.set(Calendar.YEAR, year);
//                    cdate.set(Calendar.MONTH, month);
//                    cdate.set(Calendar.DAY_OF_MONTH, day);
//                    updateLabeldate();
//                }
//            };
//    TimePickerDialog.OnTimeSetListener t =
//            new TimePickerDialog.OnTimeSetListener() {
//                @Override
//                public void onTimeSet(TimePicker view, int jam, int menit) {
//                    // TODO Auto-generated method stub
//                    ctime.set(Calendar.HOUR_OF_DAY, jam);
//                    ctime.set(Calendar.MINUTE, menit);
//                    updateLabeltime();
//                }
//            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_input);

//        scanBtn = (Button) findViewById(R.id.scan_button);
//        formatTxt = (TextView)findViewById(R.id.scan_format);
//        contentTxt = (TextView) findViewById(R.id.scan_content);

//        rg = (RadioGroup) findViewById(R.id.radioStatus);
        //Initializing views

        txtName = (EditText) findViewById(R.id.name);
        txtAddress = (EditText) findViewById(R.id.address);
        txtPhone = (EditText) findViewById(R.id.phone);
        txtPassport = (EditText) findViewById(R.id.passport);
        txtProvince = (EditText) findViewById(R.id.province);
        txtTwon = (EditText) findViewById(R.id.town);
        editTextuser = (EditText) findViewById(R.id.userid);
        editTextuser.setVisibility(View.GONE);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
//        Spinner dropdown = (Spinner)findViewById(R.id.status);
//        String[] items = new String[]{"OK", "RUSAK"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        dropdown.setAdapter(adapter);
        //user
        getData();
        editTextuser.setText(uid);

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        imageView = (ImageView) findViewById(R.id.imageView);

//        txtDate = (EditText) findViewById(R.id.in_date);
//        txtTime = (EditText) findViewById(R.id.in_time);
        //buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
//        txtDate.setOnClickListener(this);
//        txtTime.setOnClickListener(this);
//        scanBtn.setOnClickListener(this);
//        updateLabeltime();
//        updateLabeldate();

//        mLatitudeTextView = (EditText) findViewById(R.id.latitude_text);
//        mLongitudeTextView = (EditText) findViewById(R.id.longitude_text);
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            lat = extras.getString("lat");
//            lng = extras.getString("lng");
//            mLatitudeTextView.setText(lat);
//            mLongitudeTextView.setText(lng);
//        }

    }

    public void onClick(View v){
//        if(v.getId()==R.id.scan_button){
//            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
//            scanIntegrator.initiateScan();
//        }
        if(v == buttonAdd){
            if(imageView.getDrawable() == null)
            {
                Toast.makeText(this, "Image cannot null", Toast.LENGTH_SHORT).show();
            }else if(txtName.getText().toString().equals(""))
            {
                Toast.makeText(this, "Name cannot null", Toast.LENGTH_SHORT).show();
            }else if(txtName.getText().toString().equals(""))
            {
                Toast.makeText(this, "Address cannot null", Toast.LENGTH_SHORT).show();
            } else {
                addBarcode();
            }
        }if(v == buttonUpload){
            Intent inten = new Intent();
            inten.setType("image/*");
            inten.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(inten, "Select Picture"), PICK_IMAGE_REQUEST);
        }
//        if (v == txtDate) {
//            new DatePickerDialog(input.this, d,
//                    cdate.get(Calendar.YEAR),
//                    cdate.get(Calendar.MONTH),
//                    cdate.get(Calendar.DAY_OF_MONTH)).show();
//        }
//        if (v == txtTime) {
//            new TimePickerDialog(input.this, t,
//                    ctime.get(Calendar.HOUR_OF_DAY),
//                    ctime.get(Calendar.MINUTE), true).show();
//        }
    }

//    private void updateLabeldate() {
//        SimpleDateFormat curFormater = new SimpleDateFormat("dd-MM-yyyy");
//
//        txtDate.setText(
//                curFormater.format(cdate.getTime()));
//    }
//
//    private void updateLabeltime() {
//        SimpleDateFormat timFormater = new SimpleDateFormat("HH:mm");
//        txtTime.setText(
//                timFormater.format(ctime.getTime()));
//    }

    private void getData(){
        class GetData extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(input.this,"Fetching...","Wait...",false,false);
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

            txtName.setText(name);
            txtAddress.setText(address);
            txtPassport.setText(passport);
            txtPhone.setText(phone);
            txtProvince.setText(province);
            txtTwon.setText(town);

          Picasso.with(this)
            .load("http://192.168.43.31/uploads/profile/"+uid+"/images.jpg")
                    .into(imageView);

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
                imageView.setImageBitmap(bitmap);
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

    public boolean harusDiisi(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(Html
                    .fromHtml("<font color='red'>Input tidak boleh kosong</font>"));
            return false;
        }

        return true;
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
//        final String radiovalue = ((RadioButton)findViewById(rg.getCheckedRadioButtonId())).getText().toString();
//        final Spinner spinner_house = (Spinner) findViewById(R.id.status);
//        final String spinner_status = spinner_house.getSelectedItem().toString();

        class AddBarcode extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(input.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(input.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_USERID, idUser);
                data.put(AppConfig.UPLOAD_KEY, uploadImage);
                data.put(AppConfig.KEY_NAME,name);
                data.put(AppConfig.KEY_ADDRESS,address);
                data.put(AppConfig.KEY_PASSPORT,passport);
                data.put(AppConfig.KEY_PHONE,phone);
                data.put(AppConfig.KEY_PROVINCE,province);
                data.put(AppConfig.KEY_TOWN,town);
//                data.put(AppConfig.KEY_DATE,date);
//                data.put(AppConfig.KEY_TIME,time);
//                data.put(AppConfig.KEY_OPTION,radiovalue);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(AppConfig.URL_PROFILE, data);
                return res;
            }
        }

        AddBarcode ae = new AddBarcode();
        ae.execute(bitmap);

        startActivity(new Intent(input.this, input.class));
    }

}
