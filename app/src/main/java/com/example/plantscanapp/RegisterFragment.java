package com.example.plantscanapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterFragment extends Fragment {

    Button emp_btn;
    TextView user,uname,uemail;
    TextView pass;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        emp_btn = (Button) container.findViewById(R.id.login);
        user = (TextView) container.findViewById(R.id.uname);
        uname = (TextView) container.findViewById(R.id.unamehead);
        uemail = (TextView) container.findViewById(R.id.uemailhead);
        pass = (TextView) container.findViewById(R.id.upass);

            Toast.makeText(getActivity(),"Welcome "+user.getText().toString() ,Toast.LENGTH_SHORT).show();
        uname.setText(""+user);
        uemail.setText(""+user+"@gmail.com");


        return inflater.inflate(R.layout.fragment_register,container,false);
    }
}
