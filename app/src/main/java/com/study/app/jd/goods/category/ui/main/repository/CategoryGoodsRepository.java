package com.study.app.jd.goods.category.ui.main.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.core.util.ObjectsCompat;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.study.app.jd.goods.category.ui.main.model.CategoryItem;
import com.study.app.jd.goods.category.ui.main.model.GoodsCategoryData;
import com.study.app.jd.goods.category.ui.main.model.GoodsData;
import com.study.app.jd.goods.category.ui.main.model.GoodsItem;
import com.study.app.jd.goods.category.ui.main.model.SubCategoryItem;
import com.study.app.jd.goods.category.ui.main.util.ListUtil;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CategoryGoodsRepository {

    public static final String TAG = CategoryGoodsRepository.class.getSimpleName();

    /**
     * 加载第一页
     */
    private static final int TYPE_LOAD_INIT = 1;
    /**
     * 加载下一页数据
     */
    private static final int TYPE_LOAD_NEXT = 2;
    /**
     * 无效位置
     */
    private static final int POSITION_NONE = -1;
    /**
     * 默认位置
     */
    private static final int POSITION_DEFAULT = 0;
    /**
     * 默认商品id
     */
    private static final String DEFAULT_GOODS_ID = "DEFAULT_GOODS_ID";

    /**
     * 提前多少个开始加载下一个数据列表
     */
    private static final int PRE_LOAD_COUNT = 3;
    /**
     * 分类数据：大分类列表数据+小分类列表数据+排序列表数据
     */
    public final MutableLiveData<GoodsCategoryData> goodsCategoryLiveData = new MediatorLiveData<>();
    /**
     * 当前选择的分类
     * Pair(position, 分类item)
     */
    public final MutableLiveData<Pair<Integer, CategoryItem>> currentCategory = new MediatorLiveData<>();
    /**
     * 当前选择的小分类
     * Pair(position, 小分类item)
     */
    public final MutableLiveData<Pair<Integer, SubCategoryItem>> currentSubCategory = new MediatorLiveData<>();

    /**
     * 所有商品列表数据：用于商品列表显示
     */
    public final MutableLiveData<Pair<ArrayList<GoodsItem>, ArrayList<GoodsItem>>> goodsList = new MediatorLiveData<>();

    /**
     * 所有商品列表数据: 当前要滚动的位置
     */
    public final MutableLiveData<Integer> goodsListCurrentPosition = new MediatorLiveData<>();

    /**
     * 大分类id->分类item(方便获取数据)
     * 需要保持数据顺序
     */
    private final Map<String, CategoryItem> categoryMap = new LinkedHashMap<>();
    /**
     * 大分类id->子（小）分类列表(方便获取数据)
     * 需要保持数据顺序
     */
    private final Map<String, ArrayList<SubCategoryItem>> category2SubCategoryMap = new LinkedHashMap<>();

    /**
     * 子（小）分类id列表
     */
    private final List<String> subCategoryIdsMap = new ArrayList<>();
    /**
     * 小分类id->子（小）分类item(方便获取数据)
     */
    private final ArrayMap<String, SubCategoryItem> subCategoryMap = new ArrayMap<>();

    /**
     * 小分类id->商品列表(方便获取数据)
     * 需要保持数据顺序
     */
    private final Map<String, ArrayList<GoodsItem>> goodsListMap = new LinkedHashMap<>();

    private static final Object synObj = new Object();
    private final Handler handler = new Handler(Looper.getMainLooper());
    @NonNull
    private Context context;
    private Disposable dispose;
    private Disposable goodsListDispose;

    public CategoryGoodsRepository(Context context) {
        this.context = context;
    }

    public void clear() {
        if (dispose != null && !dispose.isDisposed()) {
            dispose.dispose();
            dispose = null;
        }
        if (goodsListDispose != null && !goodsListDispose.isDisposed()) {
            goodsListDispose.dispose();
            goodsListDispose = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 获取分类数据
     * <p>
     * 分类数据：大分类列表数据+小分类列表数据+排序列表数据
     */
    public void getCategoryData() {

        dispose = Observable.create((ObservableOnSubscribe<GoodsCategoryData>) emitter -> {
            GoodsCategoryData data = null;
            try {
                //TODO 从网络获取分类数据
                //TODO 分类数据：大分类列表数据+小分类列表数据+排序列表数据
                data = new Gson().fromJson(
                        new InputStreamReader(context.getAssets().open("data.json")),
                        GoodsCategoryData.class);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (data != null) {
                emitter.onNext(data);
                emitter.onComplete();
            } else {
                emitter.onError(new Throwable("category data is null!"));
            }
        }).subscribeOn(Schedulers.io())
                .doOnNext(data -> {
                    //TODO 开始生成临时数据，方便后面数据的获取

                    //分类数据
                    category2SubCategoryMap.clear();
                    ArrayList<CategoryItem> list = data.getList();
                    if (!ListUtil.isEmpty(list)) {
                        int index = 0;
                        for (CategoryItem item : list) {
                            if (item == null) {
                                continue;
                            }
                            item.setPosition(index);
                            category2SubCategoryMap.put(item.getcId(), item.getList());
                            categoryMap.put(item.getcId(), item);
                            index++;
                        }
                    }
                    //初始化小分类数据，商品列表数据
                    subCategoryMap.clear();
                    subCategoryIdsMap.clear();
                    goodsListMap.clear();
                    for (Map.Entry<String, ArrayList<SubCategoryItem>> item : category2SubCategoryMap.entrySet()) {
                        String bigCId = item.getKey();
                        if (!ListUtil.isEmpty(item.getValue())) {
                            int index = 0;
                            for (SubCategoryItem subItem : item.getValue()) {
                                if (subItem == null) {
                                    continue;
                                }
                                subItem.setBigCId(bigCId);
                                subItem.setPosition(index);
                                subCategoryMap.put(subItem.getcId(), subItem);
                                subCategoryIdsMap.add(subItem.getcId());
                                //刚开始，还没有商品数据
                                goodsListMap.put(subItem.getcId(), null);
                                index++;
                            }
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categoryList -> {
                            goodsCategoryLiveData.setValue(categoryList);
                            if (!ListUtil.isEmpty(categoryList.getList())) {
                                //大分类显示第一个
                                CategoryItem item = ListUtil.getItemAt(categoryList.getList(), 0);
                                currentCategory.setValue(new Pair<>(0, item));
                                if (item != null && !ListUtil.isEmpty(item.getList())) {
                                    //小分类显示第一个
                                    SubCategoryItem subItem = ListUtil.getItemAt(item.getList(), 0);
                                    currentSubCategory.setValue(new Pair<>(0, subItem));

                                    //开始加载第一个小分类商品列表
                                    if (subItem != null) {
                                        loadGoodsList(TYPE_LOAD_INIT, subItem.getcId());
                                    }
                                }
                            }
                        },
                        throwable -> {
                            //TODO 异常处理
                            Toast.makeText(context, "category error coming", Toast.LENGTH_LONG).show();
                        });


    }


    /**
     * 加载商品列表数据
     *
     * @param type 操作类型
     * @param cIds 小分类id列表
     */
    private void loadGoodsList(int type, String... cIds) {
        if (ListUtil.isEmpty(cIds)) {
            return;
        }
        Log.e(TAG, "loadGoodsList() type:" + type + ", cIds:" + Arrays.toString(cIds));

        if (checkCache(cIds)) {
            //如果有缓存，直接显示之前已经加载的数据
            return;
        }

        if (goodsListDispose != null) {
            //TODO 取消之前的数据更新通知
            goodsListDispose.dispose();
        }
        goodsListDispose = Observable.create((ObservableOnSubscribe<ArrayList<GoodsData>>) emitter -> {
            ArrayList<GoodsData> resultList = new ArrayList<>();
            try {
                //TODO 从网络获取分类数据
                ArrayList<GoodsData> goodsList = new Gson().fromJson(
                        new InputStreamReader(context.getAssets().open("goods_" + ListUtil.getFirstItem(cIds) + ".json")),
                        new TypeToken<ArrayList<GoodsData>>() {
                        }.getType());
                if (!ListUtil.isEmpty(goodsList)) {
                    resultList.addAll(goodsList);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            //TODO 模拟数据不存在或者没有网络时

            if (resultList != null) {
                emitter.onNext(resultList);
                emitter.onComplete();
            } else {
                emitter.onError(new Throwable("goods list data is null!"));
            }
        }).subscribeOn(Schedulers.io())
                .map(list -> {
                    //保存数据
                    cacheGoodsList(list);
                    return buildUiGoodsList(type, cIds);

                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            if (type == TYPE_LOAD_INIT) {
                                goodsList.setValue(result);
                            } else {
                                //加载下一页：直接更新所有数据
                                goodsList.setValue(new Pair<>(result.first, null));
                            }

                        },
                        throwable -> {
                            //TODO 异常处理
                            Toast.makeText(context, "goodsList error coming", Toast.LENGTH_LONG).show();
                        });
    }


    /**
     * 检查缓存数据
     * <p>
     * 如果有缓存，直接显示之前已经加载的数据
     *
     * @param cIds 小分类id列表
     * @return
     */
    private boolean checkCache(String[] cIds) {
        if (goodsList.getValue() == null) {
            return false;
        }
        if (ListUtil.getCount(cIds) == 1
                && !ListUtil.isEmpty(goodsListMap.get(ListUtil.getFirstItem(cIds)))) {
            //已经有数据
            ArrayList<GoodsItem> list = goodsList.getValue().first;
            int goodsPosition = getGoodsPosition(ListUtil.getFirstItem(cIds), list);
            goodsListCurrentPosition.postValue(goodsPosition);
            //
            Log.e(TAG, "loadGoodsList() has data goodsPosition:" + goodsPosition);
            return true;
        }
        return false;
    }

    /**
     * 缓存商品列表
     *
     * @param list
     */
    private void cacheGoodsList(ArrayList<GoodsData> list) {
        synchronized (synObj) {
            //这里需要做同步处理
            //保存商品列表数据: 可能有多个小分类的商品列表
            if (!ListUtil.isEmpty(list)) {
                for (GoodsData item : list) {
                    if (item != null) {
                        goodsListMap.put(item.getcId(), item.getList());
                    }
                }
            }
            //TODO 这里可以增加对数据过多的情况的处理：如删除最开始的数据，保留当前列表附近的数据
            //把删除的数据缓存到本地文件中，减少内存占用
        }
    }


    /**
     * 开始生成要显示到UI上的商品列表数据
     *
     * @param type
     * @param cIds 小分类id列表
     * @return
     */
    private Pair<ArrayList<GoodsItem>, ArrayList<GoodsItem>> buildUiGoodsList(int type, String[] cIds) {
        boolean isAfterCurrentCategory = false;
        String firstSubCId = ListUtil.getFirstItem(cIds);

        ArrayList<GoodsItem> beforeList = new ArrayList<>(); //保存请求分类id之前的数据
        ArrayList<GoodsItem> afterList = new ArrayList<>(); //保存包括请求分类id之后的数据

        int prePosition = subCategoryIdsMap.indexOf(firstSubCId);
        if (prePosition < 0) {
            prePosition = 0;
        }
        //只能下面两个位置能触发加载下一页
        int firstPosition = prePosition; //第一个位置：能触发加载下一页的空商品
        int lastPosition = POSITION_NONE; //最后一个位置：能触发加载下一页的空商品
        int i = 0;
        for (Map.Entry<String, ArrayList<GoodsItem>> entry : goodsListMap.entrySet()) {
            if (ListUtil.isEmpty(entry.getValue())) {
                if (i < prePosition) {
                    firstPosition = i;
                } else if (i > prePosition) {
                    lastPosition = i;
                    break;
                }
            }
            i++;
        }
        Log.e(TAG, "loadGoodsList() empty goodsList   prePosition:" + prePosition + ",firstPosition:" + firstPosition + ", lastPosition:" + lastPosition);
        int tempIndex = 0;
        for (Map.Entry<String, ArrayList<GoodsItem>> entry : goodsListMap.entrySet()) {
            String subCId = entry.getKey();
            SubCategoryItem subCategoryItem = subCategoryMap.get(subCId);
            if (subCategoryItem == null) {
                continue;
            }
            if (ObjectsCompat.equals(subCId, firstSubCId)) {
                isAfterCurrentCategory = true;
            }
            //子分类的商品列表
            ArrayList<GoodsItem> goodsList = entry.getValue();
            if (ListUtil.isEmpty(goodsList)) {
                //TODO 商品列表数据为空，生成一个空的数据，用于UI展示
                Log.e(TAG, "loadGoodsList() empty goodsList item:" + subCategoryItem.getName());
                boolean canLoadNext = firstPosition == tempIndex || lastPosition == tempIndex;
                GoodsItem goodsItem = getEmptyGoods(subCategoryItem, canLoadNext);
                if (isAfterCurrentCategory) {
                    afterList.add(goodsItem);
                } else {
                    beforeList.add(goodsItem);
                }
            } else {
                int index = 0;
                for (GoodsItem goodsItem : goodsList) {
                    if (goodsItem == null) {
                        continue;
                    }
                    goodsItem.setcId(subCategoryItem.getcId());
                    goodsItem.setName(subCategoryItem.getName());
                    goodsItem.setShowCategory(index == 0);//在第一个商品显示对应的小分类名称
                    if (isAfterCurrentCategory) {
                        afterList.add(goodsItem);
                    } else {
                        beforeList.add(goodsItem);
                    }
                    index++;
                }
            }
            tempIndex++;
        }

        Log.e(TAG, "loadGoodsList() type:" + type + ", beforeList size:" + beforeList.size() + ", afterList size:" + afterList.size());

        if (!ListUtil.isEmpty(afterList)) {
            beforeList.addAll(afterList);
        }
        return new Pair<>(beforeList, afterList);
    }


    /**
     * 获取一个空的商品数据
     *
     * @param subCategoryItem
     * @param canLoadNext
     * @return
     */
    @NonNull
    private GoodsItem getEmptyGoods(@NonNull SubCategoryItem subCategoryItem, boolean canLoadNext) {
        GoodsItem goodsItem = new GoodsItem();
        goodsItem.setcId(subCategoryItem.getcId());
        goodsItem.setName(subCategoryItem.getName());
        goodsItem.setShowCategory(true);
        goodsItem.setGoodsId(canLoadNext ? null : DEFAULT_GOODS_ID);
        return goodsItem;
    }


    /**
     * 获取位置
     *
     * @param cId       小分类id
     * @param goodsList
     * @return 没有找到返回：-1
     */
    private int getGoodsPosition(String cId, ArrayList<GoodsItem> goodsList) {
        int size = ListUtil.getCount(goodsList);
        for (int i = 0; i < size; i++) {
            GoodsItem item = ListUtil.getItemAt(goodsList, i);
            if (item != null && ObjectsCompat.equals(item.getcId(), cId)) {
                return i;
            }
        }
        return POSITION_NONE;
    }

    /**
     * 更新当前的大分类
     *
     * @param position 对应的位置,从0开始
     * @param item
     */
    public void updateCurrentCategory(int position, CategoryItem item) {
        if (item == null) {
            return;
        }
        Pair<Integer, CategoryItem> currentItem = currentCategory.getValue();
        if (currentItem != null && item.isSame(currentItem.second)) {
            //相同的分类
            Log.e(TAG, "updateCurrentCategory() the same category:" + item.getName());
            return;
        }
        currentCategory.postValue(new Pair<>(position, item));
        //对应的当前小分类切换到第一个
        SubCategoryItem subItem = ListUtil.getFirstItem(item.getList());
        if (subItem != null) {
            updateCurrentSubCategory(0, subItem);
        }
    }


    /**
     * 更新当前的小分类
     *
     * @param position 对应的位置,从0开始
     * @param item
     */
    public void updateCurrentSubCategory(int position, SubCategoryItem item) {
        if (item == null) {
            return;
        }
        Pair<Integer, SubCategoryItem> currentItem = currentSubCategory.getValue();
        if (currentItem != null && item.isSame(currentItem.second)) {
            //相同的分类
            Log.e(TAG, "updateCurrentCategory() the same subCategory:" + item.getName());
            return;
        }
        currentSubCategory.postValue(new Pair<>(position, item));
        //加载当前小分类的商品列表数据
        loadGoodsList(TYPE_LOAD_INIT, item.getcId());
    }

    /**
     * 列表滚动时，实时判断是否加载新的小分类数据
     *
     * @param position
     * @param itemCount
     * @param item
     */
    public void bindData(int position, int itemCount, GoodsItem item) {
        if (item == null) {
            return;
        }
        if (TextUtils.isEmpty(item.getGoodsId())) {
            Log.e(TAG, "bindData() empty goodsList, start load goodsList position:" + position + ", item:" + item);
            //空数据，需要加载数据
            loadGoodsList(TYPE_LOAD_NEXT, item.getcId());
        } else {
            //提前加载数据
            if (position + PRE_LOAD_COUNT >= itemCount) {
                String nextCId = getNextCIdWithEmptyGoodsList(item.getcId());
                Log.e(TAG, "bindData() getNextCIdWithEmptyGoodsList() nextCId:" + nextCId);
                if (!TextUtils.isEmpty(nextCId)) {
                    loadGoodsList(TYPE_LOAD_NEXT, nextCId);
                }
            }
        }
    }

    /**
     * 更新当前的分类，不加载商品列表(用于商品列表滚动时)
     *
     * @param item
     */
    public void updateCategory(GoodsItem item) {
        if (item == null) {
            return;
        }
        Pair<Integer, SubCategoryItem> value = currentSubCategory.getValue();
        String cId = item.getcId(); //小分类id
        if (value == null || value.second == null
                || !ObjectsCompat.equals(value.second.getcId(), cId)) {
            //更新小分类
            currentSubCategory.setValue(new Pair<>(getSubCatePosition(cId), subCategoryMap.get(cId)));
            //更新大分类
            SubCategoryItem subCategoryItem = subCategoryMap.get(cId);
            if (subCategoryItem != null) {
                String bigCId = subCategoryItem.getBigCId();
                CategoryItem categoryItem = categoryMap.get(bigCId);
                currentCategory.setValue(new Pair<>(getCatePosition(bigCId), categoryItem));
            }
        }
    }

    /**
     * 获取大分类的数据位置
     *
     * @param bigCId 大分类id
     * @return
     */
    private int getCatePosition(String bigCId) {
        CategoryItem item = categoryMap.get(bigCId);
        return item == null ? POSITION_DEFAULT : item.getPosition();
    }

    /**
     * 获取子分类的数据位置
     *
     * @param cId 小分类id
     * @return
     */
    private int getSubCatePosition(String cId) {
        SubCategoryItem subCategoryItem = subCategoryMap.get(cId);
        return subCategoryItem == null ? POSITION_DEFAULT : subCategoryItem.getPosition();

    }


    /**
     * 获取下一个空商品列表的小分类id
     *
     * @param cId 小分类id
     * @return
     */
    private String getNextCIdWithEmptyGoodsList(String cId) {
        int position = -1;
        int i = 0;
        for (Map.Entry<String, ArrayList<GoodsItem>> entry : goodsListMap.entrySet()) {
            String subCId = entry.getKey();
            SubCategoryItem subCategoryItem = subCategoryMap.get(subCId);
            if (subCategoryItem == null) {
                continue;
            }
            if (position == -1 && ObjectsCompat.equals(subCId, cId)) {
                position = i;
            }
            ArrayList<GoodsItem> goodsList = entry.getValue();
            i += ListUtil.getCount(goodsList);
            if (ListUtil.isEmpty(goodsList) && position >= 0) {
                return subCId;
            }
        }
        return null;

    }
}
