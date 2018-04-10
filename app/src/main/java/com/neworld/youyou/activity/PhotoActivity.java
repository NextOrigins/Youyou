package com.neworld.youyou.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.neworld.youyou.BuildConfig;
import com.neworld.youyou.R;
import com.neworld.youyou.utils.DialogUtil;
import com.neworld.youyou.utils.LogUtils;
import com.neworld.youyou.utils.PhotoUtil;
import com.neworld.youyou.utils.SPUtil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.view.PhotoViewPager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivCancle;
    private ImageView ivMore;
    private TextView tvTitle;
    //private PhotoView photoView;
    private Dialog dialog;
    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //请求访问外部存储
//    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //请求写入外部存储
    private static final int REQUEST_WRITE_EXTERNAL = 104;
    //请求相机
    private final int REQUEST_CAMERA = 11;
    private String userId;

    private static final String SAVE_PIC_PATH = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";
    public static final String SAVE_REAL_PATH = SAVE_PIC_PATH + "/good/";//保存的确切位置
    private String imageUrl;
    private File tempFile;
    private PhotoViewPager viewPager;
    private PhotoView imageView;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        initUser();
        initView();
        initData();
    }

    private void initUser() {
        userId = SPUtil.getString(PhotoActivity.this, "userId", "");
    }

    private void initData() {
        ivCancle.setOnClickListener(this);
        ivMore.setOnClickListener(this);
    }

    private void initView() {
        ivCancle = (ImageView) findViewById(R.id.photo_cancle);
        ivMore = (ImageView) findViewById(R.id.photo_more);
        tvTitle = $(R.id.photo_title);
        if (getIntent().getBooleanExtra("child", false)) tvTitle.setText("照片");
        else tvTitle.setText("头像");
        //photoView = (PhotoView) findViewById(R.id.photo);
        viewPager = (PhotoViewPager) findViewById(R.id.viewpager);
        Bundle extras = this.getIntent().getExtras();
        imageUrl = extras.getString("imageUrl", "");

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            //出实话
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                //可以缩放的图片显示控件
                imageView = new PhotoView(PhotoActivity.this);
                //设置图片
                //Utils.setNetImage(Uris.IMAGEHOST+mImages.get(position),imageView);
                if (imageUrl != null && imageUrl.length() > 0) {
                    Glide.with(PhotoActivity.this).load(imageUrl).into(imageView);
                } else {
                    Glide.with(PhotoActivity.this).load(R.mipmap.my_icon).into(imageView);
                }
                //添加到viewgroup中
                container.addView(imageView);
                //container.addView(imageView);
                imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        PhotoActivity.this.finish();
                    }
                });
                //点击事件 长按点击 和普通点击事件
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

        });

    }

/*    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ()
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("isRefresh", isRefresh);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_cancle:
                Intent intent = new Intent();
                intent.putExtra("isRefresh", isRefresh);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.photo_more:
                showMore();
                break;
            case R.id.choosePhoto:
                dialog.dismiss();
                choosePicture();
                break;
            case R.id.takePhoto:
                dialog.dismiss();
                takePhoto();
                break;
            case R.id.save_photo:
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                if (imageUrl != null && imageUrl.length() > 0) {
                    dialog.dismiss();
                    String replace = imageUrl.replace("http://106.14.251.200:8082/olopicture/icon/", "");
                    saveFile(bitmap, replace, "youyou");
                }
                break;
            case R.id.cancel:
                dialog.dismiss();
                break;
        }
    }

    public void saveFile(Bitmap bm, String fileName, String path) {
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
        ToastUtil.showToast("保存成功");
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(foder);
        intent.setData(uri);
        PhotoActivity.this.sendBroadcast(intent);
    }


    private void showMore() {
        dialog = new Dialog(PhotoActivity.this, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View inflate = LayoutInflater.from(PhotoActivity.this).inflate(R.layout.dialog_photo, null);
        //初始化控件
        TextView choosePhoto = (TextView) inflate.findViewById(R.id.choosePhoto);
        TextView takePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
        TextView savePhoto = (TextView) inflate.findViewById(R.id.save_photo);
        TextView cancel = (TextView) inflate.findViewById(R.id.cancel);

        choosePhoto.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        savePhoto.setOnClickListener(this);
        cancel.setOnClickListener(this);

        initDialog(dialog, inflate);
    }

    private void initDialog(Dialog dialog, View inflate) {
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager windowManager = getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        lp.width = (int) (defaultDisplay.getWidth());
        //lp.y = 20;//设置Dialog距离底部的距离
        //       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    private final int REQUEST_CAMERA_ALL = 2;

    /**
     * 调用系统相机拍摄照片
     */
    private void takePhoto() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.CAMERA}, REQUEST_CAMERA_ALL);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            //跳转到调用系统相机
            gotoCamera();
            return;
        }

        //相机判断
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else if (ContextCompat.checkSelfPermission(PhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL);
        }
    }

    private void gotoCamera() {
        tempFile = new File(checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/cache/"), System.currentTimeMillis() + "Youyou.jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
//            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(PhotoActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0) {
            switch (requestCode) {
                case REQUEST_CAMERA_ALL:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        //跳转到调用系统相机
                        gotoCamera();
                    } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                        // 读写权限给了， 相机没给
                        pmsDialog("相机权限未被授予,请到权限管理中添加");
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        // 读写没给，相机给了
                        pmsDialog("读写权限未被授予,请到权限管理中添加");
                    } else {
                        // 全都没给
                        pmsDialog("读写与相机权限未被授予,无法使用相机功能,请到权限管理中添加");
                    }
                    break;
                case REQUEST_WRITE_EXTERNAL:
                    if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                        gotoCamera();
                    } else {
                        pmsDialog("权限未被授予，请到权限管理中添加相机权限");
                    }
                    break;
