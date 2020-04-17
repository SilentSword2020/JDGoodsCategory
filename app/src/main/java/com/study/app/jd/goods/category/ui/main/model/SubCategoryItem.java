package com.study.app.jd.goods.category.ui.main.model;

import androidx.core.util.ObjectsCompat;

import com.google.gson.annotations.Expose;
import com.study.app.jd.goods.category.ui.main.adapter.SubCategoryListAdapter;

/**
 * 子分类
 */
public class SubCategoryItem {
    /**
     * 分类id
     */
    private String cId;
    /**
     * 分类id
     */
    private String bigCId;
    /**
     * 位置
     */
    @Expose
    private int position;
    /**
     * 名称
     */
    private String name;

    /**
     * 图片：表情小图片
     */
    private String imageUrl;

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

    public String getBigCId() {
        return bigCId;
    }

    public void setBigCId(String bigCId) {
        this.bigCId = bigCId;
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
    public boolean isSame(SubCategoryItem item) {
        if (item == null) {
            return false;
        }
        return ObjectsCompat.equals(item.getcId(), cId);
    }
}