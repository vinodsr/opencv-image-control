package com.example.opencv;

import java.io.IOException;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class Helper {
    
private Context context;
private Bitmap bitmap_result;
private Bitmap bitmap_cut;


public Bitmap getBitmap_result() {
	return bitmap_result;
}

public Bitmap getBitmap_cut() {
	return bitmap_cut;
}

public Helper(Context cont) {
	this.context = cont;
}

public Context getContext() {
	return context;
}
	public boolean cutImage(Bitmap  bitmap , int x1,int y1 , int x2, int y2) {
		Matrix matrix = new Matrix();
	  //  float scale = Math.max(((float) this.width) / ((float) b.getWidth()), ((float) this.height) / ((float) b.getHeight()));
	    matrix.postScale(1, 1);
	    Bitmap bitmap_copy = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	    bitmap_result = bitmap_copy.copy(bitmap_copy.getConfig(), true);
	    bitmap_cut= bitmap_copy.copy(bitmap_copy.getConfig(), true);
		Mat bgdModel = new Mat();
	    Mat fgdModel = new Mat();
	    Mat mat = new Mat(1, 1, 0, new Scalar(3.0d));
	    Mat img = new Mat();
	    mat = new Mat(img.size(), CvType.CV_8UC3, new Scalar(255.0d, 255.0d, 255.0d));
	    Utils.bitmapToMat(bitmap_copy, img);
	    
	    Imgproc.cvtColor(img, mat, 1);
	    Mat mask = new Mat(img.size(), 0, new Scalar(0.0d));
	    Mat img_mask = new Mat();
	    Mat imgC1_mask = new Mat();
	    Utils.bitmapToMat(bitmap, img_mask);
	    Imgproc.cvtColor(img_mask, imgC1_mask, 10);
	    int num_fgd_pix = 0;
	    for (int y = y1; y < y2; y++) {
	        for (int x = x1; x < x2; x++) {
	            if (imgC1_mask.get(y, x)[0] > 200.0d) {
	                mask.put(y, x, 3.0d);
	                num_fgd_pix++;
	            }
	        }
	    }
	    if (num_fgd_pix < 200) {
	        return false;
	    }
	    Mat mask_inv;
	    Point p1 = new Point();
	    Point p2 = new Point();
	    p1.x = (double) x1;
	    p1.y = (double) y1;
	    p2.x = (double) x2;
	    p2.y = (double) y2; 
	    Imgproc.grabCut(mat, mask, new Rect(p1, p2), bgdModel, fgdModel, 1, 1);
	   // Core.compare(mask, mat, mask, 0);
	    mat = new Mat(img.size(), CvType.CV_8UC4, new Scalar(0.0d, 0.0d, 0.0d, 0.0d));
	    img.copyTo(mat, mask);
	    Utils.matToBitmap(mat, bitmap_result);
	    Mat imgbgd = null;
	    Mat imgbgd2 = null;
	    try {
	        Mat imgtmp = new Mat();
	        imgtmp = Utils.loadResource(getContext(), R.drawable.images);
	        mat = new Mat(img.size(), CvType.CV_8UC3, new Scalar(255.0d, 255.0d, 255.0d, 255.0d));
	        try {
	            Imgproc.cvtColor(imgtmp, mat, 0);
	            imgtmp.release();
	            imgbgd = mat;
	        } catch (Exception e) {
	            imgbgd = mat;
	        }
	    } catch (IOException e2) {
	    }
	    if (imgbgd != null) {
	        Imgproc.resize(imgbgd, imgbgd, img.size());
	        mask_inv = new Mat(img.size(), 0, new Scalar(0.0d));
	        Imgproc.threshold(mask, mask_inv, 25.0d, 255.0d, 1);
	        mat = new Mat(img.size(), CvType.CV_8UC4, new Scalar(0.0d, 0.0d, 0.0d, 0.0d));
	        imgbgd.copyTo(mat, mask_inv);
	        Core.add(mat, mat, img);
	        Utils.matToBitmap(img, bitmap_cut);
	    } else {
	        Utils.matToBitmap(mat, bitmap_cut);
	        mask_inv = null;
	    }
	    mask.release();
	    mat.release();
	    bgdModel.release();
	    fgdModel.release();
	    img.release();
	    mat.release();
	    mat.release();
	    if (imgbgd != null) {
	        imgbgd.release();
	        //imgbgd2.release();
	        mask_inv.release();
	    }
	    return true;
	}

}
