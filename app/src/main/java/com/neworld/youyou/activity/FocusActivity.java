package com.neworld.youyou.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.neworld.youyou.R;
import com.neworld.youyou.adapter.NewFocusAdapter;
import com.neworld.youyou.bean.FocusBean;
import com.neworld.youyou.bean.FocusOldTitleBean;
import com.neworld.youyou.bean.FocusTitleBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.SPUtil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;
import com.neworld.youyou.view.NewContentFocus;
import com.neworld.youyou.view.NewFocusList;

import java.util.ArrayList;
import java.util.List;

public class FocusActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivBack;
    private ArrayList<FocusBean.MenuListBean> newList = new ArrayList<>();
    private ArrayList<FocusBean.NotCircleListBean> oldList = new ArrayList<>();
    private ListView lvOrder;
    private ArrayList<NewFocusList> allList = new ArrayList<>();
    private NewFocusAdapter focusAdapter;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        initUser();
        initView();
        initData();
    }

    private void initUser() {
        userId = SPUtil.getString(FocusActivity.this, "userId", "");
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "115");
                if (content != null && content.length() > 0) {
                    FocusBean focusBean = GsonUtil.parseJsonToBean(content, FocusBean.class);
                    if (focusBean != null && focusBean.getStatus() == 0) {
                        FocusTitleBean focusTitleBean = new FocusTitleBean("已经关注");
                        allList.add(focusTitleBean);
                        if (focusBean.getMenuList() != null && focusBean.getMenuList().size() > 0) {
                            List<FocusBean.MenuListBean> menuList = focusBean.getMenuList();
                            newList.addAll(menuList);
                        }
                        allList.addAll(newList);
                        FocusOldTitleBean focusOldTitleBean = new FocusOldTitleBean("推荐关注");
                        allList.add(focusOldTitleBean);
                        if (focusBean.getNotCircleList() != null && focusBean.getNotCircleList().size() > 0) {
                            List<FocusBean.NotCircleListBean> notCircleList = focusBean.getNotCircleList();
                            oldList.addAll(notCircleList);
                        }
                        allList.addAll(oldList);
                        Util.uiThread(new Runnable() {
                            @Override
                            public void run() {
//                                lvOrder.setAdapter(null);
//                                lvOrder.setAdapter(focusAdapter); // TODO : 很SB的写法。
                                focusAdapter.notifyDataSetChanged();
                            }
                        });
                    } else if (focusBean.getStatus() == 1) {
                        ToastUtil.showToast("访问失败");
                    }
                } else {
                    ToastUtil.showToast("网络不佳");
                }
            }
        }).start();
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.iv_focus);
        ivBack.setOnClickListener(this);
        lvOrder = (ListView) findViewById(R.id.lv);

        if (focusAdapter == null) {
            focusAdapter = new NewFocusAdapter(FocusActivity.this, allList);
            lvOrder.setAdapter(focusAdapter);
        } else {
            focusAdapter.notifyDataSetChanged();
        }
        /*lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //ToastUtil.showToast("点击子条目");
                Intent intent = new Intent();// TODO : 本来点开是专题页面，现在改为整个点击取关和关注，如有需要取消注释即可。
                intent.setClass(FocusActivity.this, FocusDetailActivity.class);
                Bundle bundle = new Bundle();
                NewFocusList focusList = allList.get(position);
                if (focusList != null && focusList instanceof FocusBean.MenuListBean) {
                    bundle.putString("title", ((FocusBean.MenuListBean) focusList).getTypeName());
                    FocusBean.MenuListBean menuListBean = (FocusBean.MenuListBean) focusList;
                    if (menuListBean != null && menuListBean.getTypeId() != 0 && menuListBean.getNamicInfoBean() != null) {
                        bundle.putInt("typeId", menuListBean.getTypeId());
                    }
                } else if (focusList != null && focusList instanceof FocusBean.NotCircleListBean){
                    FocusBean.NotCircleListBean list = (FocusBean.NotCircleListBean) focusList;
                    String typeName = list.getTypeName();
                    bundle.putString("title", typeName);
                    if (list != null && list.getTypeId() != 0 && list.getNamicInfoBean() != null) {
                        bundle.putInt("typeId", list.getTypeId());
                    }
                }
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });*/
        focusAdapter.setOnFocusClick(new NewFocusAdapter.OnNewFocusClick() {
            @Override
            public void onItemAdd(int positon, int tag) {
                if (tag == 1) {
                    deleteFocus(positon);
                } else if (tag == 2) {
                    addFocus(positon);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_focus:
                finish();
                break;

        }
    }

    //将关注放入推荐
    private void deleteFocus(final int positon) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NewFocusList focusList = allList.get(positon);
                if (focusList != null && focusList instanceof NewContentFocus) {
                    NewContentFocus newContentFocus = (NewContentFocus) focusList;
                    if (newContentFocus != null && newContentFocus instanceof FocusBean.MenuListBean) {
                        FocusBean.MenuListBean menuListBean = (FocusBean.MenuListBean) newContentFocus;
                        if (menuListBean != null) {
                            int spotId = menuListBean.getSpotId();
                            String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"spotId\":\"" + spotId + "\"}").getBytes(), Base64.DEFAULT);
                            String replace = base64.replace("\n", "");
                            String content = NetManager.getInstance().getContent(replace, "117");
                            if (content != null && content.length() > 0) {
                                ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                                if (returnStatus != null && returnStatus.getStatus() == 0) {
                                    Util.uiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            allList.clear();
                                            newList.clear();
                                            oldList.clear();
                                            initData();
                                        }
                                    });
                                } else {
                                    ToastUtil.showToast("关注中未知的异常");
                                }
                            }
                        }
                    }
                }
            }
        }).start();
    }

    //将推荐关注的放入已经关注
    private void addFocus(final int positon) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NewFocusList focusList = allList.get(positon);
                if (focusList != null && focusList instanceof NewContentFocus) {
                    NewContentFocus focusList1 = (NewContentFocus) focusList;
                    if (focusList1 != null && focusList1 instanceof FocusBean.NotCircleListBean) {
                        FocusBean.NotCircleListBean notCircleListBean = (FocusBean.NotCircleListBean) focusList1;
                        if (notCircleListBean != null) {
                            int typeId = notCircleListBean.getTypeId();
                            String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"typeId\":\"" + typeId + "\"}").getBytes(), Base64.DEFAULT);
                            String replace = base64.replace("\n", "");
                            String content = NetManager.getInstance().getContent(replace, "116");
                            if (content != null && content.length() > 0) {
                                ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                                if (returnStatus != null && returnStatus.getStatus() == 0) {
                                    NewFocusList remove = allList.remove(positon);
                                    if (newList != null) {
                                        allList.add(newList.size() + 1, remove);
                                        Util.uiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //focusAdapter.notifyDataSetChanged();
                                                allList.clear();
                                                newList.clear();
                                                oldList.clear();
                                                initData();
                                            }
                                        });
                                    }
                                } else {
                                    ToastUtil.showToast("关注中未知的异常");
                                }
                            }
                        }
                    }
                }
            }
        }).start();
    }

}