package com.example.opencv;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class TestView extends ImageView implements OnTouchListener {
    private Paint paint;
    public static List<Point> points;

    Bitmap bitmapMain;

    Context mContext;

    private int leftX = 0;
    private int rightX = 0;
    private int upY = 0;
    private int downY = 0;
	private Path path;

    public TestView(Context c) {
        super(c);

        mContext = c;
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[] { 10, 20 }, 0));
        paint.setStrokeWidth(5);
        paint.setColor(Color.WHITE);

        this.setOnTouchListener(this);
        points = new ArrayList<Point>();


    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.WHITE);

        this.setOnTouchListener(this);
        points = new ArrayList<Point>();

    }

    public void setBitmap(Bitmap bit)
    {
        bitmapMain = bit;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

       // canvas.drawBitmap(bitmap, 0, 0, null);

        path = new Path();
        boolean first = true;
        if(points.size() > 0 ) {
        for (int i = 0; i < points.size(); i += 2) {
            Point point = points.get(i);
            if (first) {
                first = false;
                path.moveTo(point.x, point.y);
            } else if (i < points.size() - 1) {
                Point next = points.get(i + 1);
                path.quadTo(point.x, point.y, next.x, next.y);
            } else {
                path.lineTo(point.x, point.y);
            }
        }
        path.lineTo(points.get(0).x,points.get(0).y);
        canvas.drawPath(path, paint);
        }
    }

    public boolean onTouch(View view, MotionEvent event) {


        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_OUTSIDE || event.getAction() == MotionEvent.ACTION_CANCEL) 
        {
             return true;
        }

        int intX = (int) (event.getX());
        int intY = (int) (event.getY());

        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            leftX = intX;
            rightX = intX;
            downY = intY;
            upY = intY;
            
        }

        Point point = new Point();

        point.x = intX;
        point.y = intY;

        if(intX < leftX)
            leftX =  intX;
        if(intX > rightX)
            rightX = intX;
        if(intY > downY)
            downY =  intY;
        if(intY < upY)
            upY = intY;

        points.add(point);


       invalidate();

        return true;
    }

    public void clear() 
    {       
        leftX = 0;
        rightX = 0;
        upY = 0;
        downY = 0;

        points.clear();
        paint.setColor(Color.WHITE);
        paint.setStyle(Style.STROKE);
        invalidate();
    }


    public Bitmap getImage2()
    {

    //getting the size of the new bitmap

        //int newHeight = rightX - leftX;
        //int newWidth = downY - upY;

        Bitmap resultingImage = Bitmap.createBitmap(bitmapMain.getHeight(), bitmapMain.getWidth(),bitmapMain.getConfig());
        //resultingImage = bitmapMain;
        
        Canvas canvas = new Canvas(resultingImage);
        
    // canvas.
        Paint paint = new Paint();
      //  paint.setColor(Color.RED);
        paint.setAntiAlias(true);
      //  paint.setColor(Color.RED);
       Path pp = new Path();
       for (int i = 0; i < points.size() - 1; i++) {

            pp.quadTo((float)points.get(i).x, (float)points.get(i).y, (float)points.get(i+1).x, (float)points.get(i+1).y);
           // path.lineTo(points.get(i).x, points.get(i).y);
        }

//closing the shape, connection last point to the first one

        pp.quadTo((float)points.get(points.size()-1).x, (float)points.get(points.size()-1).y, (float)points.get(0).x, (float)points.get(0).y);

         //drawing user shape
float xscale = 1.6F , yscale = 1.2F ; 
        path = new Path();
        boolean first = true;
        if(points.size() > 0 ) {
        for (int i = 0; i < points.size(); i += 2) {
            Point point = new Point(points.get(i));
            point.x *=  xscale;
            point.y *= yscale;
            if (first) {
                first = false;
                path.moveTo(point.x, point.y);
            } else if (i < points.size() - 1) {
                Point next = new Point(points.get(i + 1));
                next.x *=  xscale;
                next.y *= yscale;
                path.quadTo(point.x, point.y, next.x, next.y);
            } else {
                path.lineTo(point.x, point.y);
            }
        }
        path.lineTo(points.get(0).x * xscale,points.get(0).y * yscale);
        canvas.drawPath(path, paint);
        }
        points.clear();
        
         //getting image inside the shape

      //  canvas.drawPath(pp, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
      //  canvas.drawRect(0, 0, getMeasuredWidth() + 200, 100, paint);
        canvas.drawBitmap(bitmapMain, 0, 0, paint);

        return resultingImage;
        
      

    }



   }