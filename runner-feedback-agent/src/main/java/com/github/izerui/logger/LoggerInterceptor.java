package com.github.izerui.logger;

import com.github.izerui.Context;
import com.github.izerui.annotation.Feedback;
import com.github.izerui.ansi.AnsiColor;
import com.github.izerui.ansi.AnsiOutput;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * 执行拦截器
 */
public class LoggerInterceptor {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 进行方法拦截, 注意这里可以对所有修饰符的修饰的方法（包含private的方法）进行拦截
     *
     * @param method   待处理方法
     * @param callable 原方法执行
     * @return 执行结果
     */
    @RuntimeType
    public static Object intercept(
            // 被拦截的目标对象 （动态生成的目标对象）
            @This Object target,
            // 正在执行的方法Method 对象（目标对象父类的Method）
            @Origin Method method,
            // 正在执行的方法的全部参数
            @AllArguments Object[] argumengts,
            // 目标对象的一个代理
            @Super Object delegate,
            // 方法的调用者对象 对原始方法的调用依靠它
            @SuperCall Callable<?> callable
    ) throws Exception {
        long start = System.currentTimeMillis();
        Object result = null;
        try {
            Context.Trace.generateIfNone(method.getAnnotation(Feedback.class));
            result = callable.call();
            return result;
        } catch (Exception e) {
            // 进行异常信息上报
            throw e;
        } finally {
            try {
                List<StackWalker.StackFrame> frames = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(stackFrameStream -> {
                    return stackFrameStream.collect(Collectors.toList());
                });
                Class targetClass = target.getClass();
                Class declaringClass = method.getDeclaringClass();
                int methodLine = Context.getClassMethodLine(method);
                if (methodLine != -1 || Context.DEEP_SHOW) {
                    System.out.println(String.format("%s [%s]【%s】 %s  %s(%s.java:%s)#%s %s => %s",
                            // 时间
                            LocalDateTime.now().format(DATE_TIME_FORMATTER).toString(),
                            // 线程名
                            Thread.currentThread().getName(),
                            // traceId
                            AnsiOutput.toString(AnsiColor.GREEN, Context.Trace.getTraceNameId()),
                            // 耗时
                            AnsiOutput.toString(AnsiColor.BRIGHT_MAGENTA, (System.currentTimeMillis() - start) + "ms"),
                            // 包名
                            (!targetClass.equals(declaringClass) && methodLine == -1) ? targetClass.getPackageName() : declaringClass.getPackageName(),
                            // 类名
                            (!targetClass.equals(declaringClass) && methodLine == -1) ? getOriginClassName(targetClass.getSimpleName()) : getOriginClassName(declaringClass.getSimpleName()),
                            // 行号
                            methodLine,
                            // 方法
                            AnsiOutput.toString(AnsiColor.YELLOW, method.getName()),
                            // 入参
                            AnsiOutput.toString(AnsiColor.CYAN, argumengts),
                            // 返回值
                            AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, result)));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @IgnoreForBinding
    private static String getOriginClassName(String proxyClassName) {
        int proxySplitIndex = proxyClassName.indexOf("$$");
        if (proxySplitIndex > -1) {
            return proxyClassName.substring(0, proxyClassName.indexOf("$$"));
        }
        return proxyClassName;
    }

}
