package com.neworld.youyou.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.neworld.youyou.R;
import com.neworld.youyou.activity.AddChildActivity;
import com.neworld.youyou.bean.AddClassBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.bean.SchoolNameBean;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tt on 2017/8/15.
 */

public class PrimaryFragment extends BaseFragment implements View.OnClickListener, FragmentBackHandler {

    private ImageView ivCancel;
    private TextView tvChildTitle;
    private AddChildActivity activity;
    private RelativeLayout rlChildLocation;
    private TextView tvChildLocation;
    private RelativeLayout rlChildSchool;
    private TextView tvChildSchool;
    private RelativeLayout rlChildLevel;
    private TextView tvChildLevel;
    private RelativeLayout rlChildTime;
    private TextView tvChildTime;
    private String dataArray [] = {"浦东新区","徐汇区","长宁区","普陀区","黄浦区","静安区","虹口区","杨浦区","闵行区","宝山区","嘉定区","金山区","松江区","青浦区","奉贤区","崇明区"};
    private OptionsPickerView pvOptionsQu;
    private OptionsPickerView pvOptionsSchool;
    private OptionsPickerView pvOptionsTime;
    private OptionsPickerView pvOptionsLevel;
    private List<SchoolNameBean.MenuListBean> menuList = new ArrayList<>();
    private  List<String> mDate = new ArrayList<>();
    private TextView tvSave;
    private List<AddClassBean.MenuListBean> classMenuList = new ArrayList<>();
    private String userId;

    @Override
    public View createView() {
        View view = View.inflate(context, R.layout.fragment_school_primary, null);
        ivCancel = ((ImageView) view.findViewById(R.id.add_child_cancel));
        tvChildTitle = ((TextView) view.findViewById(R.id.add_child_my_name));
        activity = ((AddChildActivity) getActivity());
        rlChildLocation = ((RelativeLayout) view.findViewById(R.id.add_child_location));
        tvChildLocation = ((TextView) view.findViewById(R.id.tv_add_location));
        rlChildSchool = ((RelativeLayout) view.findViewById(R.id.add_child_school));
        tvChildSchool = ((TextView) view.findViewById(R.id.tv_add_school));
        rlChildLevel = ((RelativeLayout) view.findViewById(R.id.add_child_level));
        tvChildLevel = ((TextView) view.findViewById(R.id.tv_add_level));
        rlChildTime = ((RelativeLayout) view.findViewById(R.id.add_child_time));
        tvChildTime = ((TextView) view.findViewById(R.id.tv_add_time));
        tvSave = ((TextView) view.findViewById(R.id.add_child_save));
        initUser();
        setKindergratenMeg();
        initView();
        return view;
    }

    private void initUser() {
        userId = Sputil.getString(context, "userId", "");
    }

    @Override
    public void onResume() {
        super.onResume();
        setKindergratenMeg();
    }

    private void setKindergratenMeg() {
    /*    if (tvChildSchool.getText().toString().trim() != null && tvChildSchool.length() > 0) {

        } else {*/
            if (activity.primary != null && activity.primary.length() > 0) {
                tvChildSchool.setText(activity.primary);
            } else {
         //       tvChildSchool.setText("");
            }
       // }

       /* if (tvChildLocation.getText().toString().trim() != null && tvChildLocation.length() > 0) {

        } else {*/
            if (activity.locationPrimary != null && activity.locationPrimary.length() > 0) {
                tvChildLocation.setText(activity.locationPrimary);
            } else {
                //tvChildLocation.setText("");
            }
       // }

      /* if (tvChildTime.getText().toString().trim() != null && tvChildTime.length() > 0) {

       } else {*/
           if (activity.timePrimary != null && activity.timePrimary.length() > 0 ) {
               tvChildTime.setText(activity.timePrimary);
           } else {
        //       tvChildTime.setText("");
           }
       //}

    /*    if (tvChildLevel.getText().toString().trim() != null && tvChildLevel.length() > 0) {

        } else {*/
            if (activity.gradePrimary != null && activity.gradePrimary.length() > 0) {
                tvChildLevel.setText(activity.gradePrimary);
            } else {
         //       tvChildLevel.setText("");
            }
       // }


    }

    private void initView() {
        ivCancel.setOnClickListener(this);
        rlChildLocation.setOnClickListener(this);
        rlChildSchool.setOnClickListener(this);
        rlChildTime.setOnClickListener(this);
        rlChildLevel.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        initSchoolNet();
        initOptionPicker();
        initLevelNet();
        initTime();
    }

