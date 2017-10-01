package salam.gohajj.custom.TabFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.readystatesoftware.viewbadger.BadgeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import me.anwarshahriar.calligrapher.Calligrapher;
import me.leolin.shortcutbadger.ShortcutBadger;
import salam.gohajj.custom.R;
import salam.gohajj.custom.Utilities;
import salam.gohajj.custom.activity.LoginActivity;
import salam.gohajj.custom.activity.RequestHandler;
import salam.gohajj.custom.app.AppConfig;
import salam.gohajj.custom.app.AppController;
import salam.gohajj.custom.chat.CustomList;
import salam.gohajj.custom.helper.SQLiteHandler;
import salam.gohajj.custom.helper.SessionManager;
import salam.gohajj.custom.menu.ViewInbox;


public class InboxFragment extends Fragment implements ListView.OnItemClickListener {

    private static final String TAG = "INBOX";

    private ListView listView;

    private ProgressDialog loading;

    String uid;
    private String JSON_STRING;
    private SessionManager session;
    private SQLiteHandler db;
    View target ;
    BadgeView badge ;
    private SQLiteDatabase database;
    private View footerView;
    public InboxFragment() {
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
        View vi = inflater.inflate(R.layout.tab_inbox, container, false);
        Calligrapher calligrapher=new Calligrapher(getContext());
        calligrapher.setFont(getActivity(),"fonts/helvetica.ttf",true);

        footerView = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, null, false);

        session = new SessionManager(getContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

       TextView txtkoneksi= (TextView) vi.findViewById(R.id.txtkoneksi);

        if (!Utilities.cek_status(getContext()))
        {
            txtkoneksi.setVisibility(View.VISIBLE);
        }else{
            txtkoneksi.setVisibility(View.GONE);
        }
        // SqLite database handler
        db = new SQLiteHandler(getContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
//        CountInbox(vi);
        //tracker
        AppController application = (AppController) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        // [START screen_view_hit]
        mTracker.setScreenName("Pesan");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
        //end


        session = new SessionManager(getContext());
        listView = (ListView) vi.findViewById(R.id.listView);
        listView.destroyDrawingCache();
        listView.setOnItemClickListener(this);

        if (session.isLoggedIn()) {
            getJSON();
        }

        return vi;
    }
    private void showData(){
        JSONObject jsonObject = null;
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(AppConfig.TAG_JSON_ARRAY);
            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(AppConfig.KEY_ID);
                String message = jo.getString(AppConfig.KEY_MESSAGE);
                String time = jo.getString(AppConfig.KEY_TIME);
                String from = jo.getString(AppConfig.KEY_FROM);
                String status = jo.getString(AppConfig.KEY_STATUS);
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_ID,id);
                data.put(AppConfig.KEY_MESSAGE,message);
                data.put(AppConfig.KEY_TIME,time);
                data.put(AppConfig.KEY_FROM,from);
                data.put(AppConfig.KEY_STATUS,status);
                list.add(data);
            }


            CustomList adapter = new CustomList(getContext(), list,
                    R.layout.list_chat, new String[] { AppConfig.KEY_ID,AppConfig.KEY_MESSAGE,AppConfig.KEY_TIME,AppConfig.KEY_FROM,AppConfig.KEY_STATUS },
                    new int[] { R.id.txtNO,R.id.txtMESSAGE,R.id.txtTIME,R.id.txtFROM });

            listView.setAdapter(adapter);
            ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
        } catch (JSONException e) {
//            e.printStackTrace();
        }
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String>{

            //            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getContext(),"",getResources().getString(R.string.mohon_tunggu)+"...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showData();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(AppConfig.URL_GET_INBOX,uid);
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
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
        String empId = map.get(AppConfig.TAG_ID).toString();
        StatusInbox(empId);
    }

    protected void CountInbox(View vi){
        Cursor c= database.rawQuery("select * from badge where id=1 ", null);
        c.moveToFirst();
        int jumlah=c.getInt(1);
        target = vi.findViewById(R.id.img_inbox);
        badge = new BadgeView(getContext(), target);
        //badge
        if(jumlah > 0) {
            badge.setText("" + jumlah);
            badge.show();
            ShortcutBadger.applyCount(getContext(), jumlah);
        }else{
            ShortcutBadger.removeCount(getContext());
            badge.hide();
        }
    }

    protected void StatusInbox(final String statID){
        class GetJSON extends AsyncTask<Void,Void,String>{

            //            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                String hsl = s.trim();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                Log.e(TAG,statID );
                String s = rh.sendGetRequestParam(AppConfig.URL_STATUS_INBOX,statID);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();

        Intent intent = new Intent(getContext(), ViewInbox.class);
        intent.putExtra(AppConfig.TAG_ID,statID);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        if (loading != null && loading.isShowing()) {
            loading.dismiss();
        }
        super.onDestroy();
    }
}
