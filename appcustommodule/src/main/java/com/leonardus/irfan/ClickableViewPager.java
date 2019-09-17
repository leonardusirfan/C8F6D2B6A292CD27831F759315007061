package com.leonardus.irfan;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ClickableViewPager extends ViewPager {

    private CustomTouchListener listener;

    public ClickableViewPager(Context context) {
        super(context);
        setup();
    }

    public ClickableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        final GestureDetector tapGestureDetector = new GestureDetector(getContext(), new TapGestureListener());

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                tapGestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    public void setCustomTouchListener(CustomTouchListener listener) {
        this.listener = listener;
    }

    public interface CustomTouchListener{
        void onPress();
    }

    private class TapGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(listener != null){
                listener.onPress();
            }
            return true;
        }
    }
}