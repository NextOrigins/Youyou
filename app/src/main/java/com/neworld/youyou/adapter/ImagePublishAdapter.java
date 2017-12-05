package com.neworld.youyou.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.neworld.youyou.select.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

public class ImagePublishAdapter extends PagerAdapter {

    private Context mContext;
    private List<PhotoView> viewList = new ArrayList<>(4);
    List<String> mImgList;
    //private OnItemClickListener mListener;

    public ImagePublishAdapter(Context context, List<String> imgList) {
        this.mContext = context;
        createImageViews();
        mImgList = imgList;
    }

    private void createImageViews() {
        for (int i = 0; i < 4; i++) {
            PhotoView imageView = new PhotoView(mContext);
            viewList.add(imageView);
        }
    }

    @Override
    public int getCount() {
        return mImgList == null ? 0 : mImgList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if(object instanceof  PhotoView){
            PhotoView view = (PhotoView)object;
            view.setImageDrawable(null);
            viewList.add(view);
            container.removeView(view);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final PhotoView currentView = viewList.remove(0);
        final String path = mImgList.get(position);
        container.addView(currentView);
        Glide.with(mContext).load(path).into(currentView);
     /*   currentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position, path);
                }
            }
        });*/
        return currentView;
    }

 /*   public void setOnItemClickListener(OnItemClickListener l) {
        mListener = l;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String image);
    }*/
}
