package com.leonardus.irfan.ImageSlider;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.leonardus.irfan.ClickableViewPager;

import java.lang.reflect.Field;

public class ImageSliderViewPager extends ClickableViewPager {
    public ImageSliderViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMyScroller();
    }

    private void setMyScroller() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public class MyScroller extends Scroller {
        private final int SCROLL_DURATION = 500;

        MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, SCROLL_DURATION);
        }
    }
}
