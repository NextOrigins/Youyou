package com.neworld.youyou.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ToastUtil;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvCancle;
    private TextView tvCommit;
    private EditText etComment;
    private String userId;
    private String from_userId;
    private String taskId;
    private String nickName;
    private String commentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initUser();
        initView();

    }

    private void initUser() {
        userId = Sputil.getString(CommentActivity.this, "userId", "");
        Bundle extras = getIntent().getExtras();
        from_userId = extras.getString("from_userId", "");
        taskId = extras.getString("taskId", "");
        nickName = extras.getString("nickName", "");
        commentId = extras.getString("commentId", "");
    }

    private void initView() {
        tvCancle = (TextView) findViewById(R.id.comment_cancel);
        tvCommit = (TextView) findViewById(R.id.comment_commit);
        etComment = (EditText) findViewById(R.id.et_comment);
        tvCancle.setOnClickListener(this);
        tvCommit.setOnClickListener(this);
        if (nickName != null && nickName.length() > 0) {
            etComment.setHint("@" +nickName);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
        }
        return false;
    }
    //判断是否有内容
    private void back() {
        String trim = etComment.getText().toString().trim();
        if (trim != null && trim.length() > 0) {
            dialogPublish();
        } else {
            finish();
        }
    }

    private void dialogPublish() {
        final AlertDialog.Builder ad=new AlertDialog.Builder(CommentActivity.this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_cancel:
                back();
                break;
            case R.id.comment_commit:
                commit();
                break;
        }
    }
    //提交评论
    private void commit() {
        final String content = etComment.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(from_userId) && !TextUtils.isEmpty(taskId)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"from_userId\":\"" + from_userId + "\", \"taskId\":\"" + taskId + "\", \"content\":\"" + content + "\", \"comment_id\":\""+commentId+"\"}").getBytes(), Base64.DEFAULT);
                        String replace = base64.replace("\n", "");
                        String result = NetManager.getInstance().getContent(replace, "114");
                        if (result != null && result.length() > 0) {
                            ReturnStatus returnStatus = GsonUtil.parseJsonToBean(result, ReturnStatus.class);
                            if (returnStatus.getStatus() == 0) {
                                Intent intent = new Intent();
                                intent.putExtra("isPublish", true);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                ToastUtil.showToast("评论发生未知的错误");
                            }
                        }
                    }
                }).start();
            } else {
                ToastUtil.showToast("传入的数据有误");
            }
        } else {
            ToastUtil.showToast("评论不能为空");
        }
    }

}
