package salam.gohajj.custom.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import salam.gohajj.custom.R;
import salam.gohajj.custom.app.AppConfig;
import salam.gohajj.custom.helper.SQLiteHandler;
import salam.gohajj.custom.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import me.anwarshahriar.calligrapher.Calligrapher;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    regUser(name, email, password);
                }else if(name.isEmpty()) {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.nama_tidak_boleh_kosong), Toast.LENGTH_SHORT)
                            .show();
                }else if(email.isEmpty()) {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.email_tidak_boleh_kosong), Toast.LENGTH_SHORT)
                            .show();
                }else if(password.isEmpty()) {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.password_tidak_boleh_kosong), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void regUser(final String name, final String email,
                         final String password){


        class RegUser extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterActivity.this,"Register...",getResources().getString(R.string.mohon_tunggu)+"...",false,false);
            }

            @Override
            protected void onPostExecute(String res) {
                super.onPostExecute(res);
                loading.dismiss();
                try {
                    JSONObject jObj = new JSONObject(res);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        String errorMsg = jObj.getString("error_msg");

                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent j = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(j);
                        finish();

                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Bitmap... params) {

                HashMap<String,String> data = new HashMap<>();
                data.put("name", name);
                data.put("email", email);
                data.put("password", password);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(AppConfig.URL_REGISTER, data);
                return res;
            }
        }

        RegUser ae = new RegUser();
        ae.execute();

//        startActivity(new Intent(DataReefer.this, ViewAll.class));
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
