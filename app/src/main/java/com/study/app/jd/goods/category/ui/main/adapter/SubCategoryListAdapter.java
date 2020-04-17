package com.study.app.jd.goods.category.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.ObjectsCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.study.app.jd.goods.category.R;
import com.study.app.jd.goods.category.ui.main.model.SubCategoryItem;
import com.study.app.jd.goods.category.ui.main.util.ListUtil;

import java.util.List;

/**
 * 小分类列表适配器
 */
public class SubCategoryListAdapter extends ListAdapter<SubCategoryItem, SubCategoryListAdapter.ViewHolder>
        implements View.OnClickListener {
    public SubCategoryListAdapter(Callback callback) {
        super(new DiffUtil.ItemCallback<SubCategoryItem>() {
            //DiffUtil.ItemCallback：保证列表更新不移动当前位置
            @Override
            public boolean areItemsTheSame(@NonNull SubCategoryItem oldItem, @NonNull SubCategoryItem newItem) {
                return ObjectsCompat.equals(oldItem.getcId(), newItem.getcId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull SubCategoryItem oldItem, @NonNull SubCategoryItem newItem) {
                return ObjectsCompat.equals(oldItem.getName(), newItem.getName());
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
                .inflate(R.layout.item_sub_category, parent, false));
        viewHolder.itemView.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubCategoryItem item = getItem(position);
        if (item == null) {
            return;
        }
        holder.itemView.setTag(position);

        //
        holder.tvName.setText(item.getName());
        holder.tvName.setSelected(selectPosition == position);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryListAdapter.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!ListUtil.isEmpty(payloads)) {
            Object item = ListUtil.getItemAt(payloads, 0);
            if (item instanceof Boolean) {
                holder.tvName.setSelected((Boolean) item);
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
                callback.onClickSubCategoryItem(position, getItem(position));
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
        public TextView tvName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }

    public interface Callback {
        /**
         * 点击小分类item
         *
         * @param position
         * @param item
         */
        void onClickSubCategoryItem(int position, SubCategoryItem item);
    }
}