    private void initLevelNet() {
        if (tvChildSchool.getText().toString().trim() != null && tvChildSchool.getText().toString().trim().length() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String base64 = Base64.encodeToString(("{\"type\":\"1\", \"schoolName\":\""+tvChildSchool.getText().toString().trim()+"\"}").getBytes(), Base64.DEFAULT);
                    String replace = base64.replace("\n", "");
                    String content = NetManager.getInstance().getContent(replace, "167");
                    if (content != null && content.length() > 0) {
                        AddClassBean addClassBean = GsonUtil.parseJsonToBean(content, AddClassBean.class);
                        if (addClassBean != null && addClassBean.getStatus() == 0) {
                            classMenuList.clear();
                            classMenuList = addClassBean.getMenuList();
                            Util.uiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initLevel(classMenuList);
                                }
                            });
                        }
                    }
                }
            }).start();
        }
    }

    private void initSchoolNet() {
        if (tvChildLocation.getText().toString().trim() != null && tvChildLocation.getText().toString().trim().length() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String base64 = Base64.encodeToString(("{\"type\":\"1\", \"districtName\":\""+tvChildLocation.getText().toString().trim()+"\"}").getBytes(), Base64.DEFAULT);
                    String replace = base64.replace("\n", "");
                    String content = NetManager.getInstance().getContent(replace, "166");
                    Log.e("content", content + "");
                    if (content != null && content.length() > 0) {
                        SchoolNameBean schoolNameBean = GsonUtil.parseJsonToBean(content, SchoolNameBean.class);
                        if (schoolNameBean != null && schoolNameBean.getStatus() == 0) {
                            menuList.clear();
                            menuList = schoolNameBean.getMenuList();
                            initSchool();
                        }
                    }
                }
            }).start();
        }
    }

    private void initTime() {
        for (int i = 2002; i < 2017; i++) {
            mDate.add(i + "- 9");
        }
        initOptionTime();
    }

    @Override
    public Object getData() {

        return "";
    }

    private void initOptionPicker() {//条件选择器初始化
        final List<String> list = Arrays.asList(dataArray);
        pvOptionsQu = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String location = list.get(options1);
                if (location != null) {
                    tvChildLocation.setText(location);
                    //activity.locationPrimary = location;
                    if (activity.locationPrimary != location) {
                        tvChildSchool.setText("");
                        //tvChildTime.setText("");
                        tvChildLevel.setText("");
                    }
                    getSchool(location);
                }
            }
        })
                .setTitleText("城市选择")
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
        pvOptionsQu.setPicker(list);//二级选择器
    }


    private void initOptionTime() {//条件选择器初始化
        pvOptionsTime = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String s = mDate.get(options1);
                if (s != null && s.length() > 0) {
                    tvChildTime.setText(s);
                    //activity.timePrimary = s;
                }
            }
        })
                .setTitleText("城市选择")
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
        pvOptionsTime.setPicker(mDate);//二级选择器
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_child_cancel:
                jdegeBack();
                break;
            case R.id.add_child_location:
                pvOptionsQu.show();
                break;
            case R.id.add_child_school:
                if (tvChildLocation != null && tvChildLocation.length() > 0) {
                    if (pvOptionsSchool != null) {
                        pvOptionsSchool.show();
                    }
                } else {
                    ToastUtil.showToast("请选择区名");
                }

                break;
            case R.id.add_child_time:
                if (tvChildLocation != null && tvChildLocation.length() > 0) {
                    if (tvChildSchool != null && tvChildSchool.length() > 0) {
                        if (pvOptionsTime != null) {
                            pvOptionsTime.show();
                        }
                    } else {
                        ToastUtil.showToast("请选择学校");
                    }
                } else {
                    ToastUtil.showToast("请选择区名");
                }
                break;
            case R.id.add_child_level:
                if (tvChildLocation != null && tvChildLocation.length() > 0) {
                    if (tvChildSchool != null && tvChildSchool.length() > 0) {
                        if (tvChildTime != null && tvChildTime.length() > 0) {
                            if (pvOptionsLevel != null) {
                                pvOptionsLevel.show();
                            }
                        } else {
                            ToastUtil.showToast("请选择入园时间");
                        }
                    } else {
                        ToastUtil.showToast("请选择学校");
                    }
                } else {
                    ToastUtil.showToast("请选择区名");
                }

                break;
            case R.id.add_child_save:
                saveData();
                break;
        }
    }

    private void jdegeBack() {
        if (tvChildLocation.length() > 0 && tvChildSchool.length() > 0 && tvChildTime.length() > 0 && tvChildLevel.length() > 0) {
            tanDialog(false);
        } else if (tvChildLocation.length() == 0 && tvChildSchool.length() == 0 && tvChildTime.length() == 0 && tvChildLevel.length() == 0){
            activity.popBackStack();
        } else {
            tanDialog(true);
        }

     /*   if (activity.locationPrimary.length() > 0 && activity.primary.length() > 0 && activity.timePrimary.length() > 0 && activity.gradePrimary.length() > 0) {
            tanDialog(false);
        } else if (activity.locationPrimary.length() == 0 && activity.primary.length() == 0 && activity.timePrimary.length() == 0 && activity.gradePrimary.length() == 0){
            activity.popBackStack();
        } else {
            tanDialog(true);
        }*/
    }
    private int isBack = 100;
    private void tanDialog(final boolean isDelete) {
        final AlertDialog.Builder ad=new AlertDialog.Builder(activity);
        ad.setMessage("信息未保存，确认返回");
        ad.setPositiveButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isBack = 2;
                        dialog.dismiss();
                    }
                });
        ad.setNegativeButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isBack = 1;
                        if (isDelete) {
                            activity.locationPrimary= "";
                            activity.primary = "";
                            activity.timePrimary= "";
                            activity.gradePrimary= "";
                        }
                        dialog.dismiss();
                        activity.popBackStack();
                    }
                });
        ad.show();
    }

    private void saveData() {
        String location = tvChildLocation.getText().toString().trim();
        String time = tvChildTime.getText().toString().trim();
        String level = tvChildLevel.getText().toString().trim();
        String school = tvChildSchool.getText().toString().trim();
        if (!TextUtils.isEmpty(location)) {
            if (!TextUtils.isEmpty(school)) {
                if (!TextUtils.isEmpty(time)) {
                    if (!TextUtils.isEmpty(level)) {
                        saveNet(location, time, level, school);
                    } else {
                        ToastUtil.showToast("请选择班级");
                    }
                } else {
                    ToastUtil.showToast("请选择入学时间");
                }
            } else {
                ToastUtil.showToast("请选择学校");
            }
        } else {
            ToastUtil.showToast("请选择区");
        }
    }
    //保存到网络上面
    private void saveNet(final String location, final String time, final String level, final String school) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int babyId = activity.babyId;
                if (babyId != 0) {
                    String base64 = Base64.encodeToString(("{\"babySchoolId\":\"\",\"babyId\":\""+babyId
                            +"\",\"districtName\":\""+location+"\",\"intake_time\":\""+time
                            +"\",\"grade_type\":\""+level+"\", \"type\":\"1\", \"schoolName\":\""+school+"\"}").getBytes(), Base64.DEFAULT);
                    String replace = base64.replace("\n", "");
                    String content = NetManager.getInstance().getContent(replace, "170");
                    if (content != null && content.length() > 0) {
                        ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                        if (returnStatus != null && returnStatus.getStatus() == 0) {
                            Util.uiThread(new Runnable() {
                                @Override
                                public void run() {
                                    activity.school = school;
                                    activity.locationPrimary= location;
                                    activity.primary = school;
                                    activity.timePrimary= time;
                                    activity.gradePrimary= level;
                                    activity.popBackStack();
                                }
                            });
                        }
                    }
                }

            }
        }).start();
    }
    //从网络中获取学校
    private void getSchool(final String location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (location != null && location.length() > 0) {
                    String base64 = Base64.encodeToString(("{\"type\":\"1\", \"districtName\":\""+location+"\"}").getBytes(), Base64.DEFAULT);
                    String replace = base64.replace("\n", "");
                    String content = NetManager.getInstance().getContent(replace, "166");
                    if (content != null && content.length() > 0) {
                        SchoolNameBean schoolNameBean = GsonUtil.parseJsonToBean(content, SchoolNameBean.class);
                        if (schoolNameBean != null && schoolNameBean.getStatus() == 0) {
                            menuList.clear();
                            menuList = schoolNameBean.getMenuList();
                            initSchool();
                        }
                    }
                }

            }
        }).start();
    }
    //初始化学校
    private void initSchool() {
       if (menuList != null && menuList.size() > 0) {
           Util.uiThread(new Runnable() {
               @Override
               public void run() {
                   pvOptionsSchool = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
                       @Override
                       public void onOptionsSelect(int options1, int options2, int options3, View v) {
                           String pickerViewText = menuList.get(options1).getPickerViewText();
                           if (pickerViewText != null && pickerViewText.length() > 0) {
                               tvChildSchool.setText(pickerViewText);
                                getNetGrade(pickerViewText);
                           }
                       }
                   })
                           .setTitleText("学校选择")
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
                   pvOptionsSchool.setPicker(menuList);//二级选择器
               }
           });
       }
    }
    //从网络中获取班级
    private void getNetGrade(final String pickerViewText) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"type\":\"1\", \"schoolName\":\""+pickerViewText+"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "167");
                if (content != null && content.length() > 0) {
                    AddClassBean addClassBean = GsonUtil.parseJsonToBean(content, AddClassBean.class);
                    if (addClassBean != null && addClassBean.getStatus() == 0) {
                        classMenuList.clear();
                        classMenuList = addClassBean.getMenuList();
                        Util.uiThread(new Runnable() {
                            @Override
                            public void run() {
                                initLevel(classMenuList);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    //选择班级
    private void initLevel(final List<AddClassBean.MenuListBean> classMenuList) {
        if (classMenuList != null && classMenuList.size() > 0) {
            pvOptionsLevel = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    String s = classMenuList.get(options1).getPickerViewText();
                    if (s != null && s.length() > 0) {
                        if (s.equals("添加")) {
                            addMoreClass();
                        } else {
                            tvChildLevel.setText(s);
                            activity.gradePrimary = s;
                        }
                    }
                }
            })
                    .setTitleText("班级")
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
            pvOptionsLevel.setPicker(classMenuList);//二级选择器
        }

    }
    //添加更多班级
    private void addMoreClass() {
        AddClassFragment addClassFragment = new AddClassFragment();
        addClassFragment.setTag(1);
        activity.changePage(addClassFragment);


    }

    @Override
    public boolean onBackPressed() {
        jdegeBack();
        if (isBack == 1) {
            return false;
        }
        return true;
    }
}
