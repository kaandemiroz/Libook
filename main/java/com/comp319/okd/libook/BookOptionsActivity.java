package com.comp319.okd.libook;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class BookOptionsActivity extends FragmentActivity  implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    // Location must be inside Koc University to book a table
    // To test, long = 29.07, lat = 41.20 can be used in emulator
    private static double kuLongitudeMin = 29.065349;
    private static double kuLongitudeMax = 29.080198;
    private static double kuLatitudeMin = 41.196161;
    private static double kuLatitudeMax = 41.210562;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri = null;

    private MediaRecorder mediaRecorder = null;
    private MediaPlayer mediaPlayer = null;
    private boolean recording = false, recorded = false;
    private boolean playing = false;
    private boolean captured = false;
    protected String outputFile = null;
    protected int floor = -2;
    protected int x = -1;
    protected int y = -1;

    Spinner hourSpinner;
    Spinner minuteSpinner;

    Integer[] hours = {0, 1, 2, 3, 4, 5};
    Integer[] minutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a window with no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_book_options);
        buildGoogleApiClient();
        if(!checkCameraHardware(getApplicationContext())) findViewById(R.id.camera_layout).setVisibility(View.GONE);

        // Create spinners to choose time data
        hourSpinner = (Spinner) this.findViewById(R.id.hourSpinner);
        minuteSpinner = (Spinner) this.findViewById(R.id.minuteSpinner);

        minutes = new Integer[13];
        for (int i = 0; i < 13; i++) {
            minutes[i] = i * 5;
        }

        floor = getIntent().getIntExtra("floor",-2);
        x = getIntent().getIntExtra("x",-1);
        y = getIntent().getIntExtra("y",-1);

        ArrayAdapter<Integer> hAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        ArrayAdapter<Integer> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, minutes);
        hourSpinner.setAdapter(hAdapter);
        minuteSpinner.setAdapter(mAdapter);
        mGoogleApiClient.connect();
    }

    // Complete transaction, uploading all data to Parse
    public void completeBooking(View view) {

        // Location data
        getLocationUpdate();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        for(int i=0; mLastLocation == null; i++){
            getLocationUpdate();
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(i==10){
                Toast.makeText(getApplicationContext(), getString(R.string.noLocation),Toast.LENGTH_SHORT).show();
                return;
            }
        }
        double mLatitude = mLastLocation.getLatitude();
        double mLongitude = mLastLocation.getLongitude();
        if (mLatitude > kuLatitudeMax || mLatitude < kuLatitudeMin
                || mLongitude > kuLongitudeMax || mLongitude < kuLongitudeMin){
            Toast.makeText(getApplicationContext(), getString(R.string.outsideKU) + ": " + mLongitude + ", " + mLatitude, Toast.LENGTH_SHORT).show();
            return;
        }

        // Time data
        int hour = (int) hourSpinner.getSelectedItem();
        int minute = (int) minuteSpinner.getSelectedItem();
        if (hour == 0 && minute == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.invDuration),Toast.LENGTH_SHORT).show();
            return;
        }
        if (x == -1 || y == -1) {
            Toast.makeText(getApplicationContext(), getString(R.string.invCoords), Toast.LENGTH_SHORT).show();
            return;
        }

        Table table = new Table();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.HOUR, hour);
        date.add(Calendar.MINUTE, minute);
        Date expiresAt = date.getTime();
        date.add(Calendar.HOUR, 2);
        Toast.makeText(getApplicationContext(), getString(R.string.expDate) + date.getTime(), Toast.LENGTH_SHORT).show();

        // Media data
        File audio = null;
        File photo = null;
        if(outputFile!= null) audio = new File(outputFile);
        if(fileUri != null) photo = new File(fileUri.getPath());
        table.setFloor(floor);
        table.setX(x);
        table.setY(y);
        if(audio != null && audio.exists())table.setAudio(audio);
        if(photo != null && photo.exists())table.setPhoto(photo);
        table.setExpiresAt(expiresAt);

        // Upload to Parse
        final TextView uploadingText = (TextView) findViewById(R.id.uploading);
        uploadingText.setVisibility(View.VISIBLE);
        table.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                uploadingText.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void addToFavs(View view) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("floor",floor);
        setResult(5312);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Do nothing
    }

    // Capture a photo to attach
    public void capture(View view){
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    // Preview the captured photo
    public void display(View view){
        if(!captured)return;
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra("img_path",fileUri.getPath());
        startActivity(intent);
    }

    // Delete a captured photo
    public void deletePhoto(View view){
        if(!captured)return;
        if(new File(fileUri.getPath()).delete()){
            captured = false;
            fileUri = null;
            Toast.makeText(getApplicationContext(),getString(R.string.photo_del),Toast.LENGTH_SHORT).show();
        }else Toast.makeText(getApplicationContext(),getString(R.string.err), Toast.LENGTH_SHORT).show();
    }

    // Record an audio message
    public void record(View view){
        if(!recording){
            outputFile = getFilesDir().getAbsolutePath() + "/audio_message.3gpp";
            //outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_message.3gpp";
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(outputFile);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                recording = true;
                ((ImageButton)view).setImageResource(R.drawable.stop);
                view.setContentDescription(getString(R.string.stopRecStr));
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"record prepare() failed",Toast.LENGTH_LONG).show();
            }
        }else{
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            recording = false;
            recorded = true;
            ((ImageButton)view).setImageResource(R.drawable.rec);
            view.setContentDescription(getString(R.string.recordStr));
        }
    }

    // Preview the recorded audio message
    public void play(View view){
        if(!recorded)return;
        if(!playing){
            mediaPlayer = new MediaPlayer();
            try{
                mediaPlayer.setDataSource(outputFile);
                mediaPlayer.prepare();
                mediaPlayer.start();
                playing = true;
                ((ImageButton)view).setImageResource(R.drawable.stop);
                view.setContentDescription(getString(R.string.stopRecStr));
            }catch(IOException e){
                Toast.makeText(getApplicationContext(),"play prepare() failed",Toast.LENGTH_LONG).show();
            }
        }else{
            mediaPlayer.release();
            mediaPlayer = null;
            playing = false;
            ((ImageButton)view).setImageResource(R.drawable.play);
            view.setContentDescription(getString(R.string.playRecStr));
        }
    }

    // Delete the recorded audio message
    public void deleteAudio(View view){
        if(!recorded)return;
        if(new File(outputFile).delete()){
            recorded = false;
            outputFile = null;
            Toast.makeText(getApplicationContext(),getString(R.string.audio_del),Toast.LENGTH_SHORT).show();
        }else Toast.makeText(getApplicationContext(),getString(R.string.err), Toast.LENGTH_SHORT).show();
    }

    // Check if this device has a camera
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroy(){
        if(recorded) new File(outputFile).delete();
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Do nothing
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), getString(R.string.conSus), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        }
        if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    private void getLocationUpdate(){
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /* Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /* Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Libook");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                //Log.d("Libook", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        }else{
            return null;
        }

        return mediaFile;
    }

    /* Build Google API Client */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((BookOptionsActivity)getActivity()).onDialogDismissed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                if(data!=null)Toast.makeText(this, "Image saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
                captured = true;
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

}
