package com.example.plantscanapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class RegisterFragment extends Fragment {


    EditText regname,regemail,regpass;
    Button registerbutton;
    RequestQueue queue;
    SharedPreferences sharedPreferences;
    ProgressDialog progress;
    Dialog prompt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register,
                container, false);

        NavigationView navigationView =getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_signup);

        sharedPreferences = getActivity().getSharedPreferences("PlantScanApp", MODE_PRIVATE);

        prompt=new Dialog(getContext());
        progress = new ProgressDialog(getContext());
        progress.setTitle("Registering");
        progress.setMessage("Just a Moment");

        registerbutton=(Button)view.findViewById(R.id.Register);
        regname=(EditText)view.findViewById(R.id.uregname);
        regemail=(EditText)view.findViewById(R.id.uregemail);
        regpass=(EditText)view.findViewById(R.id.uregpass);
        queue= Volley.newRequestQueue(getContext());

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=regname.getText().toString();
                String email=regemail.getText().toString();
                String pass=regpass.getText().toString();
                if(name.equals("") || email.equals("") || !email.contains("@") || pass.equals(""))
                    Toast.makeText(getActivity(), "Please enter valid data in the fields", Toast.LENGTH_SHORT).show();
                else prompt(name,email,pass);
            }
        });
        return view;
    }

    private void prompt(final String name,final String email,final String pass) {
        TextView message,sug1,sug2;
        prompt.setContentView(R.layout.prompt_template);
        message=prompt.findViewById(R.id.message);
        sug1=prompt.findViewById(R.id.suggestion1);
        sug2=prompt.findViewById(R.id.suggestion2);

        message.setText("Are you sure you want to register with name: "+name+" and email: "+email+" ?");
        sug1.setText("Yes");
        sug2.setText("No");

        sug1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prompt.dismiss();
                register(name,email,pass);
            }
        });

        sug2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prompt.dismiss();
            }
        });

        prompt.show();
    }


    private void register(final String name, final String email, final String pass) {
        progress.show();
        String url="https://try-26-ifl.herokuapp.com/register";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        if(response.equals("1")){
                            Toast.makeText(getActivity(), "Successfully registered.", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("email",email);
                            editor.commit();
                            regemail.setText("");
                            regpass.setText("");
                            regname.setText("");
                        }else if(response.equals("0")){
                            Toast.makeText(getActivity(), "Email is already registered.", Toast.LENGTH_SHORT).show();
                            regemail.setText("");
                            regpass.setText("");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        Toast.makeText(getActivity(), /*"Something went wrong. please try later."*/ ""+error, Toast.LENGTH_SHORT).show();
                    }
                }){
                @Override
                protected Map<String,String> getParams()throws AuthFailureError {
                    Map<String,String> parms=new HashMap<String, String>();
                    parms.put("email",email);
                    parms.put("pass",pass);
                    parms.put("name",name);
                    return parms;
                }
        };
        queue.add(stringRequest);
    }

}
