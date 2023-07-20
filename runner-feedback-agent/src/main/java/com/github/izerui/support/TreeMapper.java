package com.github.izerui.support;

import java.util.ArrayList;
import java.util.List;

/**
 * 将源数组转换成目标嵌套结构的数组
 * Created by serv on 2017/5/20.
 */
public abstract class TreeMapper<S, T> {

    /**
     * 判断是否是跟节点
     *
     * @param item 源对象
     * @return
     */
    protected abstract boolean isRoot(S item);

    /**
     * 判断 child 对象的父级是否是 parent
     *
     * @param child  源对象
     * @param parent 目标对象
     * @return
     */
    protected abstract boolean isParent(S child, T parent);

    /**
     * 转换源对象到目标对象
     *
     * @param item   源对象
     * @param parent 父对象 第一级为 null
     * @return 目标对象
     */
    protected abstract T map(S item, T parent);

    /**
     * 将目标子对象添加到目标父对象中
     *
     * @param child  子对象
     * @param parent 父对象
     */
    protected abstract void addChild(T child, T parent);


    /**
     * 映射集合到嵌套结构的集合中
     *
     * @param sourceList 源集合
     * @return
     */
    public List<T> treeMap(final List<S> sourceList) {
        List<T> results = new ArrayList<T>();
        for (S source : sourceList) {
            if (isRoot(source)) {
                T node = map(source, null);
                assemblyChildren(sourceList, node);
                results.add(node);
                mark(source, node);
            }
        }
        return results;

    }

    /**
     * 标记节点已经成功放入到树中
     *
     * @param source 源对象
     * @param target 映射对象
     */
    protected void mark(S source, T target) {

    }


    //组装子分类
    private void assemblyChildren(final List<S> sourceList, T parent) {
        for (S source : sourceList) {
            if (isParent(source, parent)) {
                T tChild = map(source, parent);
                //继续添加node 的下级node
                assemblyChildren(sourceList, tChild);
                addChild(tChild, parent);
                mark(source, tChild);
            }
        }
    }
}
