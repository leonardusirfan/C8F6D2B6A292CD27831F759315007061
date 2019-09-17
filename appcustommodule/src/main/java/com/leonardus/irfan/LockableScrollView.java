package com.leonardus.irfan;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class LockableScrollView extends ScrollView {

    // true if we can scroll (not locked)
    // false if we cannot scroll (locked)
    private boolean mScrollable = true;

    public LockableScrollView(Context context) {
        super(context);
    }

    public LockableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

   /* public LockableScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if(Build.VERSION.SDK_INT >= 21){
            super(context, attrs, defStyleAttr, defStyleRes);
        }
        else{
            super(context, attrs, defStyleAttr, defStyleRes);
        }
    }*/

    public void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;
    }

    public boolean isScrollable() {
        return mScrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                return mScrollable && super.onTouchEvent(ev);
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        return mScrollable && super.onInterceptTouchEvent(ev);
    }

}
