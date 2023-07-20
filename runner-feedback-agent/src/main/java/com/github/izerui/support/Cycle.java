package com.github.izerui.support;

import java.util.Collection;

/**
 * 循环树操作类
 *
 * @param <T> 循环节点类型
 */
public abstract class Cycle<T> {

    public void cycle(T t) {
        loop(t, null, t, 0);
    }

    public void cycle(Collection<T> list) {
        for (T item : list) {
            loop(item, null, item, 0);
        }
    }

    private void loop(T item, T parent, T root, int level) {

        boolean loopChild = perform(item, parent, root, level);

        if (loopChild) {
            Collection<T> children = getChildren(item);

            if (children != null) {
                for (T child : children) {
                    loop(child, item, root, level + 1);
                }
            }
        }

        performed(item, parent, root, level);

    }

    /**
     * 执行当前节点
     *
     * @param item   当前节点
     * @param parent 父节点
     * @param root   根节点
     * @param level  当前级别
     * @return true: 继续下面子集  false: 结束循环
     */
    protected abstract boolean perform(T item, T parent, T root, int level);


    /**
     * 执行当前节点后
     *
     * @param item   当前节点
     * @param parent 父节点
     * @param root   根节点
     * @param level  当前级别
     */
    protected abstract void performed(T item, T parent, T root, int level);

    protected abstract Collection<T> getChildren(T item);

}