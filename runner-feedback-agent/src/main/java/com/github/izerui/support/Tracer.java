package com.github.izerui.support;

import com.github.izerui.ansi.AnsiColor;
import com.github.izerui.ansi.AnsiOutput;
import com.github.izerui.context.Context;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * trace跟踪
 */
@Data
@Builder
public class Tracer {
    // 唯一的traceId
    private String id;
    // trace跟踪名称
    private String name;
    // 开始时间戳
    protected long start;
    // 完成时间戳
    protected long end;
    // trace下面的跨度记录集合
    private List<Span> spans;

    /**
     * 添加一个span
     *
     * @param span
     */
    public void addSpan(Span span) {
        if (spans == null) {
            spans = new ArrayList<>();
        }
        span.traceId = this.getId();
        spans.add(span);
    }

    /**
     * 输出打印树状请求链路
     */
    public void print() {
        List<Span> trees = getTreeSpans();
        System.out.println("☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟");
        System.out.println(AnsiOutput.toString(AnsiColor.GREEN, String.format("【start:%s name:%s traceId:%s time:%s】", Context.DATE_TIME_FORMATTER.format(new Date(start)), name, id, (end - start) + "ms")));
        for (Span span : trees) {
            span.printTree(item -> String.format("%s %s  %s(%s:%s)#%s %s 【%s】",
                    // 是否成功
                    item.success ? AnsiOutput.toString(AnsiColor.GREEN, "[T]") : AnsiOutput.toString(AnsiColor.RED, "[F]"),
                    // 耗时
                    AnsiOutput.toString(AnsiColor.YELLOW, (item.end - item.start) + "ms"),
                    // 包名
                    (!item.targetClass.equals(item.declaringClass) && item.methodLine == -1) ? item.targetClass.getPackageName() : item.declaringClass.getPackageName(),
                    // 文件名
                    AnsiOutput.toString(AnsiColor.BLUE, item.fileName),
                    // 行号
                    item.methodLine,
                    // 方法
                    AnsiOutput.toString(AnsiColor.BRIGHT_MAGENTA, item.method.getName()),
                    // 方法描述符
                    AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, item.descriptor),
                    // 线程名
                    item.threadName));
        }
        System.out.println("☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝");
    }

    /**
     * 生成树
     *
     * @return
     */
    private List<Span> getTreeSpans() {
        TreeMapper<Span, Span> treeMapper = new TreeMapper<Span, Span>() {
            @Override
            protected boolean isRoot(Span item) {
                // 入口span 或者 非指定包下的class类的span
                return item.isRootInComming() || !Context.matchPackages(item.inComingClassName);
            }

            @Override
            protected boolean isParent(Span child, Span parent) {
                return child.getParentComingKey().equals(parent.getComingKey());
            }

            @Override
            protected Span map(Span item, Span parent) {
                return item;
            }

            @Override
            protected void mark(Span source, Span target) {
                source.mark = 1;
            }

            @Override
            protected void addChild(Span child, Span parent) {
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(child);
            }
        };
        List<Span> trees = treeMapper.treeMap(spans);
        Collections.reverse(trees);
        return trees;
    }

}