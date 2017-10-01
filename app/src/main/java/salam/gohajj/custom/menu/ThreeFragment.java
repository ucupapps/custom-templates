package salam.gohajj.custom.menu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
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

import salam.gohajj.custom.R;
import salam.gohajj.custom.activity.CustomListPanduan3;
import salam.gohajj.custom.activity.RequestHandler;
import salam.gohajj.custom.app.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import me.anwarshahriar.calligrapher.Calligrapher;

import static salam.gohajj.custom.app.AppConfig.URL_HOME;
import static java.sql.Types.NULL;


public class ThreeFragment extends Fragment implements ListView.OnItemClickListener {

    private ListView listView;
    private String JSON_STRING;

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private Dialog dialog;
    private TextView txtid;

    public ThreeFragment() {
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
                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    String id = jo.getString(AppConfig.KEY_ID);
                    String name = jo.getString(AppConfig.KEY_NAME);
                    String jenis = jo.getString(AppConfig.KEY_JENIS);
                    String file = jo.getString(AppConfig.KEY_FILE);
                    HashMap<String, String> data = new HashMap<>();
                    data.put(AppConfig.KEY_ID, id);
                    data.put(AppConfig.KEY_NAME, name);
                    data.put(AppConfig.KEY_JENIS, jenis);
                    data.put(AppConfig.KEY_FILE, file);
                    list.add(data);
                }


                CustomListPanduan3 adapter = new CustomListPanduan3(getContext(), list,
                        R.layout.list_panduan, new String[]{AppConfig.KEY_ID, AppConfig.KEY_NAME, AppConfig.KEY_JENIS, AppConfig.KEY_FILE},
                        new int[]{R.id.txtNO, R.id.txtNAME, R.id.txtImg});
                listView.setAdapter(adapter);
                ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

        } catch (JSONException e) {
//            e.printStackTrace();
        }
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
                data.put(AppConfig.KEY_CATEGORY, "Setelah umrah");
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(AppConfig.URL_PANDUAN, data);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void startDownload(String id) {
        String url = URL_HOME+"/uploads/doa/"+id+".mp3";
        new ThreeFragment.DownloadFileAsync().execute(url);
    }

    HashMap<Integer, Dialog> mDialogs = new HashMap<Integer, Dialog>();

    public void showDialog(int dialogId){

        Dialog d = mDialogs.get(dialogId);
        if (d == null){

            d = onCreateDialog(dialogId);
            mDialogs.put(dialogId, d);
        }
        if (d != null){
            onPrepareDialog(d, dialogId);
            d.show();
        }

    }
    public void dissmissDialog(int dialogId){

        Dialog d = mDialogs.get(dialogId);
        d.dismiss();

    }
    public Dialog onCreateDialog(int id){
        //just create your Dialog here, once
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage("Downloading file..");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    public void onPrepareDialog(Dialog d, int dialogId){
//        super.onPrepareDialog(d, dialogId);
        // change something inside already created Dialogs here
    }


    class DownloadFileAsync extends AsyncTask<String, String, String> {
        final String id = txtid.getText().toString().trim();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {

                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream("/sdcard/android/data/salam.gohajj.custom/panduan3/"+id+".mp3");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                Intent i = new Intent(getActivity(), panduan.class);
                getActivity().finish();
                startActivity(i);
            } catch (Exception e) {}
            return null;

        }
        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            dissmissDialog(DIALOG_DOWNLOAD_PROGRESS);
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        View vi=view;
        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
        final String strID = map.get(AppConfig.TAG_ID).toString();
        final String strFile = map.get(AppConfig.KEY_FILE).toString();
        final TextView txtaudio = (TextView)view.findViewById(R.id.txtAudio);
        final TextView txtImg = (TextView)view.findViewById(R.id.txtImg);
        final String jenis = txtImg.getText().toString().trim();
//        txtaudio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
        if(jenis.equals("Video")){
            Intent intent = new Intent(getActivity(), ViewPanduanVideo.class);
            intent.putExtra(AppConfig.EMP_ID,strID);
            intent.putExtra(AppConfig.KEY_FILE,strFile);
            startActivity(intent);
        }else if(jenis.equals("Doa")){
            Intent intent = new Intent(getActivity(), ViewPanduanDoa.class);
            intent.putExtra(AppConfig.EMP_ID,strID);
            intent.putExtra(AppConfig.KEY_FILE,strFile);
            startActivity(intent);
        }else if(jenis.equals("Tips")){
            Intent intent = new Intent(getActivity(), ViewPanduanTips.class);
            intent.putExtra(AppConfig.EMP_ID,strID);
            intent.putExtra(AppConfig.KEY_FILE,strFile);
            startActivity(intent);
        }else if(jenis.equals("Kamus")){
            Intent intent = new Intent(getActivity(), ViewPanduankamus.class);
            intent.putExtra(AppConfig.EMP_ID,"1");
            startActivity(intent);
        }
//            }
//        });

//        Log.d("MyDataShow", "status: " + strID);
    }

}

