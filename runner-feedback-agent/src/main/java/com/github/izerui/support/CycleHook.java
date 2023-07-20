package com.github.izerui.support;

/**
 * cycle钩子
 *
 * @param <T> 循环节点类型
 */
public interface CycleHook<T> {
    /**
     * 前置执行钩子，返回值决定是否循环下级 针对
     *
     * @param item   当前节点
     * @param parent 父节点 可能为null
     * @param root   根节点
     * @param level  当前级别
     * @return true:继续循环下级  false:当前执行后，停止循环下级
     */
    default boolean execute(T item, T parent, T root, Integer level) {
        return true;
    }

    /**
     * 后置执行钩子, 执行过下面子集后才执行的当前钩子
     *
     * @param item   当前节点
     * @param parent 父节点 可能为null
     * @param root   根节点
     * @param level  当前级别
     */
    default void executed(T item, T parent, T root, Integer level) {
    }

}