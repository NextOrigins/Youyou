package com.neworld.youyou.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.bean.AddressBean;

import java.util.List;

/**
 * @author asus on 2017/9/27.
 *  -- 现已废弃，无用待删除。
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<AddressBean.MenuListBean> mShowList;
    private OnAddressListener onAddressListenter;

    public AddressAdapter(List<AddressBean.MenuListBean> addressList) {
        this.mShowList = addressList;
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_address, null);

        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        final int index = position;
        final AddressBean.MenuListBean menuListBean = mShowList.get(position);
        holder.mTv_address_name.setText(menuListBean.getConsignee());
        holder.mTv_address_phone.setText(menuListBean.getPhone());
        holder.mTv_address_content.setText(menuListBean.getAddress());
        if (menuListBean.getStatus() == 0) {
            holder.mIv_status.setImageResource(R.mipmap.check_address);
        } else {
            holder.mIv_status.setImageResource(R.mipmap.nochecked_address);
        }

        //设置编辑监听
        holder.mEditor.setOnClickListener(v -> {
            if (onAddressListenter != null) {
                onAddressListenter.onEdit(mShowList, index, menuListBean);
            }
        });
        //设置删除监听
        holder.mDelete.setOnClickListener(v -> {
            if (onAddressListenter != null) {
                onAddressListenter.onDelete(mShowList, index, menuListBean);
            }
        });

        // 在购买商品页面点击条目返回地址并关闭
        holder.fis.setOnClickListener(v -> {
            if (onAddressListenter != null) {
                onAddressListenter.onFinished(mShowList, index, menuListBean);
            }
        });

        // 设置默认地址点击事件
        holder.addressDefault.setOnClickListener(view -> {
            if (onAddressListenter != null) {
                onAddressListenter.onDefaultAddressStateChanged(mShowList, position, menuListBean);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mShowList != null ? mShowList.size() : 0;
    }

    //设置接口回调
    public interface OnAddressListener {
        /**
         * 编辑监听
         */
        void onEdit(List<AddressBean.MenuListBean> showList, int position, AddressBean.MenuListBean menuListBean);

        /**
         * 删除监听
         */
        void onDelete(List<AddressBean.MenuListBean> showList, int position, AddressBean.MenuListBean menuListBean);

        /**
         * 购买商品页面点击条目返回地址并关闭
         */
        void onFinished(List<AddressBean.MenuListBean> showList, int position, AddressBean.MenuListBean menuListBean);

        /**
         * 默认地址按钮监听
         */
        void onDefaultAddressStateChanged(List<AddressBean.MenuListBean> showList, int position, AddressBean.MenuListBean menuListBean);
    }

    public void setOnAddressListener(OnAddressListener listener) {
        this.onAddressListenter = listener;
    }


    static class AddressViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTv_address_name;
        private final TextView mTv_address_phone;
        private final TextView mTv_address_content;
        private final ImageView mIv_status;
        private final RelativeLayout mEditor;
        private final RelativeLayout mDelete;
        private LinearLayout fis;
        private TextView addressDefault;

        AddressViewHolder(View itemView) {
            super(itemView);
            mTv_address_name = (TextView) itemView.findViewById(R.id.tv_address_name);
            mTv_address_phone = (TextView) itemView.findViewById(R.id.tv_address_phone);
            mTv_address_content = (TextView) itemView.findViewById(R.id.tv_address_content);
            mIv_status = (ImageView) itemView.findViewById(R.id.iv_status);
            mEditor = (RelativeLayout) itemView.findViewById(R.id.rl_address_editor);
            mDelete = (RelativeLayout) itemView.findViewById(R.id.rl_address_delete);
            fis = (LinearLayout) itemView.findViewById(R.id.linear_finish);
            addressDefault = (TextView) itemView.findViewById(R.id.tv_default);
        }
    }

}
