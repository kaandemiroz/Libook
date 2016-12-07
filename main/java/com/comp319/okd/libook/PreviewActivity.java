package com.comp319.okd.libook;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;

/**
 * This activity is used to preview an image saved/browsed through
 * one of the activities in the application, in a separate window.
 */
public class PreviewActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a window with no title and no background
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.activity_preview);

        // Display the taken photo
        String filePath = getIntent().getStringExtra("img_path");
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        bitmap = rotateImageIfRequired(getApplicationContext(),bitmap);

        ImageView image = (ImageView) findViewById(R.id.preview_img);
        image.setImageBitmap(bitmap);
    }

    // Rotate an image if required.
    private static Bitmap rotateImageIfRequired(Context context, Bitmap img) {

        // Detect rotation
        int rotation=getRotation(context);
        if(rotation!=0){
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        }else{
            return img;
        }
    }

    // Get the rotation of the last image added.
    private static int getRotation(Context context) {
        int rotation =0;
        ContentResolver content = context.getContentResolver();

        Cursor mediaCursor = content.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { "orientation", "date_added" },null, null,"date_added desc");

        if (mediaCursor != null && mediaCursor.getCount() !=0 ) {
            if(mediaCursor.moveToNext()){
                rotation = mediaCursor.getInt(0);
            }
        }
        if (mediaCursor != null) {
            mediaCursor.close();
        }
        return rotation;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        // Close preview when touched
        super.dispatchTouchEvent(motionEvent);
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            finish();
        }
        return true;
    }
}
