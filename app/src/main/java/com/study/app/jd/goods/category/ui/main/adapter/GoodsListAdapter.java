package com.study.app.jd.goods.category.ui.main.adapter;

import android.text.TextUtils;
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
import com.study.app.jd.goods.category.ui.main.model.GoodsItem;

public class GoodsListAdapter extends ListAdapter<GoodsItem, GoodsListAdapter.GoodsItemViewHolder> {
    public GoodsListAdapter(Callback callback) {
        super(new DiffUtil.ItemCallback<GoodsItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull GoodsItem oldItem, @NonNull GoodsItem newItem) {
                return ObjectsCompat.equals(oldItem.getGoodsId(), newItem.getGoodsId()) &&
                        ObjectsCompat.equals(oldItem.getcId(), newItem.getcId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull GoodsItem oldItem, @NonNull GoodsItem newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.callback = callback;
    }

    private Callback callback;


    @NonNull
    @Override
    public GoodsItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GoodsItemViewHolder viewHolder = new GoodsItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goods, parent, false));
        viewHolder.callback = callback;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsItemViewHolder holder, int position) {
        GoodsItem item = getItem(position);
        if (callback != null) {
            callback.onBind(position, item);
        }
        holder.itemView.setTag(item);
        holder.position = position;
        holder.item = item;
        if (item == null) {
            return;
        }
        holder.tvCategoryName.setVisibility(item.isShowCategory() ? View.VISIBLE : View.GONE);
        holder.tvCategoryName.setText(item.getName());
        if (TextUtils.isEmpty(item.getGoodsId())) {
            //空白数据
            holder.tvGoodsName.setVisibility(View.GONE);
            holder.ivImage.setVisibility(View.GONE);
        } else {
            holder.tvGoodsName.setVisibility(View.VISIBLE);
            holder.ivImage.setVisibility(View.VISIBLE);
            holder.tvGoodsName.setText(item.getName() + "--" + item.getGoodsName());
            Glide.with(holder.ivImage.getContext()).load(item.getGoodsImage()).into(holder.ivImage);
        }
    }


    public static class GoodsItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivImage;
        public TextView tvCategoryName;
        public TextView tvGoodsName;
        public Callback callback;
        public GoodsItem item;
        public int position;

        public GoodsItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvGoodsName = itemView.findViewById(R.id.tvGoodsName);
            tvCategoryName = itemView.findViewById(R.id.tvCategory);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                if (callback != null) {
                    callback.onClickGoodsItem(position, item);
                }
            }
        }
    }

    public interface Callback {
        void onBind(int position, GoodsItem item);

        void onClickGoodsItem(int position, GoodsItem item);
    }
}
