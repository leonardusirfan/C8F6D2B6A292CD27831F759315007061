package com.leonardus.irfan.ImageSlider;

import android.content.Context;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.leonardus.irfan.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ImageSliderAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private ArrayList<ImageSliderItem> items;
    private int currentPage = 0;
    private Context context;
    private boolean crop;
    private ImageSliderViewPager viewpager;

    private long slide_duration = 3000;
    private Runnable Update;
    private Timer autoscroll;
    private boolean paused = false;
    /*private Timer swipeTimer;
    private Runnable Update;
    private Handler handler;*/

    public ImageSliderAdapter(Context context, List<String> images, boolean crop){
        this.context = context;
        this.crop = crop;

        //Inisialisasi item slider
        items = new ArrayList<>();
        for(String s : images){
            items.add(new ImageSliderItem(s));
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    void setPosition(int position){
        currentPage = position;
    }

    void setSlider(ImageSliderViewPager viewpager){
        this.viewpager = viewpager;
    }

    private void slide(){
        if (currentPage + 1 == items.size()) {
            currentPage = 0;
        }
        else{
            currentPage++;
        }
        viewpager.setCurrentItem(currentPage, true);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        View myImageLayout = LayoutInflater.from(context).inflate(R.layout.custom_slider, view, false);
        ImageView myImage = myImageLayout.findViewById(R.id.image);
        if(crop){
            Glide.with(context).load(items.get(position).getItem()).
                    transition(DrawableTransitionOptions.withCrossFade()).
                    apply(RequestOptions.centerCropTransform()).into(myImage);
        }
        else{
            Glide.with(context).load(items.get(position).getItem()).
                    transition(DrawableTransitionOptions.withCrossFade()).into(myImage);
        }
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void reloadImages(List<String> images){
        //Inisialisasi item slider
        items = new ArrayList<>();
        for(String s : images){
            items.add(new ImageSliderItem(s));
        }

        currentPage = 0;

        stopAutoscroll();
        setAutoscroll();
        notifyDataSetChanged();
    }

    public void setAutoscroll(long duration){
        slide_duration = duration;
        setAutoscroll();
    }

    public void setAutoscroll(){
        if(autoscroll == null){

            Update = new Runnable() {
                @Override
                public void run() {
                    slide();
                    //Log.d("TIMER_LOG", String.valueOf(currentPage));
                }
            };

            final Handler handler = new Handler();

            autoscroll = new Timer();
            autoscroll.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, slide_duration, slide_duration);
        }
    }

    public void stopAutoscroll(){
        if(autoscroll != null){
            autoscroll.cancel();
            autoscroll = null;
        }
    }

    public void pauseAutoscroll(){
        paused = true;
    }

    public void resumeAutoscroll(){
        paused = false;
    }
}
