package com.neworld.youyou.adapter;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.neworld.youyou.R;
import com.neworld.youyou.activity.AddChildActivity;
import com.neworld.youyou.activity.PhotoActivity;
import com.neworld.youyou.activity.PhotoDetailActivity;
import com.neworld.youyou.bean.ChildBean2;
import com.neworld.youyou.bean.PublishResultBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.fragment.BaseFragment;
import com.neworld.youyou.fragment.ChildDetailSchoolFragment;
import com.neworld.youyou.fragment.ChildGenderFragment;
import com.neworld.youyou.fragment.ChildNameFragment;
import com.neworld.youyou.fragment.FragmentBackHandler;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.select.ImageSelectorUtils;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;
import com.neworld.youyou.utils.Validator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tt on 2017/8/14.
 */

public class ChildDetailFragment extends BaseFragment implements View.OnClickListener, FragmentBackHandler {

    private static final int REQUEST_CODE = 1000;
    private ImageView ivCancel;
    private TextView tvChildName;
    private TextView tvSave;
    private RelativeLayout rlChildName;
    private TextView tvName;
    private RelativeLayout rlChildGender;
    private TextView tvChildGender;
    private RelativeLayout rlChildBirth;
    private TextView tvChildBirth;
    private RelativeLayout rlChildSchool;
    private TextView tvChildSchool;
    private AddChildActivity activity;
    private TimePickerView pvTime;
    private int babyId;
    private String name = "";
    private String gender = "";
    private String birth = "";
    private String school = "";
    private String userId;
    private Dialog dialog;
    private RelativeLayout mAdd_child_photo;
    private ImageView mFragmet_child_iv_photo;
    private EditText mAdd_child_id_card;
    private ProgressDialog progressDialog;


    @Override
    public View createView() {
        activity = ((AddChildActivity) getActivity());
        View view = View.inflate(context, R.layout.fragment_child_name, null);
        ivCancel = view.findViewById(R.id.add_child_cancel);
        tvChildName = view.findViewById(R.id.add_child_my_name);
        tvSave = view.findViewById(R.id.add_child_save);
        rlChildName = view.findViewById(R.id.add_child_name);
        tvName = view.findViewById(R.id.tv_add_name);
        rlChildGender = view.findViewById(R.id.add_child_gender);
        tvChildGender = view.findViewById(R.id.tv_add_gender);
        rlChildBirth = view.findViewById(R.id.add_child_birth);
        tvChildBirth = view.findViewById(R.id.tv_add_birth);
        rlChildSchool = view.findViewById(R.id.add_child_school);
        tvChildSchool = view.findViewById(R.id.tv_add_school);
        //添加图片
        mAdd_child_photo = view.findViewById(R.id.add_child_photo);
        mFragmet_child_iv_photo = view.findViewById(R.id.fragmet_child_iv_photo);//点击图片查看原图
        mAdd_child_id_card = view.findViewById(R.id.add_child_ID_card); //添加身份证号码
        initUser();
        initView();
        return view;
    }

    private void initUser() {
        userId = Sputil.getString(context, "userId", "");
    }

    private void initView() {
        ivCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        rlChildName.setOnClickListener(this);
        rlChildGender.setOnClickListener(this);
        rlChildSchool.setOnClickListener(this);
        rlChildBirth.setOnClickListener(this);
        mAdd_child_photo.setOnClickListener(this);
//        mFragmet_child_iv_photo.setOnClickListener(this);
//        mFragmet_child_iv_photo.setOnClickListener(this);

        initTimePicker();
        iniData();
    }

    @Override
    public void onResume() {
        super.onResume();
        setChildMsg();
    }

