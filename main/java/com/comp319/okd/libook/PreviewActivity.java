package com.comp319.okd.libook;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;

import java.io.File;


public class PreviewActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.activity_preview);

        String filePath = getIntent().getStringExtra("img_path");
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        bitmap = rotateImageIfRequired(getApplicationContext(),bitmap,uri);

        ImageView image = (ImageView) findViewById(R.id.preview_img);
        image.setImageBitmap(bitmap);
    }

    /**
     * Rotate an image if required.
     * @param img
     * @param selectedImage
     * @return
     */
    private static Bitmap rotateImageIfRequired(Context context,Bitmap img, Uri selectedImage) {

        // Detect rotation
        int rotation=getRotation(context, selectedImage);
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

    /**
     * Get the rotation of the last image added.
     * @param context
     * @param selectedImage
     * @return
     */
    private static int getRotation(Context context,Uri selectedImage) {
        int rotation =0;
        ContentResolver content = context.getContentResolver();


        Cursor mediaCursor = content.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { "orientation", "date_added" },null, null,"date_added desc");

        if (mediaCursor != null && mediaCursor.getCount() !=0 ) {
            while(mediaCursor.moveToNext()){
                rotation = mediaCursor.getInt(0);
                break;
            }
        }
        if (mediaCursor != null) {
            mediaCursor.close();
        }
        return rotation;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        super.dispatchTouchEvent(motionEvent);
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            finish();
        }
        return true;
    }
}
