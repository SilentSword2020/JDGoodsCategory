package com.study.app.jd.goods.category.ui.main.model;

import androidx.core.util.ObjectsCompat;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * 分类
 */
public class CategoryItem {
    /**
     * 分类id
     */
    private String cId;


    /**
     * 分类名称
     */
    private String name;

    /**
     * 图片：表情小图片
     */
    private String imageUrl;

    /**
     * 子分类列表
     */
    private ArrayList<SubCategoryItem> list;
    /**
     * 大分类列表数据中的位置
     */
    @Expose
    private int position;

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<SubCategoryItem> getList() {
        return list;
    }

    public void setList(ArrayList<SubCategoryItem> list) {
        this.list = list;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * 是否相同
     *
     * @param item
     * @return
     */
    public boolean isSame(CategoryItem item) {
        if (item == null) {
            return false;
        }
        return ObjectsCompat.equals(item.getcId(), cId);
    }
}