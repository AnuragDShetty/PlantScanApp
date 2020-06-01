package com.example.plantscanapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.plantscanapp.model.AppDataBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Description extends AppCompatActivity {
    RequestQueue queue;
    TextView name,desc,sol,confidence;
    Button retry;
    ImageView example;
    ScrollView page;
    ProgressDialog progress;
    String prob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        queue= Volley.newRequestQueue(this);

        final String id=getIntent().getStringExtra("code");
        prob=getIntent().getStringExtra("probs");


        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Just a Moment");



        page=(ScrollView)findViewById(R.id.page);
        page.setVisibility(View.INVISIBLE);

        name=(TextView)findViewById(R.id.name);
        desc=(TextView)findViewById(R.id.desc);
        sol=(TextView)findViewById(R.id.sol);
        confidence=(TextView)findViewById(R.id.confidence);
        example=(ImageView)findViewById(R.id.example);

        retry=(Button)findViewById(R.id.retry);
        retry.setVisibility(View.INVISIBLE);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.show();
                getInfoFromId(id);
            }
        });
        progress.show();
        getInfoFromId(id);
    }

    private void getInfoFromId(final String id) {
        String url="http://try-26-ifl.herokuapp.com/getDiseasesById";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        page.setVisibility(View.VISIBLE);
                        retry.setVisibility(View.INVISIBLE);
                        try {
                            page.setVisibility(View.VISIBLE);
                            retry.setVisibility(View.INVISIBLE);
                            JSONObject data = new JSONObject(response);
                            setOrUpdateDataToLocalDatabase(data.getString("id"),data.getString("name"),data.getString("description"),data.getString("solution"),data.getString("datetime"));
                        }catch (JSONException e){
                            page.setVisibility(View.INVISIBLE);
                            retry.setVisibility(View.VISIBLE);
                            Toast.makeText(Description.this, "Server error. try Later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                page.setVisibility(View.INVISIBLE);
                retry.setVisibility(View.VISIBLE);
                Toast.makeText(Description.this, "Please try again.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> param=new HashMap<>();
                param.put("id",id);
                return param;
            }
        };
        queue.add(stringRequest);
    }

    private void setOrUpdateDataToLocalDatabase(String id,String name,String desc,String sol,String datetime) {
        AppDataBase db=new AppDataBase(this);
        boolean present=db.isPresent(id);
        if(present){
            //update
            db.updateDate(id,datetime);
        }else{
            db.insertData(id,name,datetime);
        }
        display(id,name, desc, sol);
    }

    private void display(String id,String Dname,String Ddesc,String Dsol) {
        name.setText(Dname);

        if(prob==null){
            confidence.setVisibility(View.INVISIBLE);
        }else{
            confidence.setText(prob+"% confidence");
        }

        desc.setText(Ddesc);
        sol.setText(Dsol);
        String url="https://finderrwebsite.000webhostapp.com/img/diseases/"+id+".JPG";
        Glide.with(this).load(url).into(example);
    }
}
