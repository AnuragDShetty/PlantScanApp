package com.example.plantscanapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.BitmapCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.plantscanapp.model.AppDataBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {

    ImageView image,fab;
    Button submit;
    Dialog prompt,selectionMode;
    RequestQueue queue;
    SharedPreferences sharedPreferences;
    int loggin=1;
    private static final int CAMERA_REQUEST = 2000;
    private static final int DIRECTORY_REQUEST=3000;
    String sendData="";
    ProgressDialog progress;
    String currentPhotoPath;

    int bitmapByteCount=0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home,container,false);

        queue= Volley.newRequestQueue(getContext());
        sharedPreferences = getActivity().getSharedPreferences("PlantScanApp", MODE_PRIVATE);

        progress = new ProgressDialog(getContext());
        progress.setTitle("Loading");
        progress.setMessage("Just a Moment");


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
        selectionMode=new Dialog(getContext());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String def="#####";
                if(sharedPreferences.getString("email",def).equals(def)){
                    promptLogin();
                }else{
                    if(sendData.length()!=0) {
                        progress.show();
                        submitImage();
                    }
                    else{
                        Toast.makeText(getActivity(), "Pick an Image to send.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String def="#####";
                if(sharedPreferences.getString("email",def).equals(def)){
                    promptLogin();
                }else
                    selectMode();
                }
        });

        return view;
    }

    private void selectMode() {
        ImageView dir,cam;
        selectionMode.setContentView(R.layout.image_source_selector);
        dir=selectionMode.findViewById(R.id.imgdir);
        cam=selectionMode.findViewById(R.id.imgcam);
        dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionMode.dismiss();
                launchDirectory();
            }
        });

        cam.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                selectionMode.dismiss();
                launchCamera();
            }
        });
        selectionMode.show();
    }

    private void launchCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getActivity(), "Permission not granted.", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void launchDirectory() {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"pick image"),DIRECTORY_REQUEST);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            File f=new File(currentPhotoPath);
            Uri uri=Uri.fromFile(f);
            InputStream is=null;
            try{
                is=getActivity().getContentResolver().openInputStream(uri);
            }catch(Exception e){
                e.printStackTrace();
            }
            Bitmap bm= getSquareBitmap(getResizedBitmap(BitmapFactory.decodeStream(is),1000));
            bitmapByteCount= BitmapCompat.getAllocationByteCount(bm);

            image.setImageBitmap(bm);
            sendData = BitMapToString(bm);
//                File f=new File(currentPhotoPath);
//                image.setImageURI(Uri.fromFile(f));
//            Bitmap bm = getSquareBitmap((Bitmap) data.getExtras().get("data"));
//            image.setImageBitmap(bm);
//            sendData = BitMapToString(bm);
        }
        else if(requestCode==DIRECTORY_REQUEST && resultCode==RESULT_OK){
            Uri uri=data.getData();
            InputStream is=null;
            try{
                is=getActivity().getContentResolver().openInputStream(uri);
            }catch(Exception e){
                e.printStackTrace();
            }
            Bitmap bm= getSquareBitmap(BitmapFactory.decodeStream(is));
            image.setImageBitmap(bm);
            sendData = BitMapToString(bm);
        }

    }

    public Bitmap getSquareBitmap(Bitmap bm){
        int height=bm.getHeight();
        int width=bm.getWidth();
        if(width<=height){
            int y=(height/2)-(width/2);
            int x=0;
            bm = Bitmap.createBitmap(bm,x,y,width, width);
        }else{
            int y=0;
            int x=(width/2)-(height/2);
            bm = Bitmap.createBitmap(bm,x,y,height, height);
        }
        return bm;
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
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
        String url="http://ec2-18-220-108-110.us-east-2.compute.amazonaws.com";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try{
                            JSONObject data=new JSONObject(response);
                            if(data.getString("error").equals("YES")){
                                Toast.makeText(getActivity(), "Some error occurred", Toast.LENGTH_SHORT).show();
                            }else{
                                String code=data.getString("result");
                                String probs=data.getString("prob");
                                Intent i = new Intent(getContext(),Description.class);
                                i.putExtra("code",code);
                                i.putExtra("probs",probs);
                                startActivity(i);
                            }
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        Toast.makeText(getActivity(), ""+error, Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams()throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("image",sendData);
                return params;
            }
        };
        queue.add(stringRequest);
    }


    //prompts to login if the user hasnt done that and tries to test an image.
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
