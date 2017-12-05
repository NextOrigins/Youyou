package com.neworld.youyou.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.neworld.youyou.R;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.view.PhotoViewPager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoDetailActivity extends AppCompatActivity  {

    private ProgressBar pb;
    private PhotoViewPager viewpager;
    private PhotoView imageView;
    private String fromActivity;
    private TextView choosePhoto;
    private TextView takePhoto;
    private TextView savePhoto;
    private TextView cancel;
    private RelativeLayout rl_screen;
    private static final String SAVE_PIC_PATH  = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";
    public static final String SAVE_REAL_PATH = SAVE_PIC_PATH+ "/good/";//保存的确切位置
    private List<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        initView();
        initData();
    }

    private void initView() {
        pb = (ProgressBar) findViewById(R.id.pb);
        viewpager = (PhotoViewPager) findViewById(R.id.viewpager);
        rl_screen = (RelativeLayout) findViewById(R.id.activity_photo_detail);
    }

    private void initData() {
        Bundle bunde = this.getIntent().getExtras();
        //final String[] images = bunde.getStringArray("imageString");
        images = (List<String>)bunde.getSerializable("imageList");
        int currentPosition = bunde.getInt("currentPosition");
        fromActivity = bunde.getString("FromActivity");
        int point = images.size();
        pb.setEnabled(true);
        viewpager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return images.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            //出实话
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                //可以缩放的图片显示控件
                imageView = new PhotoView(PhotoDetailActivity.this);
                //设置图片
                //Utils.setNetImage(Uris.IMAGEHOST+mImages.get(position),imageView);
                Glide.with(PhotoDetailActivity.this).load(images.get(position)).into(imageView);
                //添加到viewgroup中
                container.addView(imageView);
                //container.addView(imageView);
                pb.setEnabled(false);
                imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        PhotoDetailActivity.this.finish();
                    }
                });
                //点击事件 长按点击 和普通点击事件
                initClick(position);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        //设置当前选中
        viewpager.setCurrentItem(currentPosition);
    }

    private void initClick(final int position) {

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(PhotoDetailActivity.this, R.style.ActionSheetDialogStyle);
                //填充对话框的布局
                View inflate = LayoutInflater.from(PhotoDetailActivity.this).inflate(R.layout.dialog_photo, null);
                //初始化控件
                choosePhoto = (TextView) inflate.findViewById(R.id.choosePhoto);
                takePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
                savePhoto = (TextView) inflate.findViewById(R.id.save_photo);
                cancel = (TextView) inflate.findViewById(R.id.cancel);
          /*      choosePhoto.setOnClickListener(this);
                takePhoto.setOnClickListener(this);*/
                savePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //http://106.14.251.200:8082/olopicture/contextImg/10854201708181208301503029310711.jpeg
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        if (images != null && images.size() > 0) {
                            String s = images.get(position);
                            //Drawable fromPath = BitmapDrawable.createFromPath(s);

                            if (s != null && s.length() > 0) {
                                String replace = s.replace("http://106.14.251.200:8082/olopicture/contextImg/", "");
                                saveFile(bitmap, replace, "youyou");
                                //saveFile(bitmap, "youyou", s);
                            }
                        }
                        dialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                choosePhoto.setVisibility(View.GONE);
                takePhoto.setVisibility(View.GONE);
                //初始化dialog
                initDialog(dialog, inflate);
                return true;
            }
        });
    }

    public void saveFile(Bitmap bm, String fileName, String path)  {
        String subForder = SAVE_REAL_PATH + path;
        File foder = new File(subForder);
        if (!foder.exists()) {
            foder.mkdirs();
        }

        BufferedOutputStream bos = null;
        try {
            File myCaptureFile = new File(subForder, fileName);
            if (!myCaptureFile.exists()) {
                myCaptureFile.createNewFile();
            }
            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(foder);
        intent.setData(uri);
        PhotoDetailActivity.this.sendBroadcast(intent);
    }

    private void initDialog(Dialog dialog, View inflate) {
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity( Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager windowManager = getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        lp.width = (int)(defaultDisplay.getWidth());
        //lp.y = 20;//设置Dialog距离底部的距离
        //       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }
}