    private void setChildMsg() {
        //设置名字
        if (activity.name != null) {
            tvName.setText(activity.name);
        } else {
            tvName.setText("");
        }
        //s设置性别
        if (activity.gender != null) {
            tvChildGender.setText(activity.gender);
        } else {
            tvChildGender.setText("");
        }
        //设置出生日
        if (activity.birth != null) {
            tvChildBirth.setText(activity.birth);
        } else {
            tvChildBirth.setText("");
        }
        //设置学校
     /*   if (activity.school != null) {
            tvChildSchool.setText(activity.school);
        } else {
            tvChildSchool.setText("");
        }*/
        if (activity.junior != null && activity.junior.length() > 0) {
            tvChildSchool.setText(activity.junior);
        } else if (activity.primary != null && activity.primary.length() > 0) {
            tvChildSchool.setText(activity.primary);
        } else if (activity.kindergarten != null && activity.kindergarten.length() > 0) {
            tvChildSchool.setText(activity.kindergarten);
        }
    }

    @Override
    public Object getData() {
        return "";
    }

    private void iniData() {
        if (babyId != 0) {
            new Thread(() -> {
                String base64 = Base64.encodeToString(("{\"babyId\":\"" + babyId + "\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "142");
                if (content != null && content.length() > 0) {
                    final ChildBean2 childBean = GsonUtil.parseJsonToBean(content, ChildBean2.class);
                    if (childBean != null && childBean.getStatus() == 0) {
                        Util.uiThread(new Runnable() {
                            @Override
                            public void run() {
                                setData(childBean);
                            }
                        });
                    } else {
                        ToastUtil.showToast("网络异常");
                    }
                } else {
                    ToastUtil.showToast("网络异常");
                }
            }).start();
        }
    }

    //设置属性
    private void setData(ChildBean2 childBean) {
        ChildBean2.ResultsBean results = childBean.getResults();
        if (results != null) {
            setStudentMsg(results);
        }
        List<ChildBean2.BabyListBean> babyList = childBean.getBabyList();
        if (babyList != null && babyList.size() > 0) {
            setStudentSchool(babyList);
        }
    }

    //设置学校
    private void setStudentSchool(List<ChildBean2.BabyListBean> babyList) {

        for (ChildBean2.BabyListBean babyListBean : babyList) {
            int type = babyListBean.getType();
            if (type == 0) {
                setKindergarten(babyListBean);
            } else if (type == 1) {
                setPrimary(babyListBean);
            } else if (type == 2) {
                setJunior(babyListBean);
            }
        }

        if (activity.junior != null && activity.junior.length() > 0) {
            tvChildSchool.setText(activity.junior);
        } else {
            if (activity.primary != null && activity.primary.length() > 0) {
                tvChildSchool.setText(activity.primary);
            } else {
                if (activity.kindergarten != null) {
                    tvChildSchool.setText(activity.kindergarten);
                } else {
                    tvChildSchool.setText("");
                }
            }
        }

    }

    private void setJunior(ChildBean2.BabyListBean babyListBean2) {
        if (babyListBean2 != null) {
            //设置初中地区
            String districtName = babyListBean2.getDistrictName();
            if (districtName != null) {
                activity.locationJunior = districtName;
            } else {
                activity.locationJunior = "";
            }

            //设置初中学校
            String schoolName = babyListBean2.getSchoolName();
            if (schoolName != null) {
                activity.junior = schoolName;
                activity.school = schoolName;
            } else {
                activity.junior = "";
            }
            this.school = schoolName;
            //tvChildSchool.setText(activity.junior);
            //s设置初中入学时间
            String intake_time = babyListBean2.getIntake_time();
            if (intake_time != null) {
                activity.timeJunior = intake_time;
            } else {
                activity.timeJunior = "";
            }
            //设置初中班级
            String grade_class = babyListBean2.getGrade_class();
            if (grade_class != null) {
                activity.gradeJunior = grade_class;
            } else {
                activity.gradeJunior = "";
            }
        }
    }

    private void setPrimary(ChildBean2.BabyListBean babyListBean1) {
        if (babyListBean1 != null) {
            //设置小学地区
            String districtName = babyListBean1.getDistrictName();
            if (districtName != null) {
                activity.locationPrimary = districtName;
            } else {
                activity.locationPrimary = "";
            }

            //设置小学学校
            String schoolName = babyListBean1.getSchoolName();
            if (schoolName != null) {
                activity.primary = schoolName;
                activity.school = schoolName;
            } else {
                activity.primary = "";
            }
            this.school = schoolName;
            //tvChildSchool.setText(schoolName);
            //s设置小学入学时间
            String intake_time = babyListBean1.getIntake_time();
            if (intake_time != null) {
                activity.timePrimary = intake_time;
            } else {
                activity.timePrimary = "";
            }
            //设置小学班级
            String grade_class = babyListBean1.getGrade_class();
            if (grade_class != null) {
                activity.gradePrimary = grade_class;
            } else {
                activity.gradePrimary = "";
            }
        }
    }

    private void setKindergarten(ChildBean2.BabyListBean babyListBean0) {
        if (babyListBean0 != null) {
            //设置幼儿园地区
            String districtName = babyListBean0.getDistrictName();
            if (districtName != null) {
                activity.locationKindergarten = districtName;
            } else {
                activity.locationKindergarten = "";
            }

            //设置幼儿园学校
            String schoolName = babyListBean0.getSchoolName();
            if (schoolName != null) {
                activity.kindergarten = schoolName;
                activity.school = schoolName;
            } else {
                activity.kindergarten = "";
            }
            this.school = schoolName;
            //tvChildSchool.setText(schoolName);
            //s设置幼儿园入学时间
            String intake_time = babyListBean0.getIntake_time();
            if (intake_time != null) {
                activity.timeKindergarten = intake_time;
            } else {
                activity.timeKindergarten = "";
            }
            //设置幼儿园班级
            String grade_class = babyListBean0.getGrade_class();
            if (grade_class != null) {
                activity.gradeKindergarten = grade_class;
            } else {
                activity.gradeKindergarten = "";
            }
        }
    }

    private String imguri;

    //设置基本信息
    private void setStudentMsg(ChildBean2.ResultsBean results) {
        String birthday = results.getBirthday();
        String name = results.getName();
        int sex = results.getSex();
        String cardID = results.getCardID(); //身份证
        String imgs = results.getImgs();
        //设置名字
        if (name != null && name.length() > 0) {
            tvName.setText(name);
            activity.name = name;
        } else {
            tvName.setText("");
        }
        //设置生日
        if (birthday != null && birthday.length() > 0) {
            tvChildBirth.setText(birthday);
            activity.birth = birthday;
        } else {
            tvChildBirth.setText("");
        }
        //设置性别
        if (sex == 0) {
            tvChildGender.setText("男");
            activity.gender = "男";
        } else {
            tvChildGender.setText("女");
            activity.gender = "女";
        }

        //设置身份证
        if (TextUtils.isEmpty(cardID)) {
            mAdd_child_id_card.setHint("请填写身份证号码");
        } else {
            mAdd_child_id_card.setHint(cardID);
        }
        //设置图片
        if (TextUtils.isEmpty(imgs)) {
            mFragmet_child_iv_photo.setVisibility(View.INVISIBLE);
        } else {
            mFragmet_child_iv_photo.setVisibility(View.VISIBLE);
            mFragmet_child_iv_photo.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with(this).load(imgs).into(mFragmet_child_iv_photo);
            imguri = imgs;
            if (newList != null) {
                newList.clear();
                newList.add(imgs);

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_child_cancel:
                returnData();
                break;
            case R.id.add_child_save:
                saveChildMes();
                break;
            case R.id.add_child_name:
                addChildName();
                break;
            case R.id.add_child_gender:
                if ((activity.name != null && activity.name.length() > 0)) {
                    addChildGender();
                } else {
                    ToastUtil.showToast("请输入名字");
                }
                break;
            case R.id.add_child_birth:
                if ((activity.name != null && activity.name.length() > 0)) {
                    if ((activity.gender != null && activity.gender.length() > 0)) {
                        if (pvTime != null) {
                            pvTime.show(v);
                        }
                    } else {
                        ToastUtil.showToast("请选择性别");
                    }
                } else {
                    ToastUtil.showToast("请输入名字");
                }

                break;
            case R.id.add_child_school:
                if ((activity.name != null && activity.name.length() > 0)) {
                    if ((activity.gender != null && activity.gender.length() > 0)) {
                        if ((tvChildBirth != null && tvChildBirth.length() > 0)) {
                            addChildSchool();
                        } else {
                            ToastUtil.showToast("请选择出生日期");
                        }
                    } else {
                        ToastUtil.showToast("请选择性别");
                    }
                } else {
                    ToastUtil.showToast("请输入名字");
                }
                break;
            case R.id.choosePhoto:
                ImageSelectorUtils.openPhoto(activity, REQUEST_CODE, false, 1);
                dialog.dismiss();
                break;
            case R.id.takePhoto:
                takePhoto();
                dialog.dismiss();
                break;
            case R.id.cancel:
                dialog.dismiss();
                break;
            case R.id.add_child_photo: //添加照片
                //// TODO: 2017/9/26
                if (TextUtils.isEmpty(tvName.getText())) {
                    ToastUtil.showToast("请先添加名字~");
                    break;
                }
                Intent intent = new Intent();
                intent.setClass(context, PhotoActivity.class);
                Bundle bundle = new Bundle();
                if (imguri != null && imguri.length() > 0) {
                    bundle.putString("imageUrl", imguri);
                } else {
                    bundle.putString("imageUrl", "");
                }
                intent.putExtras(bundle);
                intent.putExtra("child", true);
                intent.putExtra("babyId", babyId);
                startActivityForResult(intent, CAMERA_PHOTO);
                break;
            case R.id.fragmet_child_iv_photo: //点击查看图片
                ToastUtil.showToast("查看图片");

                Intent intentImage = new Intent();
                intentImage.setClass(context, PhotoDetailActivity.class);

                Bundle bundleSimple = new Bundle();
                bundleSimple.putSerializable("imageList", (Serializable) newList);
                bundleSimple.putInt("currentPosition", 0);
                bundleSimple.putString("FromActivity", "ChildDetailFragment");
                intentImage.putExtras(bundleSimple);
                context.startActivity(intentImage);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_PHOTO:
                if (data != null) {
                    boolean isRefresh = data.getBooleanExtra("isRefresh", false);
                    if (isRefresh) {
                        // TODO : GetData
                        new Thread(this::iniData).start();
                    }
                }
                break;
        }
    }

    private final int CAMERA_PHOTO = 22;

    /**
     * 调用系统相机拍摄照片
     */
    private File photoFile;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_PERMISSION_CAMERA = 10001;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;

    private void takePhoto() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        photoFile = new File(directory, new Date().getTime() + "Youyou.jpg");
        //file://storage/DCIM/148xxxxxxx.jpg
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

//    /**
//     * 调用系统相机拍摄照片
//     */
//    private void takePhoto() {
//
//        //权限判断
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            //申请WRITE_EXTERNAL_STORAGE权限
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
//        } else {
//            //跳转到调用系统相机
//            gotoCamera();
//        }
//    }

    //返回
    private void returnData() {
        String s = tvName.getText().toString();
        if (tvName.getText().toString() == "" && tvChildGender.getText().toString() == "" && tvChildBirth.getText().toString() == "" && tvChildSchool.getText().toString() == "") {
            isFinsh();
        } else {
            if (babyId != 0) {
                isFinsh();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setCancelable(false);
                dialog.setMessage("信息未保存，确认返回");
                dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setNeutralButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        returnMes();
                        dialog.dismiss();
                        activity.gender = "";
                        activity.name = "";
                        activity.birth = "";
                        activity.school = "";
                        activity.babyId = 0;
                    }
                });
                dialog.show();
            }
        }
    }

