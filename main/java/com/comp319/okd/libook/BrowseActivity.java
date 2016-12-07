package com.comp319.okd.libook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class BrowseActivity extends Activity implements AdapterView.OnItemSelectedListener{

    Spinner floorSpinner;
    Integer[] floors = {-1,0,1,2};

    ArrayList<Table> tables = new ArrayList<>();

    public int tableIndex = -1;
    public File photoFile = null;
    public File audioFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        Drawable lib, libLand;
        // Display different background image for portrait and landscape
        if(android.os.Build.VERSION.SDK_INT > 20){
            lib = getResources().getDrawable(R.drawable.lib,null);
            libLand = getResources().getDrawable(R.drawable.libland,null);
        } else {
            lib = getResources().getDrawable(R.drawable.lib);
            libLand = getResources().getDrawable(R.drawable.libland);
        }
        if(lib!=null)lib.setAlpha(60);
        if(libLand!=null)libLand.setAlpha(40);

        // Create spinners to choose floor
        floorSpinner = (Spinner) findViewById(R.id.browseFloorSpinner);
        ArrayAdapter<Integer> fAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,floors);
        floorSpinner.setAdapter(fAdapter);
        floorSpinner.setSelection(1);
        floorSpinner.setOnItemSelectedListener(this);

        // Check Parse database for free tables
        ParseQuery<Table> query = new ParseQuery<>("Tables");
        query.findInBackground(new FindCallback<Table>() {
            @Override
            public void done(List<Table> list, ParseException e) {
                if(e!=null){
                    Toast.makeText(BrowseActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                }else{
                    for(Table table : list){
                        tables.add(table);
                    }
                    ((LibraryBrowseView) findViewById(R.id.libview2)).init(tables);
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Change current floor
        LibraryBrowseView libraryBrowseView = (LibraryBrowseView) findViewById(R.id.libview2);
        libraryBrowseView.setFloor(position-1);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // Display additional media messages for the table, plus the option to sit
    public void showOptions(){
        Intent intent = new Intent(this,BrowseOptionsActivity.class);
        if(photoFile!=null)intent.putExtra("photoPath",photoFile.getAbsolutePath());
        if(audioFile!=null)intent.putExtra("audioPath",audioFile.getAbsolutePath());
        startActivityForResult(intent, 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Sitting on the table, delete entry
        if(requestCode == 1234){
            if(resultCode == RESULT_OK){
                deleteTable();
            }
        }
    }

    // Remove table from the list
    public void deleteTable(){
        if(tableIndex==-1)return;
        Table table = tables.get(tableIndex);
        table.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                //Do nothing
            }
        });
        tables.remove(table);
        ((LibraryBrowseView) findViewById(R.id.libview2)).init(tables);
    }
}
