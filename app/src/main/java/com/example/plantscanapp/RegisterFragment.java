package com.example.plantscanapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterFragment extends Fragment {

    Button login_btn;
    TextView unamer,uemail;
    EditText user,pass;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register,
                container, false);
        login_btn = (Button) view.findViewById(R.id.login);
        user = (EditText) view.findViewById(R.id.uname);
        unamer = (TextView) getActivity().findViewById(R.id.unamehead);
        uemail = (TextView) getActivity().findViewById(R.id.uemailhead);
        pass = (EditText) view.findViewById(R.id.upass);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Welcome "+user.getText().toString() ,Toast.LENGTH_SHORT).show();
                unamer.setText(""+user.getText().toString());
                uemail.setText(""+user.getText().toString()+"@gmail.com");
            }
        });

        return view;
    }
}
