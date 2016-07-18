package com.example.opencv;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class Stage3 extends Activity implements OnClickListener {

    private TestView drawing;
     private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
     public static final int MEDIA_TYPE_IMAGE = 1;
     private static final String IMAGE_DIRECTORY_NAME = "demo pictures";
     private Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage3);

        //drawing = (SignatureViewModed) findViewById(R.id.myTest);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this); 
        findViewById(R.id.button6).setOnClickListener(this);

        drawing = (TestView) findViewById(R.id.testview);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

        switch(arg0.getId())
        {

        case R.id.button1:
        {
            drawing.clear();
            break;
        }

        case R.id.button2:
        {
            if(isDeviceSupportCamera())
                captureImage();
            else
                Toast.makeText(this, "No camera", Toast.LENGTH_SHORT).show();
            break;
        }

        case R.id.button3:
        {
            showCutImage();
            break;
        }
        
        case R.id.button4:
        {
        	applyFilter(Color.RED);
            break;
        }
        
        case R.id.button5:
        {
        	applyFilter(Color.YELLOW);
            break;
        }
        
        case R.id.button6:
        {
        	clearFilter();
            break;
        }

        }

    }
    
    private void applyFilter(int color){
    	Drawable d = drawing.getDrawable();
    	
    	
        ColorFilter colorFilter = ColorFilterGenerator.from(d).to(color);
        drawing.setColorFilter(colorFilter);
    	
    }
    private void clearFilter(){
    	
    	
        
        drawing.clearColorFilter();
    	
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private void captureImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri();

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

         mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data2) {

        Bitmap bitmap = null;

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {

            if (resultCode == RESULT_OK)
            {

                String filePath = fileUri.getPath();
                bitmap = decodeSampledBitmapFromPath(filePath, true);

                drawing.setBitmap(bitmap);

                drawing.setImageBitmap(bitmap);
            }

        }

            super.onActivityResult(requestCode, resultCode, data2);
        }

    public Bitmap decodeSampledBitmapFromPath(String path, boolean isFullScreen) {

        Bitmap bmp = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        if(isFullScreen)
            options.inSampleSize = calculateInSampleSize(options, getScreenWidth() , getScreenHeight());
        else
            options.inSampleSize = calculateInSampleSize(options, 200, 200);

        // Decode bitmap with inSampleSize set
        options.inMutable=true;
        options.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(path,options);
        

        return bmp;
        }

    public int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
             }
         }
         return inSampleSize;
        }

    @SuppressLint("NewApi")
    public int getScreenHeight()
    {
        Display display = ((WindowManager) this.getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay();

        int height = 0;

        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);
            height = size.y;

        } else {
            height = display.getHeight();

        }

        return height;
    }

    @SuppressLint("NewApi")
    public int getScreenWidth()
    {
        Display display = ((WindowManager) this.getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay();

        int width = 0;

        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);

            width = size.x;
        } else {

            width = display.getWidth();
        }

        return width;
    }

    public void showCutImage()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View prefView = View.inflate(this,R.layout.activity_main, null);

        ImageView image = (ImageView) prefView.findViewById(R.id.imageview);
        drawing.setImageBitmap(drawing.getImage2());

        builder.setView(prefView);

       // builder.show();

    }
}
