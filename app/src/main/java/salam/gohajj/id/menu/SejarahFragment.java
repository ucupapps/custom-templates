package salam.gohajj.id.menu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import salam.gohajj.id.R;
import salam.gohajj.id.activity.CustomListSejarah;
import salam.gohajj.id.activity.RequestHandler;
import salam.gohajj.id.app.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import me.anwarshahriar.calligrapher.Calligrapher;


public class SejarahFragment extends Fragment implements ListView.OnItemClickListener {

    private ListView listView;
    private String JSON_STRING;

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private Dialog dialog;
    private TextView txtid;
    private static SQLiteDatabase database;

    public SejarahFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.fragment_panduan, container, false);
        Calligrapher calligrapher=new Calligrapher(getActivity());
        calligrapher.setFont(getActivity(),"fonts/helvetica.ttf",true);

        // Inflate the layout for this fragment
        listView = (ListView) vi.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        txtid=(TextView) vi.findViewById(R.id.txtid);
        getJSON();
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
                String name = jo.getString(AppConfig.KEY_NAME);
                String des = jo.getString(AppConfig.KEY_DESCRIPTION);
                String lat = jo.getString(AppConfig.KEY_LAT);
                String lng = jo.getString(AppConfig.KEY_LNG);
                String phone = jo.getString(AppConfig.KEY_PHONE);
                String alamatPoi = jo.getString(AppConfig.KEY_ALAMAT);
                String catPoi = jo.getString(AppConfig.KEY_CATEGORY);
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_ID,id);
                data.put(AppConfig.KEY_NAME,name);
                data.put(AppConfig.KEY_DESCRIPTION,des);
                data.put(AppConfig.KEY_LAT,lat);
                data.put(AppConfig.KEY_LNG,lng);
                data.put(AppConfig.KEY_PHONE,phone);
                data.put(AppConfig.KEY_ALAMAT,alamatPoi);
                data.put(AppConfig.KEY_CATEGORY,catPoi);
                list.add(data);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomListSejarah adapter = new CustomListSejarah(getContext(), list,
                R.layout.list_poi_cat, new String[] { AppConfig.KEY_ID,AppConfig.KEY_NAME,AppConfig.KEY_DESCRIPTION,AppConfig.KEY_ALAMAT },
                new int[] { R.id.txtIDPoi,R.id.txtNamaPoi,R.id.txtalamatPoi });
        listView.setAdapter(adapter);
        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
    }


    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                loading.dismiss();
                JSON_STRING = s;
                showData();
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(AppConfig.KEY_CATEGORY, "Sejarah Islam");
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(AppConfig.URL_POI_CAT, data);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        View vi=view;
//        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
//        final String strID = map.get(AppConfig.TAG_ID).toString();
//        final String strFile = map.get(AppConfig.KEY_FILE).toString();
//        final TextView txtaudio = (TextView)view.findViewById(R.id.txtAudio);
//        final TextView txtImg = (TextView)view.findViewById(R.id.txtImg);
//        final String jenis = txtImg.getText().toString().trim();
////        txtaudio.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
//                    if(jenis.equals("Video")){
//                        Intent intent = new Intent(getActivity(), ViewPanduanVideo.class);
//                        intent.putExtra(AppConfig.EMP_ID,strID);
//                        intent.putExtra(AppConfig.KEY_FILE,strFile);
//                        startActivity(intent);
//                    }else if(jenis.equals("Doa")){
//                        Intent intent = new Intent(getActivity(), ViewPanduanDoa.class);
//                        intent.putExtra(AppConfig.EMP_ID,strID);
//                        intent.putExtra(AppConfig.KEY_FILE,strFile);
//                        startActivity(intent);
//                    }else if(jenis.equals("Tips")){
//                        Intent intent = new Intent(getActivity(), ViewPanduanTips.class);
//                        intent.putExtra(AppConfig.EMP_ID,strID);
//                        intent.putExtra(AppConfig.KEY_FILE,strFile);
//                        startActivity(intent);
//                    }else if(jenis.equals("Kamus")){
//                        Intent intent = new Intent(getActivity(), ViewPanduankamus.class);
//                        intent.putExtra(AppConfig.EMP_ID,"1");
//                        startActivity(intent);
//                    }
//                    if(jenis.equals("Download")){
//                        File folder = new File("/sdcard/android/data/com.gohajj.id/panduan1");
//                        boolean success = true;
//                        if (!folder.exists()) {
//                            folder.mkdirs();
//                        }
//                        txtid.setText(strID);
//                        startDownload(strID);
//                    }else{
//                        Intent intent = new Intent(getActivity(), ViewPanduan.class);
//                        intent.putExtra(AppConfig.EMP_ID,strID);
//                        intent.putExtra(AppConfig.KEY_MESSAGE,"1");
//                        startActivity(intent);
//                    }
//                }
//            });

//        Log.d("MyDataShow", "status: " + strID);
    }

}
