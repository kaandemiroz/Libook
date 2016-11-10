package com.comp319.okd.libook;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by O. Kaan Demiröz on 22.4.2015.
 */
public class LibraryBrowseView extends LibraryView{

    ImageView[] books = new ImageView[20];
    ArrayList<Table> tables = null;

    public LibraryBrowseView(Context context) {
        super(context);
    }

    public LibraryBrowseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LibraryBrowseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(ArrayList<Table> tables){
        this.tables = tables;
        update();
    }

    private void update(){
        removeAllViews();
        int count = 0;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int i=0; i<tables.size(); i++){
            Table table = tables.get(i);
            if(getFloor()!=table.getFloor())continue;
            bookImage = (ImageView) inflater.inflate(R.layout.bookimage,null);
            RelativeLayout.LayoutParams params = new LayoutParams(imageSize,imageSize);
            params.topMargin = (int)(table.getY()*12.5*scale);
            params.leftMargin = (int)(table.getX()*12.5*scale);
            books[count] = bookImage;
            count++;
            addView(bookImage,params);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if(tables == null) return false;
        int action = event.getAction();
        x = (int)(event.getX()/scale/12.5);
        y = (int)(event.getY()/scale/12.5);
        //x and y are the coordinates of the touched location

        if(action==MotionEvent.ACTION_DOWN){
            //If the touch was a down press
            BrowseActivity browseActivity = (BrowseActivity) getContext();
            browseActivity.photoFile = null;
            browseActivity.audioFile = null;
            for(int i=0; i<tables.size(); i++){
                Table table = tables.get(i);
                int difX = table.getX() - x;
                int difY = table.getY() - y;
                if(getFloor() == table.getFloor() && (difX < 2 && difX > -2) && (difY < 2 && difY > -2)){
                    browseActivity.tableIndex = i;
                    File photoFile = table.getPhoto();
                    File audioFile = table.getAudio();
                    if(photoFile!=null)browseActivity.photoFile = photoFile;
                    if(audioFile!=null)browseActivity.audioFile = audioFile;
                    browseActivity.showOptions();
                    break;
                }
            }

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
    public void setFloor(int floor){
        super.setFloor(floor);
        if(tables!=null)update();
    }


}
