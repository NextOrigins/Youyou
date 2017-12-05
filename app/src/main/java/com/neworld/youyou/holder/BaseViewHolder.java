package com.neworld.youyou.holder;

import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by tt on 2017/3/26.
 */

public abstract class BaseViewHolder<T> {

    View view;
    public BaseViewHolder() {
        view = createItemView();
        view.setTag(this);
    }

    //谁用谁传
    public abstract View createItemView();

    public abstract void bindView(T t);
    //返回一个view
    public View getView() {
        return view;
    }

}
