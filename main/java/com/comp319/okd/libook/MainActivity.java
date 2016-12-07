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

    // Favorites
    int fav1ID, fav2ID, fav3ID, fav4ID, fav5ID;
    int numFavs = 0;
    TextView noFavoriteTableTextView = null;
    ImageView selectedFavoriteBar = null;

    boolean parseEnabled = false;

    // Shake-detection variables
    private SensorManager sensorManager;
    private float mAccel = 0; // acceleration apart from gravity
    private float mAccelCurrent = 0; // current acceleration including gravity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Drawable lib, libLand;
        // Display different background image for portrait and landscape
        if(android.os.Build.VERSION.SDK_INT > 20) {
            lib = getResources().getDrawable(R.drawable.lib, null);
            libLand = getResources().getDrawable(R.drawable.libland, null);
        } else {
            lib = getResources().getDrawable(R.drawable.lib);
            libLand = getResources().getDrawable(R.drawable.libland);
        }
        if(lib!=null)lib.setAlpha(60);
        if(libLand!=null)libLand.setAlpha(40);

        // Create Parse service
        if(savedInstanceState!=null)parseEnabled = savedInstanceState.getBoolean("parseEnabled");
        if(!parseEnabled){
            try{
                // Enable Local Datastore.
                Parse.enableLocalDatastore(getApplicationContext());
            }catch(Exception e){
                // Do nothing, Datastore already enabled.
            }
            // Register with unique database identifier
            ParseObject.registerSubclass(Table.class);
            Parse.initialize(getApplicationContext(), "2OKEog9xbQw5hTdSeOjODcXrdYKaJGONHy2wbYsq",
                    "3cIeMoeg6u0YDQKSELhZEUMHIckPlDl9USr6VlDZ");
            parseEnabled = true;
        }

    }

    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Get saved favorites data (if there are any)
        fav1ID = sharedPreferences.getInt(getString(R.string.fav_1),-1);
        fav2ID = sharedPreferences.getInt(getString(R.string.fav_2),-1);
        fav3ID = sharedPreferences.getInt(getString(R.string.fav_3),-1);
        fav4ID = sharedPreferences.getInt(getString(R.string.fav_4),-1);
        fav5ID = sharedPreferences.getInt(getString(R.string.fav_5),-1);

        if(fav1ID+fav2ID+fav3ID+fav4ID+fav5ID == -5){
            // There are no favorites
            LinearLayout favoritesLayout = (LinearLayout) findViewById(R.id.favlay);
            noFavoriteTableTextView = new TextView(getApplicationContext());
            noFavoriteTableTextView.setText(R.string.nofav);
            noFavoriteTableTextView.setTextColor(Color.BLACK);
            noFavoriteTableTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            favoritesLayout.addView(noFavoriteTableTextView,2);
        }else{
            // Display favorite table data
            int floor,x,y, modX = 10000, modY = 100;

            if(fav1ID!=-1){
                numFavs = 1;
                TextView fav1text = (TextView) findViewById(R.id.fav1text);
                ImageView fav1img = (ImageView) findViewById(R.id.fav1img);
                fav1img.setImageResource(R.drawable.table);
                floor = fav1ID / modX - 1;
                x = (fav1ID % modX) / modY;
                y = (fav1ID % modY);
                fav1text.setText("Floor " + floor + "\n X: " + x + " Y: " + y);
            }
            if(fav2ID!=-1){
                numFavs = 2;
                TextView fav2text = (TextView) findViewById(R.id.fav2text);
                ImageView fav2img = (ImageView) findViewById(R.id.fav2img);
                fav2img.setImageResource(R.drawable.table);
                floor = fav2ID / modX - 1;
                x = (fav2ID % modX) / modY;
                y = (fav2ID % modY);
                fav2text.setText("Floor " + floor + "\n X: " + x + " Y: " + y);
            }
            if(fav3ID!=-1){
                numFavs = 3;
                TextView fav3text = (TextView) findViewById(R.id.fav3text);
                ImageView fav3img = (ImageView) findViewById(R.id.fav3img);
                fav3img.setImageResource(R.drawable.table);
                floor = fav3ID / modX - 1;
                x = (fav3ID % modX) / modY;
                y = (fav3ID % modY);
                fav3text.setText("Floor " + floor + "\n X: " + x + " Y: " + y);
            }
            if(fav4ID!=-1){
                numFavs = 4;
                TextView fav4text = (TextView) findViewById(R.id.fav4text);
                ImageView fav4img = (ImageView) findViewById(R.id.fav4img);
                fav4img.setImageResource(R.drawable.table);
                floor = fav4ID / modX - 1;
                x = (fav4ID % modX) / modY;
                y = (fav4ID % modY);
                fav4text.setText("Floor " + floor + "\n X: " + x + " Y: " + y);
            }
            if(fav5ID!=-1){
                numFavs = 5;
                TextView fav5text = (TextView) findViewById(R.id.fav5text);
                ImageView fav5img = (ImageView) findViewById(R.id.fav5img);
                fav5img.setImageResource(R.drawable.table);
                floor = fav5ID / modX - 1;
                x = (fav5ID % modX) / modY;
                y = (fav5ID % modY);
                fav5text.setText("Floor " + floor + "\n X: " + x + " Y: " + y);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        //Store the current state of the app
        savedInstanceState.putBoolean("parseEnabled", parseEnabled);
    }

    // Booking event
    public void bookMessage(View view){
        Intent intent = new Intent(this,BookActivity.class);
        startActivity(intent);
    }

    // Browsing event
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
        // Device is shaken
        if(se.sensor==null)return;
        if(se.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            float mAccelLast = mAccelCurrent; // last acceleration including gravity
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            // If acceleration is high enough, pick a random favorite table
            if(mAccel>10){
                highlightRandomTable();
                mAccel = 0;
//                Toast.makeText(getApplicationContext(),"ACCELERATION",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Do nothing
    }

    // Pick a random table from favorites
    private void highlightRandomTable(){
        if(numFavs==0)return;
        int randomTable = new Random().nextInt(numFavs)+1;
        LinearLayout favoriteTableLayout = null;
        if(selectedFavoriteBar!=null){
            LinearLayout parent = (LinearLayout) selectedFavoriteBar.getParent();
            parent.removeView(selectedFavoriteBar);
        }
        // Add a black line under the chosen favorite table
        selectedFavoriteBar = new ImageView(getApplicationContext());
        selectedFavoriteBar.setLayoutParams(new LinearLayout.LayoutParams(70,10));
        selectedFavoriteBar.setBackgroundColor(Color.BLACK);
        switch (randomTable){
            case 1:
                favoriteTableLayout = (LinearLayout) findViewById(R.id.fav1lay);
                break;
            case 2:
                favoriteTableLayout = (LinearLayout) findViewById(R.id.fav2lay);
                break;
            case 3:
                favoriteTableLayout = (LinearLayout) findViewById(R.id.fav3lay);
                break;
            case 4:
                favoriteTableLayout = (LinearLayout) findViewById(R.id.fav4lay);
                break;
            case 5:
                favoriteTableLayout = (LinearLayout) findViewById(R.id.fav5lay);
                break;
        }
        if(favoriteTableLayout != null)favoriteTableLayout.addView(selectedFavoriteBar);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Continue listening to shake
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Don't listen to shake while paused
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onStop(){
        // Remove lingering layout elements
        if(noFavoriteTableTextView!=null){
            LinearLayout layout = (LinearLayout) noFavoriteTableTextView.getParent();
            layout.removeView(noFavoriteTableTextView);
            noFavoriteTableTextView=null;
        }
        if(selectedFavoriteBar!=null){
            LinearLayout parent = (LinearLayout) selectedFavoriteBar.getParent();
            parent.removeView(selectedFavoriteBar);
            selectedFavoriteBar=null;
        }
        super.onStop();
    }

}
