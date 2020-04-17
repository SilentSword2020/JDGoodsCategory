package com.study.app.jd.goods.category.ui.main.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 列表相关工具方法
 */
public abstract class ListUtil {

    /**
     * 列表中指定index的item
     *
     * @param list
     * @param index
     * @param <T>
     * @return
     */
    @Nullable
    public static <T> T getItemAt(@Nullable List<T> list, int index) {
        if (list != null && index >= 0 && list.size() > index) {
            return list.get(index);
        }
        return null;
    }


    /**
     * 列表是否为空
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(@Nullable List<T> list) {
        return list == null || list.isEmpty();
    }

    /**
     * 列表的数量
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> int getCount(@Nullable List<T> list) {
        return list == null ? 0 : list.size();
    }

    /**
     * 列表的第一项
     *
     * @param list
     * @param <T>
     * @return
     */
    @Nullable
    public static <T> T getFirstItem(@Nullable List<T> list) {
        return getItemAt(list, 0);
    }

    /**
     * 列表的最后一项
     *
     * @param list
     * @param <T>
     * @return
     */
    @Nullable
    public static <T> T getLastItem(@Nullable List<T> list) {
        return getItemAt(list, getCount(list) - 1);
    }


    /**
     * 数组中指定index的item
     *
     * @param list
     * @param index
     * @param <T>
     * @return
     */
    @Nullable
    public static <T> T getItemAt(@Nullable T[] list, int index) {
        if (list != null && index >= 0 && list.length > index) {
            return list[index];
        }
        return null;
    }


    /**
     * 数组是否为空
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(@Nullable T[] list) {
        return list == null || list.length <= 0;
    }

    /**
     * 数组的数量
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> int getCount(@Nullable T[] list) {
        return list == null ? 0 : list.length;
    }

    /**
     * 数组的第一项
     *
     * @param list
     * @param <T>
     * @return
     */
    @Nullable
    public static <T> T getFirstItem(@Nullable T[] list) {
        return getItemAt(list, 0);
    }

    /**
     * 数组的最后一项
     *
     * @param list
     * @param <T>
     * @return
     */
    @Nullable
    public static <T> T getLastItem(@Nullable T[] list) {
        return getItemAt(list, getCount(list) - 1);
    }

    @NonNull
    public static <T> List<T> nullToEmpty(@Nullable List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    /**
     * 合并数组
     *
     * @param first
     * @param rest
     * @param <T>
     * @return
     */
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        if (rest == null || rest.length <= 0 || first == null) {
            return first;
        }
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array != null ? array.length : 0;
        }
        if (first.length == totalLength) {
            return first;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            if (array == null || array.length <= 0) {
                continue;
            }
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
