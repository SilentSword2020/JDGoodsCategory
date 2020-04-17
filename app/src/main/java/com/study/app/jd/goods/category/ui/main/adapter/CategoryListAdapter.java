package com.study.app.jd.goods.category.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.ObjectsCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.study.app.jd.goods.category.R;
import com.study.app.jd.goods.category.ui.main.model.CategoryItem;
import com.study.app.jd.goods.category.ui.main.util.ListUtil;

import java.util.List;

/**
 * 大分类列表
 */
public class CategoryListAdapter extends ListAdapter<CategoryItem, CategoryListAdapter.ViewHolder>
        implements View.OnClickListener {
    public CategoryListAdapter(Callback callback) {
        super(new DiffUtil.ItemCallback<CategoryItem>() {
            //DiffUtil.ItemCallback：保证列表更新不移动当前位置
            @Override
            public boolean areItemsTheSame(@NonNull CategoryItem oldItem, @NonNull CategoryItem newItem) {
                return ObjectsCompat.equals(oldItem.getcId(), newItem.getcId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull CategoryItem oldItem, @NonNull CategoryItem newItem) {
                //TODO 还有其他属性???
                return ObjectsCompat.equals(oldItem.getName(), newItem.getName())
                        && ObjectsCompat.equals(oldItem.getImageUrl(), newItem.getImageUrl());
            }
        });
        //
        this.callback = callback;
    }

    /**
     * 操作回调
     */
    private Callback callback;
    private int selectPosition = 0;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false));
        viewHolder.itemView.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryItem item = getItem(position);
        if (item == null) {
            return;
        }
        holder.itemView.setTag(position);
        holder.itemView.setSelected(selectPosition == position);
        holder.tvName.setText(item.getName());
        Glide.with(holder.ivImage.getContext()).load(item.getImageUrl()).into(holder.ivImage);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListAdapter.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!ListUtil.isEmpty(payloads)) {
            Object item = ListUtil.getItemAt(payloads, 0);
            if (item instanceof Boolean) {
                holder.itemView.setSelected((Boolean) item);
            }
        } else {
            onBindViewHolder(holder, position);
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof Integer) {
            int position = (int) tag;
            if (updateSelectPosition(position)) return;
            if (callback != null) {
                callback.onClickCategoryItem(position, getItem(position));
            }
        }
    }

    /**
     * 更新显示的位置
     *
     * @param position
     * @return
     */
    public boolean updateSelectPosition(int position) {
        if (position < 0 || position >= getItemCount()) {
            return true;
        }
        if (selectPosition == position) {
            //同一位置不做处理
            return true;
        }
        int lastPosition = selectPosition;
        selectPosition = position;
        notifyItemChanged(lastPosition, false);
        notifyItemChanged(position, true);
        return false;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivImage;
        public TextView tvName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvName = itemView.findViewById(R.id.tvName);
        }

    }

    public interface Callback {
        /**
         * 点击大分类item
         *
         * @param position
         * @param item
         */
        void onClickCategoryItem(int position, CategoryItem item);
    }
}
