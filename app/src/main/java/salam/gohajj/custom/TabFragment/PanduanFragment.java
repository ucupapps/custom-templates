package salam.gohajj.custom.TabFragment;

import android.Manifest;
import android.app.Dialog;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import me.anwarshahriar.calligrapher.Calligrapher;
import salam.gohajj.custom.Interfaces;
import salam.gohajj.custom.R;
import salam.gohajj.custom.Utilities;
import salam.gohajj.custom.activity.CustomListPanduan1;
import salam.gohajj.custom.activity.RequestHandler;
import salam.gohajj.custom.app.AppConfig;
import salam.gohajj.custom.menu.ViewPanduan;
import salam.gohajj.custom.menu.ViewPanduanDoa;
import salam.gohajj.custom.menu.ViewPanduanTips;
import salam.gohajj.custom.menu.ViewPanduanVideo;
import salam.gohajj.custom.menu.ViewPanduankamus;
import salam.gohajj.custom.menu.panduan;
import salam.gohajj.custom.menu.panduan_umrah;
import salam.gohajj.custom.menu.profile;


public class PanduanFragment extends Fragment implements ListView.OnItemClickListener, AbsListView.OnScrollListener {

    private ListView lv_sebelumUmrah,lv_saatUmrah,lv_setelahUmrah;
    private LinearLayout lin_sebelumUmrah,lin_saatUmrah,lin_sesudahUmrah;
    private boolean isLoading;
    private String JSON_STRING;
    private TextView jak_degree,jak_cuaca,jak_date,jak_time,mek_degree,mek_cuaca,mek_date,mek_time;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private Dialog dialog;
    private TextView txtid;
    private static final String[] requiredPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.RECORD_AUDIO,
    };
    private SQLiteDatabase database;
    public PanduanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
    }
    private int preLast;
    private int scrl;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.tab_panduan, container, false);
        database = getActivity().openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);

        Calligrapher calligrapher=new Calligrapher(getActivity());
        calligrapher.setFont(getActivity(),"fonts/helvetica.ttf",true);
//        TextView txtkoneksi= (TextView) vi.findViewById(R.id.txtkoneksi);
//
//        if (!Utilities.cek_status(getContext()))
//        {
//            txtkoneksi.setVisibility(View.VISIBLE);
//        }else{
//            txtkoneksi.setVisibility(View.GONE);
//        }

        // Inflate the layout for this fragment
        lv_sebelumUmrah = (ListView) vi.findViewById(R.id.lv_sebelum_umrah);
        lv_sebelumUmrah.setOnItemClickListener(this);
//        lv_saatUmrah = (ListView) vi.findViewById(R.id.lv_saat_umrah);
//        lv_saatUmrah.setOnItemClickListener(this);
//        lv_setelahUmrah = (ListView) vi.findViewById(R.id.lv_sesudah_umrah);
//        lv_setelahUmrah.setOnItemClickListener(this);
        txtid=(TextView) vi.findViewById(R.id.txtid);
        isLoading=false;
        lv_sebelumUmrah.setOnScrollListener(this);
//        getJSON("Sebelum umrah");
//        scrl=1;
        //getJSON("Saat umrah");
        //getJSON("Setelah umrah");




        lv_sebelumUmrah.setVisibility(View.GONE);
        lin_sebelumUmrah = (LinearLayout)vi.findViewById(R.id.lin_sebelum_umrah);
        lin_saatUmrah = (LinearLayout)vi.findViewById(R.id.lin_saat_umrah);
        lin_sesudahUmrah = (LinearLayout)vi.findViewById(R.id.lin_sesudah_umrah);

        Utilities.putPref(Interfaces.PANDUAN_UMRAH,Interfaces.STRING_DEFAULT,getContext());

        lin_sebelumUmrah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), panduan_umrah.class);
                Utilities.putPref(Interfaces.PANDUAN_UMRAH,Interfaces.SEBELUM_UMRAH,getContext());
                startActivity(i);
                getActivity().finish();
            }
        });
        lin_saatUmrah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), panduan_umrah.class);
                Utilities.putPref(Interfaces.PANDUAN_UMRAH,Interfaces.SAAT_UMRAH,getContext());
                startActivity(i);
                getActivity().finish();
            }
        });
        lin_sesudahUmrah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), panduan_umrah.class);
                Utilities.putPref(Interfaces.PANDUAN_UMRAH,Interfaces.SESUDAH_UMRAH,getContext());
                startActivity(i);
                getActivity().finish();
            }
        });
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

                CustomListPanduan1 adapter1 = new CustomListPanduan1(getContext(), list,
                        R.layout.list_panduan, new String[]{AppConfig.KEY_ID, AppConfig.KEY_NAME, AppConfig.KEY_JENIS, AppConfig.KEY_FILE},
                        new int[]{R.id.txtNO, R.id.txtNAME, R.id.txtImg});
                lv_sebelumUmrah.setAdapter(adapter1);
                ((BaseAdapter) lv_sebelumUmrah.getAdapter()).notifyDataSetChanged();
            isLoading=false;
