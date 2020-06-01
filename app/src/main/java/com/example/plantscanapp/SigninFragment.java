package com.example.plantscanapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.plantscanapp.helpers.UserDisplayHelper;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SigninFragment extends Fragment {

    Button login_btn;
    TextView unamer,uemail,logout;
    EditText email,pass;
    ProgressDialog progress;
    SharedPreferences sharedPreferences;
    RequestQueue queue;
    Dialog prompt;
    View view;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signin, container, false);

        NavigationView navigationView =getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_login);

        queue= Volley.newRequestQueue(getContext());
        sharedPreferences = getActivity().getSharedPreferences("PlantScanApp", MODE_PRIVATE);
        prompt=new Dialog(getContext());

        login_btn = (Button) view.findViewById(R.id.login);
        email = (EditText) view.findViewById(R.id.uemail);
        pass = (EditText) view.findViewById(R.id.upass);
        logout=(TextView)view.findViewById(R.id.logout);

        progress = new ProgressDialog(getContext());
        progress.setTitle("Logging in");
        progress.setMessage("Just a Moment");

        checkUser();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uemail,upass;
                uemail=email.getText().toString();
                upass=pass.getText().toString();
                if(uemail.equals("") || !uemail.contains("@") || upass.equals("")){
                    Toast.makeText(getActivity(), "Please enter valid data in the fields", Toast.LENGTH_SHORT).show();
                }else login(uemail,upass);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                promptLogout(view);
            }
        });

        return view;
    }

    private void promptLogout(final View view) {
        TextView message,sug1,sug2;
        prompt.setContentView(R.layout.prompt_template);
        message=prompt.findViewById(R.id.message);
        sug1=prompt.findViewById(R.id.suggestion1);
        sug2=prompt.findViewById(R.id.suggestion2);

        message.setText("Are you sure you want to log out?");
        sug1.setText("Yes");
        sug2.setText("No");

        sug1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.remove("email");
                editor.remove("name");
                editor.commit();
                checkUser();
                setUserDataToNav();
                prompt.dismiss();
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

    private void checkUser() {
        String def="#####";
        LinearLayout ll=(LinearLayout)view.findViewById(R.id.loginLayout);
        RelativeLayout rl=(RelativeLayout)view.findViewById(R.id.messageLayout);
        if(sharedPreferences.getString("email",def)==def){
            rl.setVisibility(View.INVISIBLE);
            ll.setVisibility(View.VISIBLE);
        }else{
            TextView loginEmail=(TextView)view.findViewById(R.id.loggedInEmail);
            TextView loginUser=(TextView)view.findViewById(R.id.loggedInUser);
            ll.setVisibility(View.INVISIBLE);
            loginEmail.setText(sharedPreferences.getString("email",def));
            loginUser.setText(sharedPreferences.getString("name",def));
            rl.setVisibility(View.VISIBLE);
        }
    }

    private void login(final String uemail,final String upass) {
        progress.show();
        String url="https://try-26-ifl.herokuapp.com/login";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        if(response.equals("0")){
                            Toast.makeText(getActivity(), "Please check the email and password", Toast.LENGTH_SHORT).show();
                            email.setText("");
                            pass.setText("");
                        }else{
                            Toast.makeText(getActivity(), "Successfully logged in.", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("email",uemail);
                            editor.putString("name",response);
                            editor.commit();
                            email.setText("");
                            pass.setText("");
                            checkUser();
                            setUserDataToNav();
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
                parms.put("email",uemail);
                parms.put("pass",upass);
                return parms;
            }
        };
        queue.add(stringRequest);
    }


    public void setUserDataToNav(){
        String def="#####";
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("PlantScanApp", MODE_PRIVATE);
        ImageView im=(ImageView)getActivity().findViewById(R.id.imageView);
        TextView name=(TextView)getActivity().findViewById(R.id.unamehead);
        TextView email=(TextView)getActivity().findViewById(R.id.uemailhead);
        if(sharedPreferences.getString("name",def)==def){
            im.setImageDrawable(getResources().getDrawable(R.drawable.default_user));
            name.setText("Not Logged In");
            email.setText("");
        }else{
            UserDisplayHelper userDisplayHelper=new UserDisplayHelper();
            String uname=sharedPreferences.getString("name",def);
            im.setImageBitmap(userDisplayHelper.createImage(150,150,1,(uname.charAt(0)+"").toUpperCase()));
            name.setText(uname);
            email.setText(sharedPreferences.getString("email",def));
        }
    }
}
