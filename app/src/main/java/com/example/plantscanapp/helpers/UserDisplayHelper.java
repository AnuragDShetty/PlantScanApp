package com.example.plantscanapp.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class UserDisplayHelper {
    public Bitmap createImage(int width, int height, int color, String name) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint2 = new Paint();
        Random r=new Random();
        paint2.setColor(Color.rgb(r.nextInt(225),r.nextInt(225),r.nextInt(225)));
        canvas.drawCircle(width/2,height/2,width/2,paint2);//(0F, 0F, (float) width, (float) height, paint2);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(75);
        paint.setTextScaleX(1);
        canvas.drawText(name, width/3, 2*height/3, paint);
        return bitmap;
    }
}