    //确认返回信息
    private void returnMes() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int babyId = activity.babyId;
                String base64 = Base64.encodeToString(("{\"babyId\":\"" + babyId + "\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "145");
                if (content != null && content.length() > 0) {
                    ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                    if (returnStatus.getStatus() == 0) {
                        //删除成功
                        //activity.isAddDelete = true;
                        Util.uiThread(() -> isFinsh());
                    }
                }
            }
        }).start();
    }

    //添加孩子学校
    private void addChildSchool() {
        activity.changePage(new ChildDetailSchoolFragment());
    }

    //添加孩子性别
    private void addChildGender() {
        activity.changePage(new ChildGenderFragment());
    }

    //添加孩子名字
    private void addChildName() {
        ChildNameFragment childNameFragment = new ChildNameFragment();
        childNameFragment.setBabyId(babyId);
        childNameFragment.setObserver(new ChildNameFragment.RequestOb() {
            @Override
            public void onResponse(int babyId) {
                ChildDetailFragment.this.babyId = babyId;
            }
        });
        activity.changePage(childNameFragment);
    }

    //保存孩子信息
    private void saveChildMes() {
        if (activity.name != null && activity.name.length() > 0 && activity.gender != null && activity.gender.length() > 0
                && tvChildBirth.getText() != null && tvChildBirth.getText().toString().length() > 0 && activity.school != null && activity.school.length() > 0) {
            //判断是否需要保存照片
            isSavePhoto();

        } else {
            ToastUtil.showToast("信息不全，请完善");
        }
    }

    //时间
    private void initTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(1996, 12, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2017, 11, 31);
        //时间选择器
        pvTime = new TimePickerView.Builder(context, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                /*btn_Time.setText(getTime(date));*/
                tvChildBirth.setText(getTime(date));
                setNetData(getTime(date));
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(Color.parseColor("#e7e7e7"))
                .setContentSize(19)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();
    }

    private void setNetData(final String time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (activity.babyId != 0) {
                    String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\",\"name\":\"\", \"sex\":\"\",\"babyId\":\"" + activity.babyId + "\", \"birthday\":\"" + time + "\"}").getBytes(), Base64.DEFAULT);
                    String replace = base64.replace("\n", "");
                    String content = NetManager.getInstance().getContent(replace, "140");
                    if (content != null && content.length() > 0) {
                        activity.birth = time;
                    }
                }
            }
        }).start();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public boolean onBackPressed() {
        returnDataBack();
        if (isBack == 1) {
            //return BackHandlerHelper.handleBackPress(this);
            return false;
        }
        return true;
    }

    private int isBack = 1000;

    //返回
    private void returnDataBack() {
        String s = tvName.getText().toString();
        if (tvName.getText().toString() == "" && tvChildGender.getText().toString() == "" && tvChildBirth.getText().toString() == "" && tvChildSchool.getText().toString() == "") {
            activity.popBackStack();
        } else {
            if (babyId != 0) {
                activity.popBackStack();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setCancelable(false);
                dialog.setMessage("信息未保存，确认返回");
                dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isBack = 2;
                        dialog.dismiss();
                    }
                });
                dialog.setNeutralButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isBack = 1;
                        returnMes();
                        dialog.dismiss();
                        activity.gender = "";
                        activity.name = "";
                        activity.birth = "";
                        activity.school = "";
                        activity.babyId = 0;
                    }
                });
                dialog.show();
            }
        }
    }

    private void addBlackName() {
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_photo, null);

        TextView choosePhoto = (TextView) inflate.findViewById(R.id.choosePhoto);
        TextView takePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
        TextView savePhoto = (TextView) inflate.findViewById(R.id.save_photo);
        TextView cancel = (TextView) inflate.findViewById(R.id.cancel);
        initDialog(dialog, inflate);
        savePhoto.setVisibility(View.GONE);
        choosePhoto.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        cancel.setOnClickListener(this);
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
        WindowManager windowManager = activity.getWindowData();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        lp.width = (int) (defaultDisplay.getWidth());
        //lp.y = 20;//设置Dialog距离底部的距离
        //       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    private ArrayList<String> newList = new ArrayList<>();


    private void judgeList() {
        if (newList.size() == 0) {

        } else if (newList.size() >= 1) {
            mFragmet_child_iv_photo.setVisibility(View.VISIBLE);

            String s = newList.get(0);
            Glide.with(this).load(s).into(mFragmet_child_iv_photo);
            mFragmet_child_iv_photo.setScaleType(ImageView.ScaleType.CENTER_CROP);

        }
    }

    public void isSavePhoto() {
        progressDialog = ProgressDialog.show(activity, null, "正在上传...请等待");
        final String IDCard = mAdd_child_id_card.getText().toString().trim();
        if (!TextUtils.isEmpty(IDCard)) {
            if (Validator.isIDCard(IDCard)) {
                //网络上传
                new Thread(() -> net(IDCard)).start();

            } else {
                ToastUtil.showToast("身份证号码有误");
                progressDialog.dismiss();
                mAdd_child_id_card.requestFocus();
            }

        } else {
            //判断是否上传图片
            if (newList.size() != 0 && newList.size() > 0) {
                if (!newList.get(0).equals(imguri)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadingPhoto();
                        }
                    }).start();
                } else {
                    progressDialog.dismiss();
                    isFinsh();
                }

            } else {
                progressDialog.dismiss();
                isFinsh();

            }
        }

    }

    //设置图片
    public void savePhotoView(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> stringArrayListExtra = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
                    if (stringArrayListExtra != null && stringArrayListExtra.size() > 0) {
                        String s = stringArrayListExtra.get(stringArrayListExtra.size() - 1);
                        newList.clear();
                        newList.add(stringArrayListExtra.get(stringArrayListExtra.size() - 1));
                        judgeList();
                    }
                }
                break;
            case REQUEST_CAMERA: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    if (photoFile != null) {
                        String path = photoFile.getPath();
                        if (path != null && path.length() > 0) {
                            newList.clear();
                            newList.add(path);
                            judgeList();
                        }
                    }
                } else {
                    ToastUtil.showToast("没有照相");
                }
                break;


        }

    }


    public void net(String cardId) {
        String base64 = Base64.encodeToString(("{\"cardID\":\"" + cardId + "\",\"userId\":\"" + userId + "\", \"name\":\"" + "" + "\", \"sex\":\"" + "" + "\", \"birthday\":\"" + "" + "\", \"babyId\":\"" + babyId + "\" }").getBytes(), Base64.DEFAULT);
        String replace = base64.replace("\n", "");
        String content = NetManager.getInstance().getContent(replace, "140");
        if (content != null && content.length() > 0) {
            PublishResultBean publishResultBean = GsonUtil.parseJsonToBean(content, PublishResultBean.class);
            if (publishResultBean != null && publishResultBean.getStatus() == 0) {
                ToastUtil.showToast("添加成功");
            } else {
                ToastUtil.showToast("身份证添加失败");
            }
        }
        //判断是否上传图片
        if (newList.size() > 0) if (!newList.get(0).equals(imguri))
            uploadingPhoto();

        Util.uiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                isFinsh();
            }
        });
    }

    private void uploadingPhoto() {

        File file = new File(newList.get(0));
        String path = file.getPath();
        String photoEnd = "png";
        if (path.endsWith(".jpg")) {
            photoEnd = "jpg";
        } else if (path.endsWith(".png")) {
            photoEnd = "png";
        }
        if (!TextUtils.isEmpty(path)) {
            String imageMsg = sendImageMsg(path);
            postPhoto(imageMsg, photoEnd);
        }
        progressDialog.dismiss();
    }

    private void postPhoto(String imageMsg, String photoEnd) {
        String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"iconString\":\"" + imageMsg + photoEnd + "\", \"imageType\":\"" + "" + "\", \"babyId\":\"" + babyId + "\" }").getBytes(), Base64.DEFAULT);
        String replace = base64.replace("\n", "");
        String content = NetManager.getInstance().getContent(replace, "141_1");
        System.out.println(content);
        if (content != null && content.length() > 0) {
            ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
            if (returnStatus != null && returnStatus.getStatus() == 0) {

            } else {

                ToastUtil.showToast("上传图片失败");
            }
        } else {

            ToastUtil.showToast("上传图片失败(out)");
        }

        Util.uiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                isFinsh();
            }
        });

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

    public void isFinsh() {
        Intent intent = activity.getIntent();
        if (intent.getBooleanExtra("pay", false) || intent.getBooleanExtra("edit", false))
            activity.finish();

        else activity.popBackStack();

    }

    public void setBybyId(int bybyId) {
        this.babyId = bybyId;
    }


}


