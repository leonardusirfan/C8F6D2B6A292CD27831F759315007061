package com.leonardus.irfan.ImageSlider;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.leonardus.irfan.ClickableViewPager;
import com.leonardus.irfan.R;

import me.relex.circleindicator.CircleIndicator;

public class ImageSlider extends RelativeLayout{
    CircleIndicator customIndicator;

    View root_layout;
    ImageSliderViewPager viewpager;
    CircleIndicator indicator;

    SliderEventListener listener;

    public ImageSlider(Context context) {
        super(context);
        init(context);
    }

    public ImageSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        root_layout = inflate(context, R.layout.custom_image_slider, this);
        viewpager = root_layout.findViewById(R.id.viewpager);
        indicator = root_layout.findViewById(R.id.indicator);

        viewpager.setCustomTouchListener(new ClickableViewPager.CustomTouchListener() {
            @Override
            public void onPress() {
                if(listener != null){
                    listener.onClick();
                }
            }
        });
    }

    public void setIndicator(CircleIndicator newIndicator){
        customIndicator = newIndicator;
        customIndicator.setViewPager(viewpager);
        if(viewpager.getAdapter() != null){
            indicator.setVisibility(INVISIBLE);
        }
    }

    public void setAdapter(ImageSliderAdapter adapter){
        boolean is_indicator = false;
        setAdapter(adapter, is_indicator);
    }

    public void setAdapter(ImageSliderAdapter adapter, boolean is_indicator){
        viewpager.setAdapter(adapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(viewpager.getAdapter()!=null){
                    ((ImageSliderAdapter) viewpager.getAdapter()).setPosition(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        adapter.setSlider(viewpager);

        if(customIndicator == null && is_indicator){
            indicator.setViewPager(viewpager);
        }
        else if(customIndicator != null){
            indicator.setVisibility(INVISIBLE);
            customIndicator.setViewPager(viewpager);
        }
    }

    public void setListener(SliderEventListener listener){
        this.listener = listener;
    }

    public interface SliderEventListener {
        void onClick();
    }
}
