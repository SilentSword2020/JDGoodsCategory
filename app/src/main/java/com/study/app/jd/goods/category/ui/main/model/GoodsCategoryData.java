package com.study.app.jd.goods.category.ui.main.model;

import java.util.ArrayList;

/**
 * 分类数据
 */
public class GoodsCategoryData {
    /**
     * 排序列表
     */
    private ArrayList<SortItem> sortList;

    /**
     * 分类列表
     */
    private ArrayList<CategoryItem> list;

    public ArrayList<SortItem> getSortList() {
        return sortList;
    }

    public void setSortList(ArrayList<SortItem> sortList) {
        this.sortList = sortList;
    }

    public ArrayList<CategoryItem> getList() {
        return list;
    }

    public void setList(ArrayList<CategoryItem> list) {
        this.list = list;
    }
}