package com.example.plantscanapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.plantscanapp.adapter.HistoryAdapter;
import com.example.plantscanapp.model.AppDataBase;
import com.example.plantscanapp.model.HistoryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class HistoryFragment extends Fragment {
    ListView listView;
    TextView message;
    RequestQueue queue;
    List<HistoryModel> historyModelList=new ArrayList<>();
    HistoryAdapter adapter;
    ProgressDialog progress;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_history,container,false);

        queue=Volley.newRequestQueue(getContext());
        listView=(ListView)view.findViewById(R.id.historyList);
        message=(TextView)view.findViewById(R.id.message);
        progress = new ProgressDialog(getContext());
        progress.setTitle("Loading");
        progress.setMessage("Just a Moment");
        sharedPreferences=getActivity().getSharedPreferences("PlantScanApp", MODE_PRIVATE);


        return view;
    }

    private void getHistory() {
        AppDataBase adb=new AppDataBase(getContext());
        Cursor c=adb.getAllData();

        if(c.getCount()==0){
            progress.dismiss();
            listView.setVisibility(View.INVISIBLE);
            message.setVisibility(View.VISIBLE);
            message.setText("No history to show.");
        }else {
            message.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getContext(),Description.class);
                    intent.putExtra("code",historyModelList.get(i).getId());
                    startActivity(intent);
                }});
            while(c.moveToNext()){
                HistoryModel history = new HistoryModel();
                history.setId(c.getString(0));
                history.setName(c.getString(1));
                history.setDate(c.getString(2));
                historyModelList.add(history);
            }
            adapter = new HistoryAdapter(getActivity(), historyModelList);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            progress.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String def="######";
        historyModelList.clear();
        if(sharedPreferences.getString("email",def).equals(def)){
            listView.setVisibility(View.INVISIBLE);
            message.setVisibility(View.VISIBLE);
            message.setText("You need to be logged in to view your history.");
        }else{
            progress.show();
            getHistory();
        }
    }
}