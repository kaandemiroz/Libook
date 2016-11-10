package com.comp319.okd.libook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


public class BookActivity extends Activity implements AdapterView.OnItemSelectedListener{



    Spinner floorSpinner;

    Integer[] floors = {-1, 0, 1, 2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        Drawable lib, libland;
        if(android.os.Build.VERSION.SDK_INT > 20) lib = getResources().getDrawable(R.drawable.lib,null);
        else lib = getResources().getDrawable(R.drawable.lib);
        if(lib!=null)lib.setAlpha(60);
        if(android.os.Build.VERSION.SDK_INT > 20) libland = getResources().getDrawable(R.drawable.libland,null);
        else libland = getResources().getDrawable(R.drawable.libland);
        if(libland!=null)libland.setAlpha(40);

        floorSpinner = (Spinner) findViewById(R.id.bookFloorSpinner);

        ArrayAdapter<Integer> fAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, floors);
        floorSpinner.setAdapter(fAdapter);
        floorSpinner.setSelection(1);

        floorSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LibraryBookView libraryBookView = (LibraryBookView) findViewById(R.id.libview);
        libraryBookView.setFloor(position - 1);
    }

    @Override
    /* */
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }

    public void showOptions(View view){
        LibraryBookView libraryBookView = (LibraryBookView) findViewById(R.id.libview);
        int x = libraryBookView.getClickedX();
        int y = libraryBookView.getClickedY();
        if (x == -1 || y == -1) {
            Toast.makeText(getApplicationContext(), getString(R.string.invCoords), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, BookOptionsActivity.class);
        intent.putExtra("floor",(int)floorSpinner.getSelectedItem());
        intent.putExtra("x",x);
        intent.putExtra("y",y);
        startActivityForResult(intent, 4321);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 4321){
            if(resultCode==5312){
                int floor = getIntent().getIntExtra("floor", (int)floorSpinner.getSelectedItem());
                addToFavs(floor);
            }
        }
    }

    public void addToFavs(int floor) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        LibraryBookView libraryBookView = (LibraryBookView) findViewById(R.id.libview);
        int tableID = -1;
        int clickedX = libraryBookView.getClickedX();
        int clickedY = libraryBookView.getClickedY();

        if (clickedX != -1 && clickedY != -1)
            tableID = (floor+1) * 10000 + clickedX * 100 + clickedY;
        if (sharedPreferences.getInt(getString(R.string.fav_1), -1) == -1)
            editor.putInt(getString(R.string.fav_1), tableID);
        else if (sharedPreferences.getInt(getString(R.string.fav_2), -1) == -1)
            editor.putInt(getString(R.string.fav_2), tableID);
        else if (sharedPreferences.getInt(getString(R.string.fav_3), -1) == -1)
            editor.putInt(getString(R.string.fav_3), tableID);
        else if (sharedPreferences.getInt(getString(R.string.fav_4), -1) == -1)
            editor.putInt(getString(R.string.fav_4), tableID);
        else if (sharedPreferences.getInt(getString(R.string.fav_5), -1) == -1)
            editor.putInt(getString(R.string.fav_5), tableID);
        editor.apply();
    }
}
