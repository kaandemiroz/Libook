package com.comp319.okd.libook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by O. Kaan Demiröz on 21.4.2015.
 * Top-down view of the library where the user can choose a table to
 * announce as free and modify to add messages
 */
public class LibraryBookView extends LibraryView{

    protected LayoutParams params;
    protected Bitmap background;
    protected BitmapShader shader;
    protected Matrix matrix;
    protected Paint shaderPaint;
    protected Paint circlePaint;
    protected boolean drawShader;
    protected float realX;
    protected float realY;

    public LibraryBookView(Context context) {
        super(context);
        init();
    }

    public LibraryBookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LibraryBookView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // Initialize view
    private void init(){
        bookImage = new ImageView(getContext());
        bookImage.setImageResource(R.drawable.book);
        params = new RelativeLayout.LayoutParams(imageSize,imageSize);
        // Magnifying class
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(3);
        shaderPaint = new Paint();

        matrix = new Matrix();
        // Start at floor 0
        setFloor(0);
    }

    @Override
    public void setFloor(int floor){
        // Change displayed floor
        super.setFloor(floor);
        // Load new background for magnifying glass
        background = BitmapFactory.decodeResource(getContext().getResources(), drawableID);
        background = Bitmap.createScaledBitmap(background,(int)(300*density),(int)(400*density),true);
        shader = new BitmapShader(background, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shaderPaint.setShader(shader);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        int action = event.getAction();
        realX = Math.max(Math.min(event.getX(),getWidth()-10),0);
        realY = Math.max(Math.min(event.getY()-70,getHeight()-10),0);

        x = (int)(Math.max(Math.min(event.getX(),getWidth()-10),0)/density/scale);
        y = (int)(Math.max(Math.min(event.getY()-70,getHeight()-10),0)/density/scale);
        //x and y are the coordinates of the touched location

        if(action==MotionEvent.ACTION_MOVE || action==MotionEvent.ACTION_DOWN){
            //If the touch was a down press, display magnifying glass
            drawShader = true;
            params.topMargin=(int)(/*y*12.5*scale*/Math.max(Math.min(event.getY()-12.5/2-70,getHeight()-10-12.5/2),0));
            params.leftMargin=(int)(/*x*12.5*scale*/Math.max(Math.min(event.getX()-12.5/2,getWidth()-10-12.5/2),0));
            // Remove previous book and add a new one
            removeView(bookImage);
            addView(bookImage, params);
            matrix.reset();
            matrix.postScale(2f, 2f, event.getX(), event.getY()-70);
            shader.setLocalMatrix(matrix);
        }else if(action==MotionEvent.ACTION_UP){
            // Remove magnifying glass when finger is lifted
            drawShader = false;
            invalidate();
            final Toast toast = Toast.makeText(getContext(),"X: " + x + ", Y: " + y,Toast.LENGTH_SHORT);
            toast.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 150);
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(drawShader){
            //float cx = x*12.5f*scale;
            //float cy = y*12.5f*scale;
            int size = 70;
            canvas.drawCircle(realX, realY, size, circlePaint);
            canvas.drawCircle(realX, realY, size, shaderPaint);
        }
    }

}
