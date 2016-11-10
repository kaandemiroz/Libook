package com.comp319.okd.libook;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;


public class BrowseOptionsActivity extends Activity {

    private MediaPlayer mediaPlayer = null;
    protected String photoPath = null;
    protected String audioPath = null;
    private boolean playing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_browse_options);

        photoPath = getIntent().getStringExtra("photoPath");
        audioPath = getIntent().getStringExtra("audioPath");

        if(photoPath!=null) findViewById(R.id.photo_layout).setVisibility(View.VISIBLE);
        if(audioPath!=null) findViewById(R.id.audio_layout).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void display(View view){
        if(photoPath==null) return;
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra("img_path", photoPath);
        startActivity(intent);
    }

    public void play(View view){
        if(audioPath==null) return;
        if(!playing){
            mediaPlayer = new MediaPlayer();
            try{
                FileInputStream fileInputStream = new FileInputStream(audioPath);
                mediaPlayer.setDataSource(fileInputStream.getFD());
                mediaPlayer.prepare();
                mediaPlayer.start();
                playing = true;
                ((ImageButton)view).setImageResource(R.drawable.stop);
                view.setContentDescription(getString(R.string.stopRecStr));
            }catch(IOException e){
                Toast.makeText(getApplicationContext(), "play prepare() failed", Toast.LENGTH_LONG).show();
            }
        }else{
            mediaPlayer.release();
            mediaPlayer = null;
            playing = false;
            ((ImageButton)view).setImageResource(R.drawable.play);
            view.setContentDescription(getString(R.string.playRecStr));
        }
    }

    public void sit(View view){
        setResult(RESULT_OK);
        finish();
    }
}
