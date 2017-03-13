package com.garudatekno.jemaah.sample;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.activity.RequestHandler;
import com.garudatekno.jemaah.app.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewData extends AppCompatActivity implements View.OnClickListener {


    private TextView txtFrom,txtTo,txtDate;
    private Button buttonDone;
    private Button buttonOK;
    private Button buttonStop;

    private String id;

    int numMessages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sample);

        Intent intent = getIntent();

        id = intent.getStringExtra(AppConfig.EMP_ID);

        txtFrom = (TextView) findViewById(R.id.from);
        txtTo = (TextView) findViewById(R.id.to);
        txtDate = (TextView) findViewById(R.id.date);
        buttonOK = (Button) findViewById(R.id.buttonOK);
        buttonDone = (Button) findViewById(R.id.buttonDone);
        buttonOK.setVisibility(View.GONE);
        buttonDone.setVisibility(View.GONE);

        buttonOK.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                RequestHandler rh = new RequestHandler();
                rh.sendGetRequestParam(AppConfig.URL_STATUS_START,id);
                Intent i = new Intent(getApplicationContext(), ViewData.class);
                i.putExtra(AppConfig.EMP_ID,id);
                startActivity(i);
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
//                RequestHandler rh = new RequestHandler();
//                rh.sendGetRequestParam(AppConfig.URL_STATUS_DONE,id);
//                Intent i = new Intent(getApplicationContext(), OrderHistory.class);
//                startActivity(i);
            }
        });

        getData();
    }

//    private void Notify(String notificationTitle, String notificationMessage){
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setSmallIcon(R.drawable.logo);
//        builder.setContentTitle(notificationTitle);//"Notifications Example")
//        builder.setContentText(notificationMessage);//"This is a test notification");
//        builder.setTicker("New Message Alert!");
//        builder.setSmallIcon(R.drawable.logo);
//
//        builder.setNumber(++numMessages);
//
//   /* Add Big View Specific Configuration */
//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//
//        String[] events = new String[6];
//        events[0] = new String("This is first line....");
//        events[1] = new String("This is second line...");
//        events[2] = new String("This is third line...");
//        events[3] = new String("This is 4th line...");
//        events[4] = new String("This is 5th line...");
//        events[5] = new String("This is 6th line...");
//
//        // Sets a title for the Inbox style big view
//        inboxStyle.setBigContentTitle("BCP Notification :");
//
//        // Moves events into the big view
//        for (int i=0; i < events.length; i++) {
//            inboxStyle.addLine(events[i]);
//        }
//        builder.setStyle(inboxStyle);
//
//        Intent notificationIntent = new Intent(this, NotificationView.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(contentIntent);
//        // Add as notification
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(1, builder.build());
//    }

    // Remove notification


    private void getData(){
        class GetData extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewData.this,"Fetching...","Wait...",false,false);
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
                String s = rh.sendGetRequestParam(AppConfig.URL_GET_SAMPLE,id);
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
            String from = c.getString(AppConfig.KEY_FROM);
            String to = c.getString(AppConfig.KEY_TO);
            String date = c.getString(AppConfig.KEY_DATE);
            String status = c.getString(AppConfig.KEY_STATUS);
//            String barcode = c.getString(AppConfig.KEY_BARCODE);
//            String option = c.getString(AppConfig.KEY_OPTION);
//            String datetime = c.getString(AppConfig.KEY_DATETIME);
//            String image = c.getString(AppConfig.UPLOAD_KEY);
//            String lat = c.getString(AppConfig.TAG_LAT);
//            String lng = c.getString(AppConfig.TAG_LNG);

            if(status.equals("ORDER")){
                buttonOK.setVisibility(View.VISIBLE);
            }
            if(status.equals("PROCESS")){
                buttonDone.setVisibility(View.VISIBLE);
            }
            txtFrom.setText(from);
            txtTo.setText(to);
            txtDate.setText(date);

//            textBarcode.setText(barcode);
//            textOption.setText(option);
//            textDatetime.setText(datetime);
//            textlat.setText(lat);
//            textlng.setText(lng);
//            Picasso.with(this)
//                    .load(image)
////                .placeholder(R.drawable.placeholder)   // optional
////                .error(R.drawable.error)      // optional
//                    .resize(200,200)                        // optional
//                    .into(imageView);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void actionUpdate(){
//        final String barcode = textBarcode.getText().toString().trim();
//        final String status = textOption.getText().toString().trim();
//
//        class UpdateData extends AsyncTask<Void,Void,String>{
//            ProgressDialog loading;
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                loading = ProgressDialog.show(ViewData.this,"Updating...","Wait...",false,false);
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                loading.dismiss();
//                Toast.makeText(ViewData.this,s,Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            protected String doInBackground(Void... params) {
//                HashMap<String,String> hashMap = new HashMap<>();
//                hashMap.put(AppConfig.KEY_ID,id);
//                hashMap.put(AppConfig.KEY_BARCODE,barcode);
//                hashMap.put(AppConfig.KEY_OPTION,status);
//
//                RequestHandler rh = new RequestHandler();
//
//                String s = rh.sendPostRequest(AppConfig.URL_UPDATE_SAMPLE,hashMap);
//
//                return s;
//            }
//        }
//
//        UpdateData ue = new UpdateData();
//        ue.execute();
    }

    private void deleteData(){
        class DeleteData extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewData.this, "Deleting...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ViewData.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(AppConfig.URL_DELETE_SAMPLE, id);
                return s;
            }
        }

        DeleteData de = new DeleteData();
        de.execute();
    }

    private void confirmDeleteEmployee(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Delete Data ?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteData();
                        startActivity(new Intent(ViewData.this, ViewAll.class));
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
//        if(v == buttonDone){
//            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//            // Creating a criteria object to retrieve provider
//            Criteria criteria = new Criteria();
//
//            // Getting the name of the best provider
//            String provider = locationManager.getBestProvider(criteria, true);
//
//            // Getting Current Location
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            actionUpdate();
//        }

//        if(v == buttonDone){
//            confirmDeleteEmployee();
//        }
    }
}
