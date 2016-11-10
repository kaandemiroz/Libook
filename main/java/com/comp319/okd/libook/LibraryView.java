package com.comp319.okd.libook;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by O. Kaan Demiröz on 22.4.2015.
 */
public abstract class LibraryView extends RelativeLayout{

    final float scale = getResources().getDisplayMetrics().density;
    ImageView bookImage;
    protected int imageSize;
    protected int floor;
    protected int drawableID;
    protected int x=-1;
    protected int y=-1;

    public LibraryView(Context context) {
        super(context);
        init();
    }

    public LibraryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LibraryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        imageSize = (int) (12.5*scale);
    }

    public int getClickedX(){
        return x;
    }

    public int getClickedY(){
        return y;
    }

    public int getFloor(){
        return floor;
    }

    public void setFloor(int floor){
        this.floor = floor;
        switch(floor){
            case -1:
                drawableID = R.drawable.fneg1;
                break;
            case 0:
                drawableID = R.drawable.f0;
                break;
            case 1:
                drawableID = R.drawable.f1;
                break;
            case 2:
                drawableID = R.drawable.f2;
                break;
        }
        setBackgroundResource(drawableID);
    }

}
