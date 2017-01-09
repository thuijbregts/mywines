package com.example.thomas.mywines.activities.winesActivity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Rect;
import android.graphics.RectF;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;

public class ImageCanvas extends ImageView implements View.OnTouchListener{

    private PictureActivity activity;
    private float boxSide;
    private boolean editing = false;
    private int width;
    private int height;

    private Bitmap noThumb;
    private int thumbNailSize = 46;
    private int outerBoxSize = 50;
    private Rect imageBox;
    private int dp;

    private Paint paint = new Paint();
    private LinearLayout view;
    private RectF box;
    private float centerX;
    private float centerY;

    public ImageCanvas(PictureActivity context) {
        super(context);
        activity = context;
        view = (LinearLayout) context.findViewById(R.id.dialog_picture_parent);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        lp.weight = 90;
        setLayoutParams(lp);
        view.addView(this, 0);

        noThumb = BitmapFactory.decodeResource(getResources(), R.drawable.no_picture, null);
        dp = MainActivity.convertDpToPixel(1, context);
        setOnTouchListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        centerX = (float)w/2;
        centerY = (float)h/2;
        boxSide = w/2;
        box = new RectF(centerX - boxSide, centerY - boxSide, centerX + boxSide, centerY + boxSide);
        imageBox = new Rect(width-(outerBoxSize*dp)+(2*dp), 2*dp, width-2*dp, thumbNailSize*dp + 2*dp);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(editing) {
            paint.setColor(Color.parseColor("#880000FF"));
            canvas.drawRect(0, 0, box.left, height, paint);
            canvas.drawRect(box.left, 0, box.right, box.top, paint);
            canvas.drawRect(box.right, 0, width, height, paint);
            canvas.drawRect(box.left, box.bottom, box.right, height, paint);
        }
        else{
            paint.setColor(getResources().getColor(R.color.dialog_picture_button_pressed));
            canvas.drawRect(width-outerBoxSize*dp, 0, width, outerBoxSize*dp, paint);
            if(activity.getThumbnailToSave() != null){
                canvas.drawBitmap(activity.getThumbnailToSave(), null, imageBox, null);
            }
            else{
                canvas.drawBitmap(noThumb, null, imageBox, null);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!editing)
            return false;
        if(event.getX() < boxSide)
            centerX = boxSide;
        else if(event.getX() > width - boxSide)
            centerX = width - boxSide;
        else
            centerX = event.getX();

        if(event.getY() < boxSide)
            centerY = boxSide;
        else if(event.getY() > height - boxSide)
            centerY = height - boxSide;
        else
            centerY = event.getY();

        box.left = centerX - boxSide;
        box.right = centerX + boxSide;
        box.top = centerY - boxSide;
        box.bottom = centerY + boxSide;
        invalidate();
        return true;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
        invalidate();
    }

    public boolean isEditing() {
        return editing;
    }

    public Bitmap getThumbnail(){
        Bitmap b = activity.getImageToSave();
        Bitmap result = Bitmap.createBitmap(b, (int)box.left, (int)box.top, (int)box.right - (int)box.left, (int)box.bottom - (int)box.top);
        return result;
    }
}
