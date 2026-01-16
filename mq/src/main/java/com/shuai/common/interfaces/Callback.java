package com.shuai.common.interfaces;

/**
 * 通用回调接口
 *
 * @author Shuai
 */
public interface Callback<T> {

    /**
     * 操作成功回调
     *
     * @param result 结果
     */
    void onSuccess(T result);

    /**
     * 操作失败回调
     *
     * @param error 错误信息
     */
    void onFailure(String error);
}
