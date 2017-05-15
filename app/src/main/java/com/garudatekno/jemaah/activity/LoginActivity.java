package com.garudatekno.jemaah.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.auth0.android.jwt.JWT;
import com.garudatekno.jemaah.R;
import com.garudatekno.jemaah.app.AppConfig;
import com.garudatekno.jemaah.app.AppController;
import com.garudatekno.jemaah.helper.SQLiteHandler;
import com.garudatekno.jemaah.helper.SessionManager;
import com.garudatekno.jemaah.menu.Bantuan;
import com.garudatekno.jemaah.menu.SyaratKetentuan;
import com.garudatekno.jemaah.menu.panduan;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.anwarshahriar.calligrapher.Calligrapher;

//import com.auth0.jwt.JWT;
//import com.auth0.jwt.exceptions.JWTDecodeException;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 100;
    private EditText inputEmail;
    private TextView txtBantuan,txtSyarat,txtNodaftar;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private String idToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"fonts/helvetica.ttf",true);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        txtBantuan = (TextView) findViewById(R.id.txtBantuan);
//        txtSyarat = (TextView) findViewById(R.id.txtSyarat);
        txtNodaftar = (TextView) findViewById(R.id.txtNodaftar);
        txtNodaftar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, panduan.class);
                startActivity(intent);
            }
        });
        txtBantuan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, Bantuan.class);
                startActivity(intent);
            }
        });
//        txtSyarat.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, SyaratKetentuan.class);
//                startActivity(intent);
//            }
//        });

//        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);

//        btnLoginGoogle = (Button) findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                signIn();
            }

        });

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, panduan.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password,refreshedToken);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
            }
        });

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.e(TAG, "refreshtokengmc:" + refreshedToken);
            idToken = acct.getIdToken();
//            Toast.makeText(this, acct.getDisplayName()+","+acct.getEmail()+","+idToken, Toast.LENGTH_LONG).show();
            loginSocial(idToken,refreshedToken);
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
//            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
//            updateUI(false);
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }

    private void loginSocial(final String token, final String refreshedToken){
        class LoginSocial extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this,"Mohon tunggu..."," ",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                loading.dismiss();
                Log.e("response : ",s);
                try {
                    JSONObject jObj = new JSONObject(s);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
//                        String uid = jObj.getString("id");

//                        JSONObject user = jObj.getJSONObject("user");
                        JSONObject user = jObj.getJSONObject("user");
                        JWT jwt = new JWT(jObj.getString("token"));
                        String ids = jwt.getId();
                        Log.e("email",ids);
                        String uid = user.getString("id");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String family_phone = user.getString("family_phone");
//                        String token = refreshedToken;
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(uid,name, email, created_at,family_phone);

                        Toast.makeText(getApplicationContext(), "email " + email, Toast.LENGTH_LONG).show();
                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                panduan.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
//                        String errorMsg = jObj.getString("error_msg");
                        JSONObject user = jObj.getJSONObject("user");
                        String errorMsg = user.getString("message");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put("idToken", token);
                data.put("token", refreshedToken);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(AppConfig.URL_LOGIN_SOCIAL,data);
                return s;
            }
        }
        LoginSocial ge = new LoginSocial();
        ge.execute();
    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password,final String refreshedToken) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();
        Log.d(TAG, "Login : " + email + ":"+password+":"+refreshedToken);
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
//                        String uid = jObj.getString("id");

//                        JSONObject user = jObj.getJSONObject("user");
                        JSONObject user = jObj.getJSONObject("user");
                        JWT jwt = new JWT(jObj.getString("token"));
                        String ids = jwt.getId();
                        Log.e("email",ids);
                        String uid = user.getString("id");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String family_phone = user.getString("family_phone");
//                        String token = refreshedToken;
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(uid,name, email, created_at,family_phone);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                panduan.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
//                        String errorMsg = jObj.getString("error_msg");
                        JSONObject user = jObj.getJSONObject("user");
                        String errorMsg = user.getString("message");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                params.put("token", refreshedToken);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
