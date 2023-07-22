package com.github.izerui.support;

import com.github.izerui.ansi.AnsiColor;
import com.github.izerui.ansi.AnsiOutput;
import com.github.izerui.context.Context;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.text.StringSubstitutor;

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
        Optional<Span> first = spans.stream().filter(sp -> sp.getId().equals(span.getId())).findFirst();
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
            span.printTree(item -> item.getSubstitutorStr(Context.OUTPUT_FORMAT));
        }
        System.out.println("☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝");
    }

    /**
     * 生成树
     *
     * @return
     */
    public List<Span> getTreeSpans() {
        if (Context.DEBUGGER) {
            for (Span span : spans) {
                System.out.println(span.getId());
                for (String parentId : span.getParentIds()) {
                    System.out.println("    " + parentId);
                }
            }
        }

//        TreeMapper<Span,Span> treeMapper = new TreeMapper<Span, Span>() {
//            @Override
//            protected boolean isRoot(Span item) {
//                return item.isRootInComming();
//            }
//
//            @Override
//            protected boolean isParent(Span child, Span parent) {
//                return Objects.equals(child.getParentId(), parent.getId());
//            }
//
//            @Override
//            protected Span map(Span item, Span parent) {
//                return item;
//            }
//
//            @Override
//            protected void addChild(Span child, Span parent) {
//                parent.getChildren().add(child);
//            }
//        };
//        return treeMapper.treeMap(spans);

        for (Span span : spans) {
            if (span.isRootInComming()) {
                continue;
            }
            for (String parentId : span.getParentIds()) {
                // 找到父级调用者
                Span parent = spans.stream()
                        .filter(sp -> sp.getId().equals(parentId))
                        // 作为儿子的span不能拥有parent的id，否则就是死循环
                        .filter(sp -> !span.isContains(sp.getId()))
                        .findFirst()
                        .orElse(null);
                if (parent != null) {
                    parent.getChildren().add(span);
                    span.setMark(1);
                    break;
                }
            }
        }
        List<Span> collect = spans.stream().filter(span -> span.getMark() == null).collect(Collectors.toList());
        return collect;
    }

}