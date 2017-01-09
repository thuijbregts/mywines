package com.example.thomas.mywines.activities.overrides;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.informationclasses.Wine;

public class ParentListLayout extends RelativeLayout {

    private int type;
    private boolean draw;
    private RectF top;
    private RectF bottom;
    private Paint paint = new Paint();

    public ParentListLayout(Context context) {
        super(context);
    }

    public ParentListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParentListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void paintRed(){
        type = Wine.RED;
        invalidate();
    }

    public void paintRose(){
        type = Wine.ROSE;
        invalidate();
    }

    public void paintWhite(){
        type = Wine.WHITE;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(draw) {
            super.onDraw(canvas);

            switch (type){
                case Wine.RED:
                    paint.setColor(getResources().getColor(R.color.expandable_list_parent_red_top));
                    canvas.drawRect(top, paint);
                    paint.setColor(getResources().getColor(R.color.expandable_list_parent_red_bottom));
                    canvas.drawRect(bottom, paint);
                    break;
                case Wine.ROSE:
                    paint.setColor(getResources().getColor(R.color.expandable_list_parent_rose_top));
                    canvas.drawRect(top, paint);
                    paint.setColor(getResources().getColor(R.color.expandable_list_parent_rose_bottom));
                    canvas.drawRect(bottom, paint);
                    break;
                case Wine.WHITE:
                    paint.setColor(getResources().getColor(R.color.expandable_list_parent_white_top));
                    canvas.drawRect(top, paint);
                    paint.setColor(getResources().getColor(R.color.expandable_list_parent_white_bottom));
                    canvas.drawRect(bottom, paint);
                    break;
            }
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        draw = true;
        top = new RectF(0, 0, w, (float)h/2);
        bottom = new RectF(0, (float)h/2, w, h);
        paint.setStyle(Paint.Style.FILL);
        setWillNotDraw(false);
    }
}
