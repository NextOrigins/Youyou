package com.neworld.youyou.add.common;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by user on 2017/11/6.
 * 万用Adapter (没有封装多type列表处理，暂无需要。懒)
 */

public class Adapter<T> extends RecyclerView.Adapter<Adapter.Holder> {

    protected List<T> bean = new ArrayList<>();
    protected AdapterObs<T> obs;

    public Adapter(AdapterObs<T> obs, List<T> bean) {
        this.bean = bean;
        this.obs = obs;
    }

    public Adapter(List<T> bean) {
        this.bean = bean;
    }

    public void setObs(AdapterObs<T> obs) {
        this.obs = obs;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(obs.layoutId(), parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        obs.onBind(holder, bean, position);
    }

    @Override
    public int getItemCount() {
        return bean.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        Holder(View itemView) {
            super(itemView);
        }

        public <T extends View> T find(int res) {
            SparseArray<T> array = (SparseArray<T>) itemView.getTag();
            if (array == null) {
                array = new SparseArray<>();
                itemView.setTag(array);
            }
            T t = array.get(res);
            if (t == null) {
                t = itemView.findViewById(res);
                array.put(res, t);
            }
            return t;
        }
    }

    public void remove(int position) {
        bean.remove(position);
        this.notifyItemRemoved(position);
        if (position < getItemCount()) {
            this.notifyItemRangeChanged(position, getItemCount());
        }
    }

    public interface AdapterObs<T> {
        void onBind(@NotNull Holder holder, @NotNull List<T> bean, int position);
        int layoutId();
    }
}

