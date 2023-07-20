package com.github.izerui.context;

import com.github.izerui.ansi.AnsiColor;
import com.github.izerui.ansi.AnsiOutput;
import com.github.izerui.support.TreeMapper;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class TracerContext {

    private final static ThreadLocal<Tracer> TRACER_THREAD_LOCAL = new InheritableThreadLocal<>();


    /**
     * 添加一个tracer, 第一个添加的tracer返回ture
     *
     * @param tracer
     */
    public static boolean addTracerAndReturnTrue(Tracer tracer) {
        if (TRACER_THREAD_LOCAL.get() == null) {
            TRACER_THREAD_LOCAL.set(tracer);
            return true;
        }
        return false;
    }

    /**
     * 返回当前跟踪
     *
     * @return
     */
    public static Tracer getTracer() {
        return TRACER_THREAD_LOCAL.get();
    }

    /**
     * trace跟踪
     */
    @Data
    @Builder
    public static class Tracer {
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
            Tracer tracer = TRACER_THREAD_LOCAL.get();
            if (tracer != null) {
                List<Span> trees = tracer.getTreeSpans();
                System.out.println("☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟");
                System.out.println(AnsiOutput.toString(AnsiColor.GREEN, String.format("【time:{} name:%s traceId:%s time:%s】", new Date(start), name, id, (end - start) + "ms")));
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
        }

        private List<Span> getTreeSpans() {
            TreeMapper<Span, Span> treeMapper = new TreeMapper<Span, Span>() {
                @Override
                protected boolean isRoot(Span item) {
                    return item.isRootInComming();
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
                protected void addChild(Span child, Span parent) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(child);
                }
            };
            return treeMapper.treeMap(spans);
        }

    }

    /**
     * trace跟踪中的一个跨度记录
     */
    @Data
    @Builder
    public static class Span {

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
            return comingClassName + comingMethodName + descriptor;
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
            return parentComingClassName + parentComingMethodName + parentDescriptor;
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

    interface ILine {
        String getLine(Span item);
    }


    /**
     * 循环树操作类
     *
     * @param <T> 循环节点类型
     */
    public abstract static class Cycle<T> {

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
}