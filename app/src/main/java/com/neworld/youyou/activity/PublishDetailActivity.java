package com.neworld.youyou.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.adapter.ImagePublishAdapter;
import com.neworld.youyou.view.PhotoViewPager;

import java.util.ArrayList;


public class PublishDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private PhotoViewPager viewPager;
    private ImageView ivCancel;
    private ImageView ivDelete;
    private ArrayList<String> mData = new ArrayList<>();
    private TextView tvChoose;
    private String point;
    private ImageView imageView;
    private int tempPosition;
    private ImagePublishAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_detail);
        setStatusBarColor();
        initView();
        initData();
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#373c3d"));
        }
    }

    private void initData() {
        if (adapter == null) {
            adapter = new ImagePublishAdapter(this, mData);
            viewPager.setAdapter(adapter);
        }
        viewPager.setCurrentItem(Integer.parseInt(point));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tempPosition = position;
                tvChoose.setText((position + 1 ) + "/" + mData.size());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initView() {
        viewPager = (PhotoViewPager) findViewById(R.id.viewpager);
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        ivDelete = (ImageView) findViewById(R.id.iv_delete);
        tvChoose = (TextView) findViewById(R.id.tv_choose);
        ivCancel.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<String> imageList =  extras.getStringArrayList("imageList");
            point = extras.getString("point", "1");
            if (imageList != null && imageList.size() > 0) {
                mData.addAll(imageList);
            }
        }
        tvChoose.setText((Integer.parseInt(point) + 1) + "/" + mData.size());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                Intent intent = getIntent();
                intent.putStringArrayListExtra("publish", mData);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.iv_delete:
                delete();
                break;
        }
    }
    //删除
    private void delete() {
        mData.remove(tempPosition);
        if (mData.size() >= 1) {
            viewPager.setAdapter(null);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(mData.size() -1);
        } else {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("publish", mData);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putStringArrayListExtra("publish", mData);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    /*
     class Page extends PagerAdapter {
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //出实话
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //可以缩放的图片显示控件
            imageView = new ImageView(PublishDetailActivity.this);
            //设置图片
            //Utils.setNetImage(Uris.IMAGEHOST+mImages.get(position),imageView);
            Glide.with(PublishDetailActivity.this).load(mData.get(position)).into(imageView);
            ToastUtil.showToast(position + "");
            tvChoose.setText(position + "/" + mData.size());
            tempPosition = position;
            //添加到viewgroup中
            container.addView(imageView);
          *//*  imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    PublishDetailActivity.this.finish();
                }
            });*//*
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PublishDetailActivity.this.finish();
                }
            });
            //点击事件 长按点击 和普通点击事件
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }*/
}
