package com.example.mqttclient.dele_add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.Toast;

import androidx.annotation.Nullable;

import static android.app.Activity.RESULT_OK;

public class MyListView extends ListView {

    private LinearLayout mPreScrollView;

    private LinearLayout itemRoot;

    private Context mContext;

    private Scroller mScroller;

    private int mlastX = 0;

    private final int MAX_WIDTH = 60;

    private Scroller mPreScroller;

    private MyListView myListView;

    private OnItemClickListener onItemClickListener;

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mScroller = new Scroller(context, new LinearInterpolator(context, null));
        mPreScroller = new Scroller(context, new LinearInterpolator(context, null));
    }

    @Override
    public Object getItemAtPosition(int position) {
        return super.getItemAtPosition(position);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int maxLength = dipToPx(getContext(),MAX_WIDTH);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                //获取点击的行
                Toast.makeText(getContext(),"点击",Toast.LENGTH_SHORT).show();
                int position = pointToPosition(x, y);
                if (position != INVALID_POSITION) {
                    DeviceAdapter.DataHolder data = (DeviceAdapter.DataHolder)getItemAtPosition(position);
                    itemRoot = data.rootView;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                //Toast.makeText(getContext(),"移动",Toast.LENGTH_SHORT).show();
                int scrollX = itemRoot.getScrollX();
                int newScrollX = scrollX + mlastX - x;
                if (newScrollX < 0) {
                    newScrollX = 0;
                } else if (newScrollX > maxLength) {
                    newScrollX = maxLength;
                }
                itemRoot.scrollTo(newScrollX, 0);
                //super.onTouchEvent(event);//调用父类方法，防止滑动时触发点击事件
                break;
            }
            case MotionEvent.ACTION_UP: {
                Toast.makeText(getContext(),"松开",Toast.LENGTH_SHORT).show();
                int scrollX = itemRoot.getScrollX();
                int newScrollX = scrollX + mlastX - x;
                if (scrollX > maxLength / 2) {
                    newScrollX = maxLength;
                }else {
                    newScrollX = 0;
                }
                itemRoot.scrollTo(newScrollX, 0);

            }
            break;
        }
        mlastX = x;
        return super.onTouchEvent(event);
    }

    public void disPatchTouchEvent(MotionEvent event){
        int maxLength = dipToPx(mContext, MAX_WIDTH);

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                int scrollX = this.getScrollX();
                int newScrollX = scrollX + mlastX - x;
                if (newScrollX < 0) {
                    newScrollX = 0;
                } else if (newScrollX > maxLength) {
                    newScrollX = maxLength;
                }
                this.scrollTo(newScrollX, 0);
            }
            break;
            case MotionEvent.ACTION_UP: {
                int scrollX = this.getScrollX();
                int newScrollX = scrollX + mlastX - x;
                if (scrollX > maxLength / 2) {
                    newScrollX = maxLength;
                }
                else {
                    newScrollX = 0;
                }
                mScroller.startScroll(scrollX, 0, newScrollX - scrollX, 0);
                invalidate();
            }
            break;
        }
        mlastX = x;
    }


    public void computeScroll() {
        if (mScroller.computeScrollOffset()){
            this.scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
        }
        invalidate();
    }

    private int dipToPx(Context context, int dip) {
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
    }

}
