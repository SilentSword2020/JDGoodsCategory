package com.study.app.jd.goods.category.ui.main;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.study.app.jd.goods.category.ui.main.model.CategoryItem;
import com.study.app.jd.goods.category.ui.main.model.GoodsCategoryData;
import com.study.app.jd.goods.category.ui.main.model.GoodsItem;
import com.study.app.jd.goods.category.ui.main.model.SubCategoryItem;
import com.study.app.jd.goods.category.ui.main.repository.CategoryGoodsRepository;

import java.util.ArrayList;

/**
 * 分类页面的ViewModel
 */
public class MainViewModel extends AndroidViewModel {


    private final CategoryGoodsRepository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoryGoodsRepository(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clear();
    }

    /**
     * 加载数据
     */
    public void loadData() {
        repository.getCategoryData();
    }

    public void updateCurrentCategory(int position, CategoryItem item) {
        repository.updateCurrentCategory(position, item);
    }

    public void updateCurrentSubCategory(int position, SubCategoryItem item) {
        repository.updateCurrentSubCategory(position, item);
    }

    public void updateCategory(GoodsItem item) {
        repository.updateCategory(item);
    }

    public void bindData(int position, int itemCount, GoodsItem item) {
        repository.bindData(position, itemCount, item);
    }

    public MutableLiveData<GoodsCategoryData> goodsCategoryLiveData() {
        return repository.goodsCategoryLiveData;
    }

    public MutableLiveData<Pair<ArrayList<GoodsItem>, ArrayList<GoodsItem>>> goodsList() {
        return repository.goodsList;
    }


    public MutableLiveData<Pair<Integer, SubCategoryItem>> currentSubCategory() {
        return repository.currentSubCategory;
    }

    public MutableLiveData<Pair<Integer, CategoryItem>> currentCategory() {
        return repository.currentCategory;
    }


    public MutableLiveData<Integer> goodsListCurrentPosition() {
        return repository.goodsListCurrentPosition;
    }


}
