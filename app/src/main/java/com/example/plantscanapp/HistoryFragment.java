package com.example.plantscanapp;

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
import com.example.plantscanapp.model.HistoryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {
    ListView listView;
    TextView message;
    RequestQueue queue;
    List<HistoryModel> historyModelList=new ArrayList<>();
    HistoryAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_history,container,false);

        queue=Volley.newRequestQueue(getContext());
        listView=(ListView)view.findViewById(R.id.historyList);
        message=(TextView)view.findViewById(R.id.message);

        int loggedin=1;
        if(loggedin==1){
            message.setVisibility(View.INVISIBLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getActivity(), ""+historyModelList.get(i).getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            listView.setVisibility(View.INVISIBLE);
        }

        getHistory();

        return view;
    }

    private void getHistory() {
        String url="https://try-26-ifl.herokuapp.com/history";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray jarray=json.getJSONArray("history");
                            for(int i=0;i<jarray.length();i++){
                                JSONObject data=jarray.getJSONObject(i);
                                HistoryModel history=new HistoryModel();
                                history.setName(data.getString("history"));
                                history.setDate("date");
                                historyModelList.add(history);
                            }
                            adapter=new HistoryAdapter(getActivity(),historyModelList);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), ""+error, Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String,String> getParams()throws AuthFailureError {
                Map<String,String> parms=new HashMap<String, String>();
                parms.put("email","krishna");
                return parms;
            }
        };
        queue.add(stringRequest);
    }
}
