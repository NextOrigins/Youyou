package com.neworld.youyou.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.neworld.youyou.R;
import com.neworld.youyou.adapter.ChildDetailFragment;
import com.neworld.youyou.fragment.AddChildFragment;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.view.BackHandlerHelper;
import com.neworld.youyou.view.ChildId;

public class AddChildActivity extends AppCompatActivity implements ChildId {
    private FragmentManager fragmentManager;
    public int babyId;
    public String name;
    public String gender;
    public String birth;
    public String school;
    public String kindergarten;
    public String junior;
    public String primary;
    public String gradeJunior;
    public String timeJunior;
    public String locationKindergarten;
    public String timeKindergarten;
    public String gradeKindergarten;
    public String locationPrimary;
    public String timePrimary;
    public String gradePrimary;
    public String locationJunior;
    public String userId;
    public boolean isFromPay = false;
    public boolean isAddDelete = false;
    private AddChildFragment mAddChildFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);
        initUser();
        initView();
    }

    private void initUser() {
        userId = Sputil.getString(AddChildActivity.this, "userId", "");
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                this.isFromPay = extras.getBoolean("isFromPay", false);
            }
        }
    }

    public void changePage(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.fl_content, fragment)
                .addToBackStack(null)//添加到栈
                .commit();
    }

    public void changePage(Fragment fragment, String tag, String ft) {
        fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.fl_content, fragment, ft)
                .addToBackStack(tag)//添加到栈
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
        }
    }

    /**
     * 模拟退栈，抛出最上层的Fragment
     */
    public void popBackStack() {
        fragmentManager.popBackStackImmediate(null, 0);//参数为0，清除栈顶的Fragment，参数为1，清空栈
    }

    private void initView() {
        Intent intent = getIntent();

        if (mAddChildFragment == null)
            mAddChildFragment = new AddChildFragment();

        if (intent.getBooleanExtra("edit", false)) {
            ChildDetailFragment childDetailFragment = new ChildDetailFragment();
            childDetailFragment.setBybyId(intent.getIntExtra("id", 0));
            changePage(childDetailFragment);
        } else if (intent.getBooleanExtra("pay", false)) {
            ChildDetailFragment childDetailFragment = new ChildDetailFragment();
            childDetailFragment.setBybyId(0);
            changePage(childDetailFragment);

        } else changePage(mAddChildFragment);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fragmentManager.getBackStackEntryCount() == 1) {
                this.finish();
                return true;
            }
        }
        return true;
    }

    public WindowManager getWindowData() {
        return getWindowManager();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mAddChildFragment != null) {
            ChildDetailFragment childeDettailFragment = mAddChildFragment.getChildeDettailFragment();
            if (childeDettailFragment != null) {
                childeDettailFragment.savePhotoView(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onBabyId(int babyId, String name, String gender, String birth, String school, String kindergarten, String primary, String junior,
                         String locationKindergarten, String timeKindergarten, String gradeKindergarten,
                         String locationPrimary, String timePrimary, String gradePrimary,
                         String locationJunior, String timeJunior, String gradeJunior, boolean isFromPay, boolean isAddDelete) {
        this.kindergarten = kindergarten;
        this.babyId = babyId;
        this.name = name;
        this.gender = gender;
        this.birth = birth;
        this.school = school;
        this.junior = junior;
        this.primary = primary;
        this.locationKindergarten = locationKindergarten;
        this.timeKindergarten = timeKindergarten;
        this.gradeKindergarten = gradeKindergarten;
        this.locationPrimary = locationPrimary;
        this.timePrimary = timePrimary;
        this.gradePrimary = gradePrimary;
        this.locationJunior = locationJunior;
        this.timeJunior = timeJunior;
        this.gradeJunior = gradeJunior;
        this.isFromPay = isFromPay;
        this.isAddDelete = isAddDelete;
    }
}
