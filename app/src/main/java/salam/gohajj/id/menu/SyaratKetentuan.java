package salam.gohajj.id.menu;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import salam.gohajj.id.R;

import salam.gohajj.id.activity.RequestHandler;
import salam.gohajj.id.app.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import me.anwarshahriar.calligrapher.Calligrapher;

public class SyaratKetentuan extends AppCompatActivity {

    private static final String TAG = "MyUser";
    WebView txtValue;
    TextView txtName;


    private String JSON_STRING,uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syarat);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);
        txtValue=(WebView) findViewById(R.id.txtValue);
        txtName=(TextView) findViewById(R.id.txtName);
        getData();

    }

    private void getData(){
        class GetData extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SyaratKetentuan.this,"","Mohon Tunggu...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showData(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_NAME, "Syarat dan Ketentuan");
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(AppConfig.URL_PARAMS, data);
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
            String id = c.getString(AppConfig.KEY_ID);
            String name = c.getString(AppConfig.KEY_NAME);
            String value = c.getString(AppConfig.KEY_VALUE);
//            txtName.setText(name);
            txtValue.loadData(value, "text/html; charset=utf-8", "utf-8");

        } catch (JSONException e) {
//            e.printStackTrace();
        }
    }

}