//                CustomListPanduan1 adapter2 = new CustomListPanduan1(getContext(), list,
//                    R.layout.list_panduan, new String[]{AppConfig.KEY_ID, AppConfig.KEY_NAME, AppConfig.KEY_JENIS, AppConfig.KEY_FILE},
//                    new int[]{R.id.txtNO, R.id.txtNAME, R.id.txtImg});
//                lv_saatUmrah.setAdapter(adapter2);
//                ((BaseAdapter) lv_saatUmrah.getAdapter()).notifyDataSetChanged();
//                CustomListPanduan1 adapter3 = new CustomListPanduan1(getContext(), list,
//                    R.layout.list_panduan, new String[]{AppConfig.KEY_ID, AppConfig.KEY_NAME, AppConfig.KEY_JENIS, AppConfig.KEY_FILE},
//                    new int[]{R.id.txtNO, R.id.txtNAME, R.id.txtImg});
//                lv_setelahUmrah.setAdapter(adapter3);
//                ((BaseAdapter) lv_setelahUmrah.getAdapter()).notifyDataSetChanged();

        } catch (JSONException e) {
//            e.printStackTrace();
        }
    }


    private void getJSON(final String category){
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
                data.put(AppConfig.KEY_CATEGORY, category);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(AppConfig.URL_PANDUAN, data);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void startDownload(String id) {
        String url = AppConfig.URL_HOME+"/uploads/doa/"+id+".mp3";
        new PanduanFragment.DownloadFileAsync().execute(url);
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

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (lv_sebelumUmrah.getAdapter() == null)
            return ;

        if (lv_sebelumUmrah.getAdapter().getCount() == 0)
            return ;

        int l = visibleItemCount + firstVisibleItem;
        if (l >= totalItemCount && !isLoading) {
            // It is time to add new data. We call the listener
            isLoading = true;
            if(scrl==1){
                getJSON("saat umrah");
                scrl++;
            }else if(scrl==2){
                getJSON("setelah umrah");
                scrl++;
            }
            else if(scrl==3){

            }
        }
        // sik bagian kene durung dadi
        else if(l < totalItemCount && !isLoading)
        {
            isLoading=true;
            if(scrl==1){

            }else if(scrl==2){
                getJSON("sebelum umrah");
                scrl--;
            }else if(scrl==3){
                getJSON("saat umrah");
            scrl--;
            }
        }
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
                OutputStream output = new FileOutputStream("/sdcard/android/data/salam.gohajj.id/panduan1/"+id+".mp3");

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
//                @Override
//                public void onClick(View view) {
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
//        if(jenis.equals("Download")){
//            File folder = new File("/sdcard/android/data/salam.gohajj.custom/panduan1");
//            boolean success = true;
//            if (!folder.exists()) {
//                folder.mkdirs();
//            }
//            txtid.setText(strID);
//            startDownload(strID);
//        }else{
//            Intent intent = new Intent(getActivity(), ViewPanduan.class);
//            intent.putExtra(AppConfig.EMP_ID,strID);
//            intent.putExtra(AppConfig.KEY_MESSAGE,"1");
//            startActivity(intent);
//        }
//    }
//});

//        Log.d("MyDataShow", "status: " + strID);
    }

}
