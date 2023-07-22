package com.github.izerui.support;

import com.github.izerui.ansi.AnsiColor;
import com.github.izerui.ansi.AnsiOutput;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.text.StringSubstitutor;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * trace跟踪中的一个跨度记录
 */
@Data
@Builder
public class Span extends Stack {

    // 当前方法调用的帧
    protected List<StackWalker.StackFrame> stackFrames;
    // 被拦截的目标对象
    protected Object target;
    // 当前方法
    protected Method method;
    // 入参
    protected Object[] argumengts;
    // 是否是第一个请求入口
    protected boolean rootInComming;
    // 跟踪id
    protected String traceId;
    // 是否成功调用
    protected boolean success;
    // 调用次数
    protected int count;
    // 耗时
    protected long time;
    // 线程名
    protected String threadName;
    // 方法所属行号
    protected int methodLine;
    // 只是一个标记, 用法一: 表示是否增加到树中
    protected transient Integer mark;
    // 子集span
    private List<Span> children;

    public List<Span> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    /**
     * 获取方法声明所在的类
     *
     * @return
     */
    public String getDeclaringClassName() {
        return method.getDeclaringClass().getName();
    }

    /**
     * 获取执行方法名
     *
     * @return
     */
    public String getMethodName() {
        String methodName = super.getMethodName();
        String currentMethodName = method.getName();
        Assert.that(Objects.equals(methodName, currentMethodName), String.format("getMethodName 帧中 %s 与 %s 不一致", methodName, currentMethodName));
        return methodName;
    }

    /**
     * 获取当前类的包名
     *
     * @return
     */
    public String getPackage() {
        String className = getClassName();
        return className.substring(0, className.lastIndexOf("."));
    }

    /**
     * 获取方法声明所在的类的包名
     *
     * @return
     */
    public String getDeclaringPackage() {
        String className = getDeclaringClassName();
        return className.substring(0, className.lastIndexOf("."));
    }

    /**
     * 判断当前节点及下级节点是否包含指定id
     *
     * @param id
     * @return
     */
    public boolean isContains(String id) {
        AtomicBoolean isContains = new AtomicBoolean(false);
        cycle(new CycleHook<Span>() {
            @Override
            public boolean execute(Span item, Span parent, Span root, Integer level) {
                boolean contains = item.getId().equals(id);
                if (contains) {
                    isContains.set(contains);
                    // 如果包含则退出下层循环
                    return false;
                }
                return true;
            }
        });
        return isContains.get();
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

    /**
     * 根据Substitutor模版进行替换
     *
     * @param templateStr 模版
     * @return 替换后的字符串
     */
    public String getSubstitutorStr(String templateStr) {
        Map<String, Object> variables = new HashMap<>();
        // 是否成功
        variables.put("success", this.success ? AnsiOutput.toString(AnsiColor.GREEN, "[T]") : AnsiOutput.toString(AnsiColor.RED, "[F]"));
        // 耗时
        variables.put("time", this.time + "ms");
        // 调用次数
        variables.put("count", AnsiOutput.toString(AnsiColor.RED, this.count > 1 ? "[" + this.count + "]" : ""));
        // 包名
        variables.put("package", AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, this.getDeclaringPackage()));
        // 文件名
        variables.put("file", this.getFileName());
        // 行号
        variables.put("line", this.getLine());
        // 方法
        variables.put("method", AnsiOutput.toString(AnsiColor.YELLOW, this.getMethodName()));
        // 方法描述符
        variables.put("descriptor", AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, this.getDescriptor()));
        // 线程名
        variables.put("thread", this.threadName);
        StringSubstitutor substitutor = new StringSubstitutor(variables);
        return substitutor.replace(templateStr);
    }

    // for test
//    private String id;
//    private List<String> parentIds;
//
//    public String getId() {
//        return id;
//    }
//
//    public List<String> getParentIds() {
//        if (parentIds == null) {
//            parentIds = new ArrayList<>();
//        }
//        return parentIds;
//    }

}