//            case READ_EXTERNAL_STORAGE_REQUEST_CODE:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission Granted
//                    choosePicture();
//                } else {
//                    pmsDialog("权限未被授予，请到权限管理中添加读写权限");
//                }
//                break;
                case REQUEST_CAMERA:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        gotoCamera();
                    } else {
                        pmsDialog("权限未被授予，请到权限管理中添加相机权限");
                    }
                    break;
            }
        }
    }

    DialogUtil.OnDialogClickListener dialogClickListener = new DialogUtil.OnDialogClickListener() {
        @Override
        public void onPositive(DialogInterface dialog, int which) {
            dialog.dismiss();
        }

        @Override
        public void onNegative(DialogInterface dialog, int which) {

        }
    };

    private void pmsDialog(String message) {
        DialogUtil.setDialog(this, message,
                "确定", "", dialogClickListener);
    }

    /**
     * 从系统图库中选择图片
     */
    private void choosePicture() {
        /**
         * Action
         * data MediaStore.Images.Media.EXTERNAL_CONTENT_URI
         */
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
    }

    /**
     * 检查文件是否存在
     */
    private static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }

    /**
     * 打开截图界面
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, ClipImageActivity.class);
        intent.putExtra("type", 2);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    /**
     * 根据Uri返回文件绝对路径
     * 兼容了file:///开头的 和 content://开头的情况
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    LogUtils.D("TAG : \"evan\", \"**********camera uri*******\" + Uri.fromFile(tempFile).toString()");
                    LogUtils.D("TAG : \"evan\", \"**********camera path*******\" + getRealFilePathFromUri(PhotoActivity.this, Uri.fromFile(tempFile))");
                    gotoClipActivity(Uri.fromFile(tempFile));
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    Log.d("evan", "**********pick path*******" + getRealFilePathFromUri(PhotoActivity.this, uri));
                    gotoClipActivity(uri);
                }
                break;
            case REQUEST_CROP_PHOTO:  //剪切图片返回
                if (resultCode == RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    String path = uri.getPath();
                    //File file = new File(uri);
                    // String cropImagePath = getRealFilePathFromUri(getApplicationContext(), uri);
                    //Bitmap bitMap = BitmapFactory.decodeFile(path);
                    sendImageMsg(path);
                }
                break;
        }
    }

    private void sendImageMsg(final String imagePath) {
        String parseFile = parseFile(imagePath);
        new Thread(() -> {
            boolean b;
            if (getIntent().getBooleanExtra("child", false)) {
                b = PhotoUtil.saveNet(userId, parseFile
                        , getIntent().getIntExtra("babyId", 0), "141_1");
            } else {
                b = PhotoUtil.saveNet(userId, parseFile, "127");
            }
            if (b) {
                isRefresh = true;
            } else {
                runOnUiThread(() -> {
                    dialog.dismiss();
                    ToastUtil.showToast("上传失败");
                });
            }
        }).start();

     /*   File file = new File(imagePath);
        Luban.get(PhotoActivity.this)
                .load(file)
                .putGear(Luban.THIRD_GEAR)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }
                    @Override
                    public void onSuccess(File file) {
                        String path = file.getPath();
                        String parseFile =  parseFile(path);
                        saveNet(parseFile);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast("网络异常");
                    }
                }).launch();*/
    }

    //将压缩好的图片加密
    private String parseFile(String file) {
        Bitmap bitmap = BitmapFactory.decodeFile(file);
        imageView.setImageBitmap(bitmap);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        String replace = base64.replace("\n", "");
        return replace.replace("+", "%2B");
    }

    private <T extends View> T $(int res) {
        return (T) findViewById(res);
    }
}
