package com.neworld.youyou.wxapi;

import android.content.Intent;
import android.os.Bundle;


import com.neworld.youyou.R;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {
	
//	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, "wxd7cf604bab22c904");
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

/*	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		//Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		*//*if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.app_tip);
			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
			builder.show();
		}*//*
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			//int errCord = resp.errCode;
			//这里接收到了返回的状态码可以进行相应的操作，如果不想在这个页面操作可以把状态码存在本地然后finish掉这个页面，这样就回到了你调起支付的那个页面
			//获取到你刚刚存到本地的状态码进行相应的操作就可以了
			if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
				int code = resp.errCode; switch (code) {
					case 0:
						ToastUtil.showToast("支付成功");
						break;
					case -1:
						ToastUtil.showToast("支付失败");
						finish();
						break;
					case -2:
						ToastUtil.showToast("支付取消");
						finish();
						break;
					deftimg:
						ToastUtil.showToast("支付失败");
						setResult(RESULT_OK);
						finish();
						break;
				}
			}

		} else if (resp.getType()==ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX){
			super.onResp(resp);//一定要加super，实现我们的方法，否则不能回调
			//ToastUtil.showToast("我走啦+//++++++++++++++++++++++++++++++++++++");
			String result = "";
			switch (resp.errCode) {
				case BaseResp.ErrCode.ERR_OK:
					result = "分享成功";
					Sputil.saveBoolean(this, "WXShare", true);
					break;
				case BaseResp.ErrCode.ERR_USER_CANCEL:
					result = "取消分享";
					Sputil.saveBoolean(this, "WXShare", false);
					break;
				case BaseResp.ErrCode.ERR_SENT_FAILED:
					result = "分享失败";
					Sputil.saveBoolean(this, "WXShare", false);
					break;
				deftimg:
                    ToastUtil.showToast(result+"");
					result = "未知原因";

					Sputil.saveBoolean(this, "WXShare", false);
					break;
			}
			ToastUtil.showToast(result);
			finish();
		}*/


}