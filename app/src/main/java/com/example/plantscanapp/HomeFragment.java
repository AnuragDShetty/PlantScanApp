package com.example.plantscanapp;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class HomeFragment extends Fragment {

    ImageView image,fab;
    Button submit;
    Dialog prompt;
    int loggin=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home,container,false);

        image=(ImageView)view.findViewById(R.id.image);
        submit=(Button)view.findViewById(R.id.subBtn);
        fab=(ImageView)view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelp();
            }
        });

        prompt=new Dialog(getContext());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loggin==1){
                    submitImage();
                }else{
                    promptLogin();
                }
            }
        });

        return view;
    }

    private void showHelp() {

        prompt.setContentView(R.layout.prompt_help);
        TextView close=(TextView)prompt.findViewById(R.id.helpClose);
        prompt.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prompt.dismiss();
            }
        });
    }

    private void submitImage() {
    }

    private void promptLogin() {
        TextView message,sug1,sug2;
        prompt.setContentView(R.layout.prompt_template);
        message=prompt.findViewById(R.id.message);
        sug1=prompt.findViewById(R.id.suggestion1);
        sug2=prompt.findViewById(R.id.suggestion2);

        message.setText("You have'nt logged in. Please login to proceed further.");
        sug1.setText("Login");
        sug2.setText("Register");

        sug1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SigninFragment()).commit();
                prompt.dismiss();
            }
        });

        sug2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new RegisterFragment()).commit();
                prompt.dismiss();
            }
        });

        prompt.show();
    }
}
