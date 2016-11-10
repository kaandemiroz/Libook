package com.comp319.okd.libook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;

import java.util.Random;


public class MainActivity extends Activity implements SensorEventListener {

    int fav1ID;
    int fav2ID;
    int fav3ID;
    int fav4ID;
    int fav5ID;
    int numFavs = 0;
    TextView tv = null;
    ImageView bar = null;
    boolean parseEnabled = false;
    private SensorManager sensorManager;
    private float mAccel = 0; // acceleration apart from gravity
    private float mAccelCurrent = 0; // current acceleration including gravity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Drawable lib, libland;
        if(android.os.Build.VERSION.SDK_INT > 20) lib = getResources().getDrawable(R.drawable.lib,null);
        else lib = getResources().getDrawable(R.drawable.lib);
        if(lib!=null)lib.setAlpha(60);
        if(android.os.Build.VERSION.SDK_INT > 20) libland = getResources().getDrawable(R.drawable.libland,null);
        else libland = getResources().getDrawable(R.drawable.libland);
        if(libland!=null)libland.setAlpha(40);

        if(savedInstanceState!=null)parseEnabled = savedInstanceState.getBoolean("parseEnabled");
        if(!parseEnabled){
            // Enable Local Datastore.
            try{
                Parse.enableLocalDatastore(getApplicationContext());
            }catch(Exception e){
                // Do nothing, Datastore already enabled.
            }
            ParseObject.registerSubclass(Table.class);
            Parse.initialize(getApplicationContext(), "2OKEog9xbQw5hTdSeOjODcXrdYKaJGONHy2wbYsq", "3cIeMoeg6u0YDQKSELhZEUMHIckPlDl9USr6VlDZ");
            parseEnabled = true;
        }

    }

    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        fav1ID = sharedPreferences.getInt(getString(R.string.fav_1),-1);
        fav2ID = sharedPreferences.getInt(getString(R.string.fav_2),-1);
        fav3ID = sharedPreferences.getInt(getString(R.string.fav_3),-1);
        fav4ID = sharedPreferences.getInt(getString(R.string.fav_4),-1);
        fav5ID = sharedPreferences.getInt(getString(R.string.fav_5),-1);
        if(fav1ID+fav2ID+fav3ID+fav4ID+fav5ID == -5){
            LinearLayout favlay = (LinearLayout) findViewById(R.id.favlay);
            tv = new TextView(getApplicationContext());
            tv.setText(R.string.nofav);
            tv.setTextColor(Color.BLACK);
            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            favlay.addView(tv,2);
        }else{
            int f,x,y;
            if(fav1ID!=-1){
                numFavs = 1;
                TextView fav1text = (TextView) findViewById(R.id.fav1text);
                ImageView fav1img = (ImageView) findViewById(R.id.fav1img);
                fav1img.setImageResource(R.drawable.table);
                f = fav1ID / 10000 - 1;
                x = (fav1ID%10000) / 100;
                y = (fav1ID%100);
                fav1text.setText("Floor " + f + "\n X: " + x + " Y: " + y);
            }
            if(fav2ID!=-1){
                numFavs = 2;
                TextView fav2text = (TextView) findViewById(R.id.fav2text);
                ImageView fav2img = (ImageView) findViewById(R.id.fav2img);
                fav2img.setImageResource(R.drawable.table);
                f = fav2ID / 10000 - 1;
                x = (fav2ID%10000) / 100;
                y = (fav2ID%100);
                fav2text.setText("Floor " + f + "\n X: " + x + " Y: " + y);
            }
            if(fav3ID!=-1){
                numFavs = 3;
                TextView fav3text = (TextView) findViewById(R.id.fav3text);
                ImageView fav3img = (ImageView) findViewById(R.id.fav3img);
                fav3img.setImageResource(R.drawable.table);
                f = fav3ID / 10000 - 1;
                x = (fav3ID%10000) / 100;
                y = (fav3ID%100);
                fav3text.setText("Floor " + f + "\n X: " + x + " Y: " + y);
            }
            if(fav4ID!=-1){
                numFavs = 4;
                TextView fav4text = (TextView) findViewById(R.id.fav4text);
                ImageView fav4img = (ImageView) findViewById(R.id.fav4img);
                fav4img.setImageResource(R.drawable.table);
                f = fav4ID / 10000 - 1;
                x = (fav4ID%10000) / 100;
                y = (fav4ID%100);
                fav4text.setText("Floor " + f + "\n X: " + x + " Y: " + y);
            }
            if(fav5ID!=-1){
                numFavs = 5;
                TextView fav5text = (TextView) findViewById(R.id.fav5text);
                ImageView fav5img = (ImageView) findViewById(R.id.fav5img);
                fav5img.setImageResource(R.drawable.table);
                f = fav5ID / 10000 - 1;
                x = (fav5ID%10000) / 100;
                y = (fav5ID%100);
                fav5text.setText("Floor " + f + "\n X: " + x + " Y: " + y);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        //Store the current state of the app
        savedInstanceState.putBoolean("parseEnabled", parseEnabled);
    }

    public void bookMessage(View view){
        Intent intent = new Intent(this,BookActivity.class);
        startActivity(intent);
    }

    public void browseMessage(View view){
        Intent intent = new Intent(this,BrowseActivity.class);
        startActivity(intent);
    }

    /**
     * Called when sensor values have changed.
     * <p>See {@link SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link SensorEvent SensorEvent}.
     * <p/>
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param se the {@link SensorEvent SensorEvent}.
     */

    @Override
    public void onSensorChanged(SensorEvent se) {
        if(se.sensor==null)return;
        if(se.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            float mAccelLast = mAccelCurrent; // last acceleration including gravity
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            if(mAccel>10){
                highlightRandomTable();
                mAccel = 0;
                Toast.makeText(getApplicationContext(),"ACCELERATION",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Do nothing
    }

    private void highlightRandomTable(){
        if(numFavs==0)return;
        int randomTable = new Random().nextInt(numFavs)+1;
        LinearLayout layout = null;
        if(bar!=null){
            LinearLayout parent = (LinearLayout) bar.getParent();
            parent.removeView(bar);
        }
        bar = new ImageView(getApplicationContext());
        bar.setLayoutParams(new LinearLayout.LayoutParams(70,10));
        bar.setBackgroundColor(Color.BLACK);
        switch (randomTable){
            case 1:
                layout = (LinearLayout) findViewById(R.id.fav1lay);
                break;
            case 2:
                layout = (LinearLayout) findViewById(R.id.fav2lay);
                break;
            case 3:
                layout = (LinearLayout) findViewById(R.id.fav3lay);
                break;
            case 4:
                layout = (LinearLayout) findViewById(R.id.fav4lay);
                break;
            case 5:
                layout = (LinearLayout) findViewById(R.id.fav5lay);
                break;
        }
        if(layout != null)layout.addView(bar);
    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onStop(){
        if(tv!=null){
            LinearLayout layout = (LinearLayout) tv.getParent();
            layout.removeView(tv);
            tv=null;
        }
        if(bar!=null){
            LinearLayout parent = (LinearLayout) bar.getParent();
            parent.removeView(bar);
            bar=null;
        }
        super.onStop();
    }

}
