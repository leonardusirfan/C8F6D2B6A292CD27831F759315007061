package id.net.gmedia.pal.Adapter;

import android.app.Activity;

import com.bumptech.glide.Glide;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainSliderAdapter extends SliderAdapter {

    private Activity activity;
    private List<String> listImage;

    public MainSliderAdapter(Activity activity, List<String> listImage){
        this.activity = activity;
        this.listImage = listImage;
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        Glide.with(activity).load(listImage.get(position)).into(imageSlideViewHolder.imageView);
    }
}
