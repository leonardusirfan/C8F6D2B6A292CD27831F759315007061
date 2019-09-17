package com.leonardus.irfan;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.View;

public class DynamicGridLayout extends GridLayout {

    View[] mChild = null;

    public DynamicGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DynamicGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DynamicGridLayout(Context context) {
        this(context, null);
    }

    private void arrangeElements() {

        mChild = new View[getChildCount()];
        for (int i = 0; i < getChildCount(); i++) {
            mChild[i] = getChildAt(i);
        }

        removeAllViews();
        for (int i = 0; i < mChild.length; i++) {
            if (mChild[i].getVisibility() != View.GONE)
                addView(mChild[i]);
        }
        for (int i = 0; i < mChild.length; i++) {
            if (mChild[i].getVisibility() == View.GONE)
                addView(mChild[i]);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        arrangeElements();
        super.onLayout(changed, left, top, right, bottom);

    }


}