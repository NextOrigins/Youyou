package com.neworld.youyou.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.neworld.youyou.R;
import com.neworld.youyou.bean.PublishResultBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.bean.TypeBean;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.select.ImageSelectorUtils;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PublishActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvCancle;
    private TextView tvChoose;
    private TextView tvPublish;
    private EditText etTheme;
    private EditText etPublish;

    List<TypeBean.MenuListBean> mData = new ArrayList<>();
    private File photoFile;
    private static final int REQUEST_PIC = 1;
    private static final String TAG = "ChatActivity";
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_PERMISSION_CAMERA = 10001;

    private Dialog dialog;
    private static final int REQUEST_CODE = 0x00000011;
    private static final int REQUEST_PUBLISH = 0x00000012;
    private ArrayList<String> newList = new ArrayList<>();
    //private NoScrollGridView gridView;
    private String typeId = "";
    private OptionsPickerView pvoptionsChoose;
    private String userId;
    private String theme = "";
    private String publish = "";
    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;
    private ImageView iv4;
    private ImageView iv5;
    private ImageView iv6;
    private ImageView iv7;
    private ImageView iv8;
    private ImageView iv9;
    private ProgressDialog progressDialog;

    @SuppressWarnings("ALL")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        tvCancle = (TextView) findViewById(R.id.tv_cancle);
        tvChoose = (TextView) findViewById(R.id.tv_choose);
        tvPublish = (TextView) findViewById(R.id.tv_publish);
        etTheme = (EditText) findViewById(R.id.et_add_theme);
        etPublish = (EditText) findViewById(R.id.et_add_content);
        iv1 = (ImageView) findViewById(R.id.iv_1);
        iv2 = (ImageView) findViewById(R.id.iv_2);
        iv3 = (ImageView) findViewById(R.id.iv_3);
        iv4 = (ImageView) findViewById(R.id.iv_4);
        iv5 = (ImageView) findViewById(R.id.iv_5);
        iv6 = (ImageView) findViewById(R.id.iv_6);
        iv7 = (ImageView) findViewById(R.id.iv_7);
        iv8 = (ImageView) findViewById(R.id.iv_8);
        iv9 = (ImageView) findViewById(R.id.iv_9);

        iv1.setOnClickListener(this);
        iv2.setOnClickListener(this);
        iv3.setOnClickListener(this);
        iv4.setOnClickListener(this);
        iv5.setOnClickListener(this);
        iv6.setOnClickListener(this);
        iv7.setOnClickListener(this);
        iv8.setOnClickListener(this);
        iv9.setOnClickListener(this);
        etTheme.setOnTouchListener((v, event) -> {
            if (!v.isFocusableInTouchMode())
                v.setFocusableInTouchMode(true);
            return false;
        });
        iv1.setVisibility(View.VISIBLE);

        initUser();
        initData();
        initView();
    }

    private void initUser() {
        userId = Sputil.getString(PublishActivity.this, "userId", "");
    }

    private void initData() {
        new Thread(() -> {
            String content = NetManager.getInstance().getContent("", "120");
            if (content != null && content.length() > 0) {
                TypeBean typeBean = GsonUtil.parseJsonToBean(content, TypeBean.class);
                if (typeBean != null && typeBean.getStatus() == 0) {
                    List<TypeBean.MenuListBean> menuList = typeBean.getMenuList();
                    mData.addAll(menuList);
                }
            }
        }).start();
    }

    //此方法只是关闭软键盘
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null && imm != null && imm.isActive()) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void initView() {

        tvCancle.setOnClickListener(this);
        tvChoose.setOnClickListener(this);
        tvPublish.setOnClickListener(v -> publishContent());
        // 创建界面后弹出键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancle:
                backPublish();
                break;
            case R.id.tv_choose:
                hintKbTwo();
                pvoptionsChoose = new OptionsPickerView.Builder(PublishActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        String pickerViewText = mData.get(options1).getPickerViewText();
                        Log.e("PublishActivity", pickerViewText);
                        if (pickerViewText != null && pickerViewText.length() > 0) {
                            tvChoose.setText(pickerViewText);
                            if (pickerViewText.equals("升学")) {
                                typeId = "1";
                            } else if (pickerViewText.equals("热点话题")) {
                                typeId = "2";
                            } else if (pickerViewText.equals("七嘴八舌")) {
                                typeId = "10";
                            } else if (pickerViewText.equals("中考")) {
                                typeId = "3";
                            } else if (pickerViewText.equals("育儿")) {
                                typeId = "4";
                            } else if (pickerViewText.equals("婚后夫妻")) {
                                typeId = "5";
                            } else if (pickerViewText.equals("婆媳关系")) {
                                typeId = "6";
                            } else if (pickerViewText.equals("美食厨房")) {
                                typeId = "7";
                            } else if (pickerViewText.equals("旅行随拍")) {
                                typeId = "8";
                            } else if (pickerViewText.equals("创意手工")) {
                                typeId = "9";
                            }
                        }
                    }
                })
                        .setTitleText("类型选择")
                        .setContentTextSize(20)//设置滚轮文字大小
                        .setDividerColor(Color.parseColor("#e7e7e7"))//设置分割线的颜色
                        .setSelectOptions(0, 1)//默认选中项
                        .setBgColor(Color.WHITE)
                        .setTitleBgColor(Color.parseColor("#f2f2f2"))
                        .setTitleColor(Color.BLACK)
                        .setCancelColor(Color.BLACK)
                        .setSubmitColor(Color.BLACK)
                        .setTextColorCenter(Color.BLACK)
                        .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                        .setLabels("", "", "")
                        .setBackgroundId(0x66000000) //设置外部遮罩颜色
                        .build();
                pvoptionsChoose.setPicker(mData);//二级选择器
                pvoptionsChoose.show();

                break;
            case R.id.choosePhoto:
                ImageSelectorUtils.openPhoto(PublishActivity.this, REQUEST_CODE, false, 9 - newList.size());
                dialog.dismiss();
                break;
            case R.id.takePhoto:
                takePhoto();
                dialog.dismiss();
                break;
            case R.id.cancel:
                dialog.dismiss();
                break;
            case R.id.iv_1:
                if (newList != null && newList.size() == 0) {
                    tanDialog();
                } else if (newList.size() > 0) {
                    startImage("0");
                }
                break;
            case R.id.iv_2:
                if (newList != null && newList.size() == 1) {
                    tanDialog();
                } else if (newList.size() > 1) {
                    startImage("1");
                }
                break;
            case R.id.iv_3:
                if (newList != null && newList.size() == 2) {
                    tanDialog();
                } else if (newList.size() > 2) {
                    startImage("2");
                }
                break;
            case R.id.iv_4:
                if (newList != null && newList.size() == 3) {
                    tanDialog();
                } else if (newList.size() > 3) {
                    startImage("3");
                }
                break;
            case R.id.iv_5:
                if (newList != null && newList.size() == 4) {
                    tanDialog();
                } else if (newList.size() > 4) {
                    startImage("4");
                }
                break;
            case R.id.iv_6:
                if (newList != null && newList.size() == 5) {
                    tanDialog();
                } else if (newList.size() > 5) {
                    startImage("5");
                }
                break;
            case R.id.iv_7:
                if (newList != null && newList.size() == 6) {
                    tanDialog();
                } else if (newList.size() > 6) {
                    startImage("6");
                }
                break;
            case R.id.iv_8:
                if (newList != null && newList.size() == 7) {
                    tanDialog();
                } else if (newList.size() > 7) {
                    startImage("7");
                }
                break;
            case R.id.iv_9:
                if (newList != null && newList.size() == 8) {
                    tanDialog();
                } else if (newList.size() > 8) {
                    startImage("8");
                }
                break;
        }
    }

    private void backPublish() {
        if ((etPublish.getText().toString().trim() != null && etPublish.getText().toString().trim().length() > 0) || (etTheme.getText().toString().trim() != null && etTheme.getText().toString().trim().length() > 0)) {
            dialogPublish();
        } else if (newList != null && newList.size() > 0) {
            dialogPublish();
        } else {
            finish();
        }
    }

    private void dialogPublish() {
        final AlertDialog.Builder ad = new AlertDialog.Builder(PublishActivity.this);
        ad.setMessage("确定退出编辑吗");
        ad.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        ad.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        ad.show();
    }

    private void startImage(String s) {
        Intent intent = new Intent();
        intent.setClass(PublishActivity.this, PublishDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("imageList", newList);
        bundle.putString("point", s);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_PUBLISH);
    }

    private void tanDialog() {
        dialog = new Dialog(PublishActivity.this, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View inflate = LayoutInflater.from(PublishActivity.this).inflate(R.layout.dialog_photo, null);
        //初始化控件
        TextView choosePhoto = inflate.findViewById(R.id.choosePhoto);
        TextView takePhoto = inflate.findViewById(R.id.takePhoto);
        TextView savePhoto = inflate.findViewById(R.id.save_photo);
        TextView cancel = inflate.findViewById(R.id.cancel);
        choosePhoto.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        savePhoto.setVisibility(View.GONE);
        cancel.setOnClickListener(this);
        initDialog(dialog, inflate);
    }

    /**
     * 调用系统相机拍摄照片
     */
    private void takePhoto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
            return;
        }
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//        photoFile = new File(directory, new Date().getTime() + "Youyou.jpg");
//        //file://storage/DCIM/148xxxxxxx.jpg
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//        startActivityForResult(intent, REQUEST_CAMERA);
        gotoCamera();
    }

    private void gotoCamera() {
        File tempFile = new File(checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"), System.currentTimeMillis() + "Youyou.jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
//            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, "com.neworld.youyou.fileprovider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    /**
     * 检查文件是否存在
     */
    private String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                //被授权了
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            } else {
                //没有被授权
                ToastUtil.showToast("您拒绝了照相功能！");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> stringArrayListExtra = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
                    if (stringArrayListExtra != null && stringArrayListExtra.size() > 0) {
                        newList.addAll(stringArrayListExtra);
                        judgeList();
                    }
                }
                break;
            case REQUEST_CAMERA: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    if (photoFile != null) {
                        String path = photoFile.getPath();
                        if (path != null && path.length() > 0) {
                            newList.add(path);
                            judgeList();
                        }
                    }
                } else {
                    ToastUtil.showToast("没有照相");
                }
                break;
            case REQUEST_PUBLISH:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> stringArrayListExtra = data.getStringArrayListExtra("publish");
                    if (stringArrayListExtra != null && stringArrayListExtra.size() > 0) {
                        newList.clear();
                        newList.addAll(stringArrayListExtra);
                        iv2.setVisibility(View.GONE);
                        iv3.setVisibility(View.GONE);
                        iv4.setVisibility(View.GONE);
                        iv5.setVisibility(View.GONE);
                        iv6.setVisibility(View.GONE);
                        iv7.setVisibility(View.GONE);
                        iv8.setVisibility(View.GONE);
                        iv9.setVisibility(View.GONE);
                        judgeList();
                    } else {
                        newList.clear();
                        iv1.setVisibility(View.VISIBLE);
                        iv1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        iv1.setImageResource(R.mipmap.add_pick);
                        iv2.setVisibility(View.GONE);
                        iv3.setVisibility(View.GONE);
                        iv4.setVisibility(View.GONE);
                        iv5.setVisibility(View.GONE);
                        iv6.setVisibility(View.GONE);
                        iv7.setVisibility(View.GONE);
                        iv8.setVisibility(View.GONE);
                        iv9.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    private void judgeList() {
        if (newList.size() == 0) {
            iv1.setVisibility(View.VISIBLE);
            iv1.setImageResource(R.mipmap.add_pick);
            iv1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else if (newList.size() == 1) {
            iv1.setVisibility(View.VISIBLE);
            for (int i = 0; i < newList.size(); i++) {
                String s = newList.get(0);
                Glide.with(PublishActivity.this).load(s).into(iv1);
                iv1.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            iv2.setVisibility(View.VISIBLE);
            iv2.setImageResource(R.mipmap.add_pick);
            iv2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else if (newList.size() == 2) {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv3.setVisibility(View.VISIBLE);
            iv3.setImageResource(R.mipmap.add_pick);
            iv3.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            for (int i = 0; i < newList.size(); i++) {
                String s = newList.get(i);
                if (i == 0) {
                    Glide.with(PublishActivity.this).load(s).into(iv1);
                    iv1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 1) {
                    Glide.with(PublishActivity.this).load(s).into(iv2);
                    iv2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }

        } else if (newList.size() == 3) {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv3.setVisibility(View.VISIBLE);
            iv4.setVisibility(View.VISIBLE);
            iv4.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iv4.setImageResource(R.mipmap.add_pick);
            for (int i = 0; i < newList.size(); i++) {
                String s = newList.get(i);
                if (i == 0) {
                    Glide.with(PublishActivity.this).load(s).into(iv1);
                    iv1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 1) {
                    Glide.with(PublishActivity.this).load(s).into(iv2);
                    iv2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 2) {
                    Glide.with(PublishActivity.this).load(s).into(iv3);
                    iv3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }

        } else if (newList.size() == 4) {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv3.setVisibility(View.VISIBLE);
            iv4.setVisibility(View.VISIBLE);
            iv5.setVisibility(View.VISIBLE);
            iv5.setImageResource(R.mipmap.add_pick);
            iv5.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            for (int i = 0; i < newList.size(); i++) {
                String s = newList.get(i);
                if (i == 0) {
                    Glide.with(PublishActivity.this).load(s).into(iv1);
                    iv1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 1) {
                    Glide.with(PublishActivity.this).load(s).into(iv2);
                    iv2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 2) {
                    Glide.with(PublishActivity.this).load(s).into(iv3);
                    iv3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 3) {
                    Glide.with(PublishActivity.this).load(s).into(iv4);
                    iv4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }

        } else if (newList.size() == 5) {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv3.setVisibility(View.VISIBLE);
            iv4.setVisibility(View.VISIBLE);
            iv5.setVisibility(View.VISIBLE);
            iv6.setVisibility(View.VISIBLE);
            iv6.setImageResource(R.mipmap.add_pick);
            iv6.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            for (int i = 0; i < newList.size(); i++) {
                String s = newList.get(i);
                if (i == 0) {
                    Glide.with(PublishActivity.this).load(s).into(iv1);
                    iv1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 1) {
                    Glide.with(PublishActivity.this).load(s).into(iv2);
                    iv2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 2) {
                    Glide.with(PublishActivity.this).load(s).into(iv3);
                    iv3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 3) {
                    Glide.with(PublishActivity.this).load(s).into(iv4);
                    iv4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 4) {
                    Glide.with(PublishActivity.this).load(s).into(iv5);
                    iv5.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        } else if (newList.size() == 6) {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv3.setVisibility(View.VISIBLE);
            iv4.setVisibility(View.VISIBLE);
            iv5.setVisibility(View.VISIBLE);
            iv6.setVisibility(View.VISIBLE);
            iv7.setVisibility(View.VISIBLE);
            iv7.setImageResource(R.mipmap.add_pick);
            iv7.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            for (int i = 0; i < newList.size(); i++) {
                String s = newList.get(i);
                if (i == 0) {
                    Glide.with(PublishActivity.this).load(s).into(iv1);
                    iv1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 1) {
                    Glide.with(PublishActivity.this).load(s).into(iv2);
                    iv2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 2) {
                    Glide.with(PublishActivity.this).load(s).into(iv3);
                    iv3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 3) {
                    Glide.with(PublishActivity.this).load(s).into(iv4);
                    iv4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 4) {
                    Glide.with(PublishActivity.this).load(s).into(iv5);
                    iv5.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 5) {
                    Glide.with(PublishActivity.this).load(s).into(iv6);
                    iv6.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }

        } else if (newList.size() == 7) {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv3.setVisibility(View.VISIBLE);
            iv4.setVisibility(View.VISIBLE);
            iv5.setVisibility(View.VISIBLE);
            iv6.setVisibility(View.VISIBLE);
            iv7.setVisibility(View.VISIBLE);
            iv8.setVisibility(View.VISIBLE);
            iv8.setImageResource(R.mipmap.add_pick);
            iv8.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            for (int i = 0; i < newList.size(); i++) {
                String s = newList.get(i);
                if (i == 0) {
                    iv1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(PublishActivity.this).load(s).into(iv1);
                } else if (i == 1) {
                    Glide.with(PublishActivity.this).load(s).into(iv2);
                    iv2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 2) {
                    Glide.with(PublishActivity.this).load(s).into(iv3);
                    iv3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 3) {
                    Glide.with(PublishActivity.this).load(s).into(iv4);
                    iv4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 4) {
                    Glide.with(PublishActivity.this).load(s).into(iv5);
                    iv5.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 5) {
                    Glide.with(PublishActivity.this).load(s).into(iv6);
                    iv6.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 6) {
                    Glide.with(PublishActivity.this).load(s).into(iv7);
                    iv7.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }

        } else if (newList.size() == 8) {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv3.setVisibility(View.VISIBLE);
            iv4.setVisibility(View.VISIBLE);
            iv5.setVisibility(View.VISIBLE);
            iv6.setVisibility(View.VISIBLE);
            iv7.setVisibility(View.VISIBLE);
            iv8.setVisibility(View.VISIBLE);
            iv9.setVisibility(View.VISIBLE);
            iv9.setImageResource(R.mipmap.add_pick);
            iv9.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            for (int i = 0; i < newList.size(); i++) {
                String s = newList.get(i);
                if (i == 0) {
                    Glide.with(PublishActivity.this).load(s).into(iv1);
                    iv1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 1) {
                    Glide.with(PublishActivity.this).load(s).into(iv2);
                    iv2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 2) {
                    Glide.with(PublishActivity.this).load(s).into(iv3);
                    iv3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 3) {
                    Glide.with(PublishActivity.this).load(s).into(iv4);
                    iv4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 4) {
                    Glide.with(PublishActivity.this).load(s).into(iv5);
                    iv5.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 5) {
                    Glide.with(PublishActivity.this).load(s).into(iv6);
                    iv6.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 6) {
                    Glide.with(PublishActivity.this).load(s).into(iv7);
                    iv7.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 7) {
                    Glide.with(PublishActivity.this).load(s).into(iv8);
                    iv8.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }

        } else if (newList.size() == 9) {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv3.setVisibility(View.VISIBLE);
            iv4.setVisibility(View.VISIBLE);
            iv5.setVisibility(View.VISIBLE);
            iv6.setVisibility(View.VISIBLE);
            iv7.setVisibility(View.VISIBLE);
            iv8.setVisibility(View.VISIBLE);
            iv9.setVisibility(View.VISIBLE);
            for (int i = 0; i < newList.size(); i++) {
                String s = newList.get(i);
                if (i == 0) {
                    iv1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(PublishActivity.this).load(s).into(iv1);
                } else if (i == 1) {
                    iv2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(PublishActivity.this).load(s).into(iv2);
                } else if (i == 2) {
                    Glide.with(PublishActivity.this).load(s).into(iv3);
                    iv3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 3) {
                    Glide.with(PublishActivity.this).load(s).into(iv4);
                    iv4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 4) {
                    Glide.with(PublishActivity.this).load(s).into(iv5);
                    iv5.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 5) {
                    Glide.with(PublishActivity.this).load(s).into(iv6);
                    iv6.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 6) {
                    Glide.with(PublishActivity.this).load(s).into(iv7);
                    iv7.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 7) {
                    Glide.with(PublishActivity.this).load(s).into(iv8);
                    iv8.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (i == 8) {
                    Glide.with(PublishActivity.this).load(s).into(iv9);
                    iv9.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        }
    }

    //发布内容
    private void publishContent() {

        String trim = etTheme.getText().toString().trim();
        if (trim != null && trim.length() > 0) {
            theme = trim;
        }
        String trim1 = etPublish.getText().toString().trim();
        if (trim1 != null && trim1.length() > 0) {
            publish = trim1;
        }

        new Thread(this::addContentAndPhoto).start();
    }

    private String sendImageMsg(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        String replace = base64.replace("\n", "");
        return replace.replace("+", "%2B");
    }

    //内容和图片
    private void addContentAndPhoto() {
        if (typeId != null && typeId.length() > 0) {
            if (newList != null && newList.size() > 0) {
                net();
            } else {
                if (!TextUtils.isEmpty(publish))
                    net();
                 else
                    ToastUtil.showToast("请输入内容");

            }
        } else {
            ToastUtil.showToast("请选择类型");
        }
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

    public void net() {
        String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"typeId\":\"" + typeId + "\", \"title\":\"" + theme + "\", \"content\":\"" + publish + "\"}").getBytes(), Base64.DEFAULT);
        String replace = base64.replace("\n", "");
        String content = NetManager.getInstance().getContent(replace, "121");
        if (content != null && content.length() > 0) {
            PublishResultBean publishResultBean = GsonUtil.parseJsonToBean(content, PublishResultBean.class);
            if (publishResultBean != null && publishResultBean.getStatus() == 0) {
                String taskId = publishResultBean.getTaskId();
                if (newList != null && newList.size() > 0) {
                    uploadingPhoto(taskId);
                } else {
                    Sputil.saveBoolean(PublishActivity.this, "isSuccess", true);
                    PublishActivity.this.finish();
                }
            } else {
                ToastUtil.showToast("发送失败");
            }
        }
    }

    private void uploadingPhoto(final String taskId) {
        Util.uiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = ProgressDialog.show(PublishActivity.this, null, "正在上传...请等待");
            }
        });
        for (int i = 0; i < newList.size(); i++) {
            File file = new File(newList.get(i));
            String path = file.getPath();
            String photoEnd = "png";
            if (path.endsWith(".jpg")) {
                photoEnd = "jpg";
            } else if (path.endsWith(".png")) {
                photoEnd = "png";
            }
            String imageMsg = sendImageMsg(path);
            postPhoto(taskId, imageMsg, photoEnd);
        }
        Sputil.saveBoolean(PublishActivity.this, "isSuccess", true);
        progressDialog.dismiss();
        PublishActivity.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backPublish();
        }
        return false;
    }

    private void postPhoto(String taskId, String imageMsg, String photoEnd) {

        String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\",\"taskId\":\"" + taskId + "\", \"iconString\":\"" + imageMsg + "\", \"imageType\":\"" + photoEnd + "\", \"sum\":\"" + newList.size() + "\" }").getBytes(), Base64.DEFAULT);
        String replace = base64.replace("\n", "");
        String content = NetManager.getInstance().getContent(replace, "122");
        if (content != null && content.length() > 0) {
            ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
            if (returnStatus != null && returnStatus.getStatus() == 0) {

            } else {
                Util.uiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
                ToastUtil.showToast("上传失败");
            }
        } else {
            Util.uiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
            ToastUtil.showToast("上传失败");
        }
    }
}
