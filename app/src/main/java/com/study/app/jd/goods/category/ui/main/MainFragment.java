package com.study.app.jd.goods.category.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.study.app.jd.goods.category.R;
import com.study.app.jd.goods.category.ui.main.adapter.CategoryListAdapter;
import com.study.app.jd.goods.category.ui.main.adapter.GoodsListAdapter;
import com.study.app.jd.goods.category.ui.main.adapter.SubCategoryListAdapter;
import com.study.app.jd.goods.category.ui.main.model.CategoryItem;
import com.study.app.jd.goods.category.ui.main.model.GoodsItem;
import com.study.app.jd.goods.category.ui.main.model.SubCategoryItem;
import com.study.app.jd.goods.category.ui.main.util.ListUtil;

public class MainFragment extends Fragment implements CategoryListAdapter.Callback,
        SubCategoryListAdapter.Callback, GoodsListAdapter.Callback {

    private MainViewModel mViewModel;


    public static MainFragment newInstance() {
        return new MainFragment();
    }


    private RecyclerView rvCategoryList;
    private RecyclerView rvSubCategoryList;
    private RecyclerView rvGoodsList;

    private CategoryListAdapter categoryListAdapter;
    private SubCategoryListAdapter subCategoryListAdapter;
    private GoodsListAdapter goodsListAdapter;

    private volatile boolean isMove = false;
    private LinearLayoutManager goodsListLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mViewModel.loadData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvCategoryList = view.findViewById(R.id.rv_category_list);
        rvSubCategoryList = view.findViewById(R.id.rv_sub_category_list);
        rvGoodsList = view.findViewById(R.id.rv_goods_list);

        rvCategoryList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvSubCategoryList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        goodsListLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvGoodsList.setLayoutManager(goodsListLayoutManager);

        categoryListAdapter = new CategoryListAdapter(this);
        rvCategoryList.setAdapter(categoryListAdapter);

        subCategoryListAdapter = new SubCategoryListAdapter(this);
        rvSubCategoryList.setAdapter(subCategoryListAdapter);

        goodsListAdapter = new GoodsListAdapter(this);
        rvGoodsList.setAdapter(goodsListAdapter);
        rvGoodsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    changeCategoryShow(recyclerView);
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMove) {
                    changeCategoryShow(recyclerView);
                }
            }
        });
        rvGoodsList.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    isMove = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    isMove = false;
                    break;
            }
            return false;
        });

        subscribeUi(getViewLifecycleOwner());
    }

    /**
     * 改变当前分类，小分类的选择显示
     *
     * @param recyclerView
     */
    private void changeCategoryShow(@NonNull RecyclerView recyclerView) {
        if (goodsListLayoutManager == null) {
            return;
        }
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(goodsListLayoutManager.findLastVisibleItemPosition());
        if (viewHolder instanceof GoodsListAdapter.GoodsItemViewHolder) {
            GoodsListAdapter.GoodsItemViewHolder itemViewHolder = (GoodsListAdapter.GoodsItemViewHolder) viewHolder;
            if (itemViewHolder.item != null) {
                mViewModel.updateCategory(itemViewHolder.item);
            }
        }
    }

    private void subscribeUi(LifecycleOwner viewLifecycleOwner) {
        mViewModel.goodsCategoryLiveData().observe(viewLifecycleOwner, goodsCategoryData -> {
            if (goodsCategoryData == null) {
                //TODO 这里不可能为空
                return;
            }
            if (categoryListAdapter != null) {
                categoryListAdapter.submitList(goodsCategoryData.getList());
            }
            if (subCategoryListAdapter != null) {
                CategoryItem item = ListUtil.getFirstItem(goodsCategoryData.getList());
                subCategoryListAdapter.submitList(item == null ? null : item.getList());
            }
        });
        mViewModel.currentCategory().observe(viewLifecycleOwner, data -> {
            if (data == null || data.first == null) {
                return;
            }
            if (rvCategoryList != null) {
                rvCategoryList.smoothScrollToPosition(data.first);
            }
            if (categoryListAdapter != null) {
                categoryListAdapter.updateSelectPosition(data.first);
            }
            if (subCategoryListAdapter != null) {
                subCategoryListAdapter.submitList(data.second == null ? null : data.second.getList());
            }
        });
        mViewModel.currentSubCategory().observe(viewLifecycleOwner, item -> {
            if (item == null || item.first == null) {
                return;
            }
            if (rvSubCategoryList != null) {
                rvSubCategoryList.smoothScrollToPosition(item.first);
            }
            Log.e("test", "currentSubCategory position:" + item.first);
            if (subCategoryListAdapter != null) {
                subCategoryListAdapter.updateSelectPosition(item.first);
            }
        });
        mViewModel.goodsListCurrentPosition().observe(viewLifecycleOwner, position -> {
            if (position == null || position < 0) {
                return;
            }
            if (goodsListLayoutManager != null) {
                goodsListLayoutManager.scrollToPositionWithOffset(position, 0);
            }
        });
        mViewModel.goodsList().observe(viewLifecycleOwner, item -> {
            if (item == null) {
                return;
            }
            if (goodsListAdapter != null) {
                if (ListUtil.isEmpty(item.second)) {
                    //加载下一页：所有商品列表数据
                    goodsListAdapter.submitList(item.first);
                } else {
                    //加载第一页（第一个小分类时）：先显示后面的列表数据，这样不用去考虑滚动的位置
                    goodsListAdapter.submitList(item.second, () -> {
                        if (goodsListLayoutManager != null) {
                            goodsListLayoutManager.scrollToPositionWithOffset(0, 0);
                        }
                        rvGoodsList.postDelayed(() -> {
                            //过一段时间，再把所有数据更新到列表
                            goodsListAdapter.submitList(item.first);
                        }, 100);

                    });
                }
            }
        });


    }

    @Override
    public void onClickCategoryItem(int position, CategoryItem item) {
        mViewModel.updateCurrentCategory(position, item);
    }

    @Override
    public void onClickSubCategoryItem(int position, SubCategoryItem item) {
        mViewModel.updateCurrentSubCategory(position, item);
    }

    @Override
    public void onClickGoodsItem(int position, GoodsItem item) {
        Toast.makeText(getContext(), item.getGoodsName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBind(int position, GoodsItem item) {
        if (goodsListAdapter != null) {
            mViewModel.bindData(position, goodsListAdapter.getItemCount(), item);
        }
    }
}
