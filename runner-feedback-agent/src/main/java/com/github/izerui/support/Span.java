package com.github.izerui.support;

import com.github.izerui.context.Context;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * trace跟踪中的一个跨度记录
 */
@Data
@Builder
public class Span {

    // 是否是第一个请求入口
    protected boolean rootInComming;
    // 跟踪id
    protected String traceId;
    // 当前对象持有者所属类
    protected Class declaringClass;
    // 当前对象执行的方法所属类
    protected Class targetClass;
    // 是否成功调用
    protected boolean success;
    // 文件名
    protected String fileName;
    // 方法
    protected Method method;
    // 方法的JNI描述符 参看: https://docs.oracle.com/javase/7/docs/technotes/guides/jni/spec/types.html#wp16432
    protected String descriptor;
    // 开始时间戳
    protected long start;
    // 完成时间戳
    protected long end;
    // 线程名
    protected String threadName;
    // 方法所属行号
    protected int methodLine;
    // 外部调用者
    protected String inComingClassName;
    // 外部调用者方法名
    protected String inComingMethodName;
    // 外部调用者方法描述符
    protected String inComingMethodDescriptor;
    // 只是一个标记, 用法一: 表示是否增加到树中
    protected transient Integer mark;
    // 子集span
    private List<Span> children;

    /**
     * 当前执行类+方法+描述符的标识key
     *
     * @return
     */
    public String getComingKey() {
        String comingClassName = Context.getOriginName(getDeclaringClass().getName(), "$$");
        String comingMethodName = Context.getOriginName(method.getName(), "$");
        String descriptor = getDescriptor();
        return comingClassName + "#" + comingMethodName + descriptor;
    }

    /**
     * 外部调用者类+方法+描述符的标识key
     *
     * @return
     */
    public String getParentComingKey() {
        String parentComingClassName = Context.getOriginName(inComingClassName, "$$");
        String parentComingMethodName = Context.getOriginName(inComingMethodName, "$");
        String parentDescriptor = inComingMethodDescriptor;
        return parentComingClassName + "#" + parentComingMethodName + parentDescriptor;
    }

    /**
     * 从当前节点循环树
     *
     * @param hook
     */
    public void cycle(CycleHook<Span> hook) {
        Cycle<Span> cycle = new Cycle<Span>() {
            @Override
            protected boolean perform(Span item, Span parent, Span root, int level) {
                return hook.execute(item, parent, root, level);
            }

            @Override
            protected void performed(Span item, Span parent, Span root, int level) {
                hook.executed(item, parent, root, level);
            }

            @Override
            protected Collection<Span> getChildren(Span item) {
                return item.getChildren();
            }
        };
        cycle.cycle(this);
    }


    /**
     * 从当前节点打印树
     *
     * @param printLine
     */
    public void printTree(ILine printLine) {
        List<String> lines = new ArrayList<>();
        appendLines(lines, "", true, printLine);
        for (String line : lines) {
            System.out.println(line);
        }
    }

    /**
     * 解析当前line 添加到 lines集合
     *
     * @param lines  lines集合
     * @param prefix 前缀
     * @param isTail 是否tab
     * @param line   当前line
     */
    private void appendLines(List<String> lines, String prefix, boolean isTail, ILine line) {
        if (prefix == null) {
            prefix = "";
        }
        if (prefix.equals("")) {
            lines.add(line.getLine(this));
        } else {
            lines.add(prefix + (isTail ? "└── " : "├── ") + line.getLine(this));
        }
        List<Span> children = getChildren();
        if (children == null) {
            return;
        }
        for (int i = 0; i < children.size() - 1; i++) {
            this.children.get(i).appendLines(lines, prefix + (isTail ? "    " : "│   "), false, line);
        }
        if (this.children.size() > 0) {
            this.children.get(this.children.size() - 1)
                    .appendLines(lines, prefix + (isTail ? "    " : "│   "), true, line);
        }
    }

}