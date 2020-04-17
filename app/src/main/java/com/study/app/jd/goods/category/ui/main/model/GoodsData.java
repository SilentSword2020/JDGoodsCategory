package com.study.app.jd.goods.category.ui.main.model;

import java.util.ArrayList;

/**
 * 小分类的商品列表
 */
public class GoodsData {
    private String cId;

    private String count;

    private ArrayList<GoodsItem> list;

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public ArrayList<GoodsItem> getList() {
        return list;
    }

    public void setList(ArrayList<GoodsItem> list) {
        this.list = list;
    }
}