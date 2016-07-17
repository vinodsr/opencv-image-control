package com.example.opencv;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

    static final int REQUEST_OPEN_IMAGE = 1;

    String mCurrentPhotoPath;
    Bitmap mBitmap;
    ImageView mImageView;
    int touchCount = 0;
    Point tl; 
    Point br;
    boolean targetChose = false;
    ProgressDialog dlg;

    
    SeekBar hueBar, satBar, valBar;
    TextView hueText, satText, valText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageview);
        
        hueBar = (SeekBar) findViewById(R.id.huebar);
        satBar = (SeekBar) findViewById(R.id.satbar);
        valBar = (SeekBar) findViewById(R.id.valbar);
        hueText = (TextView) findViewById(R.id.texthue);
        satText = (TextView) findViewById(R.id.textsat);
        valText = (TextView) findViewById(R.id.textval);
        
        hueBar.setOnSeekBarChangeListener(seekBarChangeListener);
        satBar.setOnSeekBarChangeListener(seekBarChangeListener);
        valBar.setOnSeekBarChangeListener(seekBarChangeListener);
        dlg = new ProgressDialog(this);
        tl = new Point();
        br = new Point();
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    

    OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

     @Override
     public void onProgressChanged(SeekBar seekBar, int progress,
       boolean fromUser) {
      // TODO Auto-generated method stub

     }

     @Override
     public void onStartTrackingTouch(SeekBar seekBar) {
      // TODO Auto-generated method stub

     }

     @Override
     public void onStopTrackingTouch(SeekBar seekBar) {
      loadBitmapHSV();
     }

    };
    

    private void loadBitmapHSV() {
     if (mBitmap != null) {

      int progressHue = hueBar.getProgress() - 256;
      int progressSat = satBar.getProgress();
      int progressVal = valBar.getProgress();

      /*
       * Hue (0 .. 360) Saturation (0...1) Value (0...1)
       */

      float hue = (float) progressHue * 360 / 256;
      float sat = (float) progressSat ;
      float val = (float) progressVal ;

     hueText.setText("Hue: " + String.valueOf(hue));
      satText.setText("Saturation: " + String.valueOf(sat));
      valText.setText("Value: " + String.valueOf(val));
     Drawable d = Drawable.createFromPath(mCurrentPhotoPath);
     d.setColorFilter(Color.rgb(20, 255, 20), Mode.ADD);
    // mImageView.setImageDrawable(d);
    //  mImageView.setImageBitmap(updateHSV(mBitmap, hue, sat, val));
     
     Mat src = new Mat();
     Mat hsv = new Mat();
     Bitmap targetBitmap = Bitmap.createBitmap(mBitmap);
     Utils.bitmapToMat(targetBitmap, src);
     Imgproc.cvtColor(src, hsv,Imgproc.COLOR_RGB2HSV );   
     List<Mat> hsv_channel = new ArrayList<Mat>();
	Core.split(hsv, hsv_channel);
	float[] hsvarray = {0,0,0};
	Color.RGBToHSV(0, 96, 169, hsvarray);
     // Imgproc.equalizeHist(hsv_channel.get(0), hsv_channel.get(0));
	Log.d("TEST", hsvarray[0] + " , " + hsvarray[1] + " , " + hsvarray[2] * 100 + " , " );
     hsv_channel.get(0).setTo(new Scalar(hsvarray[0]));
     hsv_channel.get(1).setTo(new Scalar(hsvarray[1] * 100));
      hsv_channel.get(2).setTo(new Scalar(hsvarray[2] * 100));
     Core.merge(hsv_channel, hsv);
     Imgproc.cvtColor(hsv, src,Imgproc.COLOR_HSV2RGB );     
     
     Utils.matToBitmap(src, targetBitmap);
     mImageView.setImageBitmap(targetBitmap);
     //return hsv_channel.get(0);
     

     }
    }

    private Bitmap updateHSV(Bitmap src, float settingHue, float settingSat,
      float settingVal) {

     int w = src.getWidth();
     int h = src.getHeight();
     int[] mapSrcColor = new int[w * h];
     int[] mapDestColor = new int[w * h];

     float[] pixelHSV = new float[3];

     src.getPixels(mapSrcColor, 0, w, 0, 0, w, h);

     int index = 0;
     for (int y = 0; y < h; ++y) {
      for (int x = 0; x < w; ++x) {

       // Convert from Color to HSV
       Color.colorToHSV(mapSrcColor[index], pixelHSV);

       // Adjust HSV
       pixelHSV[0] = pixelHSV[0] + settingHue;
       if (pixelHSV[0] < 0.0f) {
        pixelHSV[0] = 0.0f;
       } else if (pixelHSV[0] > 360.0f) {
        pixelHSV[0] = 360.0f;
       }

       pixelHSV[1] = pixelHSV[1] + settingSat;
       if (pixelHSV[1] < 0.0f) {
        pixelHSV[1] = 0.0f;
       } else if (pixelHSV[1] > 1.0f) {
        pixelHSV[1] = 1.0f;
       }

       pixelHSV[2] = pixelHSV[2] + settingVal;
       if (pixelHSV[2] < 0.0f) {
        pixelHSV[2] = 0.0f;
       } else if (pixelHSV[2] > 1.0f) {
        pixelHSV[2] = 1.0f;
       }

       // Convert back from HSV to Color
       mapDestColor[index] = Color.HSVToColor(pixelHSV);

       index++;
      }
     }

     return Bitmap.createBitmap(mapDestColor, w, h, Config.ARGB_8888);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void setPic() {
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        mBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(mBitmap);
        
        hueBar.setProgress(256);
        satBar.setProgress(256);
        valBar.setProgress(256);
        loadBitmapHSV();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_OPEN_IMAGE:
                if (resultCode == RESULT_OK) {
                	
                	 Uri selectedImageUri = data.getData();
                	 mCurrentPhotoPath = getPath(selectedImageUri);
                  
                    setPic();
                }
                break;
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
            // just some safety built in 
            if( uri == null ) {
                // TODO perform some logging or show user feedback
                return null;
            }
            // try to retrieve the image from the media store first
            // this will only work for images selected from gallery
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            if( cursor != null ){
                int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            // this is our fallback here
            return uri.getPath();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_open_img:
                Intent getPictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getPictureIntent.setType("image/*");
                Intent pickPictureIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent chooserIntent = Intent.createChooser(getPictureIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {
                        pickPictureIntent
                });
                startActivityForResult(chooserIntent, REQUEST_OPEN_IMAGE);
                return true;
            case R.id.action_choose_target:
                if (mCurrentPhotoPath != null)
                    targetChose = false;
                    mImageView.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                        	  if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        		  tl.x = event.getX();
                                  tl.y = event.getY();
                        	  } else   if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        		  br.x = event.getX();
                                  br.y = event.getY();
                        		   updateRectArea();
                        	  } else 
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                            	  br.x = event.getX();
                                  br.y = event.getY();

                                  updateRectArea();

                                  targetChose = true;
                                  touchCount = 0;
                                  mImageView.setOnTouchListener(null);
                                if (touchCount == 0) {
                                   
                                    touchCount++;
                                }
                                else  {
                                  

                                }
                            }

                            return true;
                        }

						private void updateRectArea() {
							Paint rectPaint = new Paint();
							rectPaint.setARGB(255, 255, 0, 0);
							rectPaint.setStyle(Paint.Style.STROKE);
							rectPaint.setStrokeWidth(3);
							Bitmap tmpBm = Bitmap.createBitmap(mBitmap.getWidth(),
							        mBitmap.getHeight(), Bitmap.Config.RGB_565);
							Canvas tmpCanvas = new Canvas(tmpBm);

							tmpCanvas.drawBitmap(mBitmap, 0, 0, null);
							tmpCanvas.drawRect(new RectF((float) tl.x, (float) tl.y, (float) br.x, (float) br.y),
							        rectPaint);
							mImageView.setImageDrawable(new BitmapDrawable(getResources(), tmpBm));
						}
                    });

                return true;
            case R.id.action_cut_image:
                if (mCurrentPhotoPath != null && targetChose) {
                    new ProcessImageTask().execute();
                    targetChose = false;
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ProcessImageTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dlg.setMessage("Processing Image...");
            dlg.setCancelable(false);
            dlg.setIndeterminate(true);
            dlg.show();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            Mat img = Imgcodecs.imread(mCurrentPhotoPath);
            Mat background = new Mat(img.size(), CvType.CV_8UC3,
                    new Scalar(255, 255, 255));
            Mat firstMask = new Mat();
            Mat bgModel = new Mat();
            Mat fgModel = new Mat();
            Mat mask;
            Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
            Mat dst = new Mat();
            Rect rect = new Rect(tl, br);

            Imgproc.grabCut(img, firstMask, rect, bgModel, fgModel,
                    5, Imgproc.GC_INIT_WITH_RECT);
            Core.compare(firstMask, source, firstMask, Core.CMP_EQ);

            Mat foreground = new Mat(img.size(), CvType.CV_8UC3,
                    new Scalar(255, 255, 255));
            img.copyTo(foreground, firstMask);

            Scalar color = new Scalar(255, 0, 0, 255);
            Imgproc.rectangle(img, tl, br, color);

            Mat tmp = new Mat();
            Imgproc.resize(background, tmp, img.size());
            background = tmp;
            mask = new Mat(foreground.size(), CvType.CV_8UC1,
                    new Scalar(255, 255, 255));

            Imgproc.cvtColor(foreground, mask, Imgproc.COLOR_BGR2GRAY);
            Imgproc.threshold(mask, mask, 254, 255, Imgproc.THRESH_BINARY_INV);
            System.out.println();
            Mat vals = new Mat(1, 1, CvType.CV_8UC3, new Scalar(0.0));
            background.copyTo(dst);

            background.setTo(vals, mask);

            Core.add(background, foreground, dst, mask);

            firstMask.release();
            source.release();
            bgModel.release();
            fgModel.release();
            vals.release();

            Imgcodecs.imwrite(mCurrentPhotoPath + "_mod.png", dst);

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            Bitmap jpg = BitmapFactory
                    .decodeFile(mCurrentPhotoPath + "_mod.png");

            mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mImageView.setAdjustViewBounds(true);
            mImageView.setPadding(2, 2, 2, 2);
            mImageView.setImageBitmap(jpg);
            mImageView.invalidate();

            dlg.dismiss();
        }
    }
}