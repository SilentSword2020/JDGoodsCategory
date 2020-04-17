package com.study.app.jd.goods.category.ui.main.model;

import androidx.annotation.TransitionRes;

import com.google.gson.annotations.Expose;

/**
 * 商品item
 */
public class GoodsItem {

    /**
     * 大分类id
     */
    @Expose
    private String bigCId;

    /**
     * 分类id
     */
    @Expose
    private String cId;

    /**
     * 名称
     */
    @Expose
    private String name;
    /**
     * 子分类的位置
     */
    @Expose
    private int subCatPosition;
    /**
     * 子分类的位置
     */
    @Expose
    private int catPosition;



    @Expose
    private boolean isShowCategory;


    private String goodsId;

    private String goodsName;

    private String goodsImage;

    private String goodsNumber;

    private String goodsPrice;

    private String marketPrice;

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsId() {
        return this.goodsId;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsName() {
        return this.goodsName;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }

    public String getGoodsImage() {
        return this.goodsImage;
    }

    public void setGoodsNumber(String goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public String getGoodsNumber() {
        return this.goodsNumber;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsPrice() {
        return this.goodsPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getMarketPrice() {
        return this.marketPrice;
    }

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

    public boolean isShowCategory() {
        return isShowCategory;
    }

    public void setShowCategory(boolean showCategory) {
        isShowCategory = showCategory;
    }

    public String getBigCId() {
        return bigCId;
    }

    public void setBigCId(String bigCId) {
        this.bigCId = bigCId;
    }

    public int getSubCatPosition() {
        return subCatPosition;
    }

    public void setSubCatPosition(int subCatPosition) {
        this.subCatPosition = subCatPosition;
    }

    public int getCatPosition() {
        return catPosition;
    }

    public void setCatPosition(int catPosition) {
        this.catPosition = catPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoodsItem goodsItem = (GoodsItem) o;

        if (goodsId != null ? !goodsId.equals(goodsItem.goodsId) : goodsItem.goodsId != null)
            return false;
        if (goodsName != null ? !goodsName.equals(goodsItem.goodsName) : goodsItem.goodsName != null)
            return false;
        if (goodsImage != null ? !goodsImage.equals(goodsItem.goodsImage) : goodsItem.goodsImage != null)
            return false;
        if (goodsNumber != null ? !goodsNumber.equals(goodsItem.goodsNumber) : goodsItem.goodsNumber != null)
            return false;
        if (goodsPrice != null ? !goodsPrice.equals(goodsItem.goodsPrice) : goodsItem.goodsPrice != null)
            return false;
        return marketPrice != null ? marketPrice.equals(goodsItem.marketPrice) : goodsItem.marketPrice == null;
    }

    @Override
    public int hashCode() {
        int result = goodsId != null ? goodsId.hashCode() : 0;
        result = 31 * result + (goodsName != null ? goodsName.hashCode() : 0);
        result = 31 * result + (goodsImage != null ? goodsImage.hashCode() : 0);
        result = 31 * result + (goodsNumber != null ? goodsNumber.hashCode() : 0);
        result = 31 * result + (goodsPrice != null ? goodsPrice.hashCode() : 0);
        result = 31 * result + (marketPrice != null ? marketPrice.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GoodsItem{" +
                "cId='" + cId + '\'' +
                ", name='" + name + '\'' +
                ", goodsId='" + goodsId + '\'' +
                ", goodsName='" + goodsName + '\'' +
                '}';
    }

    public static GoodsItem NULL_ITEM = new GoodsItem();
}