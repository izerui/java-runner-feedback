package com.github.izerui.support;

import com.github.izerui.ansi.AnsiColor;
import com.github.izerui.ansi.AnsiOutput;
import com.github.izerui.context.Context;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

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
     * 添加一个span, 如果相同的调用有多次，则不重复添加，只是在原来的基础上计数+1，耗时累加
     *
     * @param span
     */
    public void addSpan(Span span) {
        if (spans == null) {
            spans = new ArrayList<>();
        }
        span.traceId = this.getId();
        Optional<Span> first = spans.stream().filter(sp -> sp.getKey().equals(span.getKey()) && sp.getParentKey().equals(span.getParentKey())).findFirst();
        if (first.isPresent()) {
            first.get().count++;
            first.get().time += span.time;
        } else {
            spans.add(span);
        }
    }

    /**
     * 输出打印树状请求链路
     */
    public void print() {
        System.out.println("☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟");
        System.out.println(AnsiOutput.toString(AnsiColor.GREEN, String.format("【start:%s name:%s traceId:%s time:%s】", Context.DATE_TIME_FORMATTER.format(new Date(start)), name, id, (end - start) + "ms")));
        for (Span span : getTreeSpans()) {
            span.printTree(item -> String.format("%s %s%s  %s(%s:%s)#%s %s 【%s】",
                    // 是否成功
                    item.success ? AnsiOutput.toString(AnsiColor.GREEN, "[T]") : AnsiOutput.toString(AnsiColor.RED, "[F]"),
                    // 耗时
                    AnsiOutput.toString(AnsiColor.YELLOW, item.time + "ms"),
                    // 调用次数
                    AnsiOutput.toString(AnsiColor.YELLOW, item.count > 1 ? "[" + item.count + "]" : ""),
                    // 包名
                    item.getCurrentPackage(),
                    // 文件名
                    item.fileName,
                    // 行号
                    item.methodLine,
                    // 方法
                    AnsiOutput.toString(AnsiColor.BRIGHT_MAGENTA, item.currentMethodName),
                    // 方法描述符
                    AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, item.currentMethodDescriptor),
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
    public List<Span> getTreeSpans() {
        for (Span parent : spans) {
            for (Span child : spans) {
                if (!parent.getKey().equals(parent.getParentKey())
                        && parent.getKey().equals(child.getParentKey())) {
                    parent.getChildren().add(child);
                    child.setMark(1);
                }
            }
        }
        List<Span> collect = spans.stream().filter(span -> span.mark == null).collect(Collectors.toList());
        Collections.reverse(collect);
        return collect;
    }

}