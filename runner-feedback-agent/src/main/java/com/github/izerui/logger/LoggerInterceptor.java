package com.github.izerui.logger;

import com.github.izerui.annotation.Tracer;
import com.github.izerui.context.Context;
import com.github.izerui.context.MethodContext;
import com.github.izerui.context.TracerContext;
import com.github.izerui.support.Span;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * 执行拦截器
 *
 * @author liuyuhua
 */
public class LoggerInterceptor {

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
        Tracer tracerAnnotation = method.getAnnotation(Tracer.class);
        boolean success = true;
        // true: 表示是入口方法
        boolean rootInComming = false;
        if (tracerAnnotation != null && TracerContext.getTracer() == null) {
            rootInComming = TracerContext.addTracerAndReturnTrue(
                    com.github.izerui.support.Tracer.builder()
                            .id(Context.generateTraceId())
                            .name(tracerAnnotation.value())
                            .start(start)
                            .spans(new ArrayList<>())
                            .build()
            );
        }
        try {
            return callable.call();
        } catch (Exception e) {
            success = false;
            // 进行异常信息上报
            throw e;
        } finally {
            try {
                long end = System.currentTimeMillis();
                List<StackWalker.StackFrame> stackFrames = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(stackFrameStream -> stackFrameStream.collect(Collectors.toList()));
                StackWalker.StackFrame currentStackFrame = stackFrames.get(1);
                // 外部对象调用者
                StackWalker.StackFrame inComingStackFrame = Context.getInComingStackFrame(stackFrames);
                // 当tracer存在，则后续跨度及子线程都需要拦截记录
                com.github.izerui.support.Tracer tracer = TracerContext.getTracer();
                if (tracer != null) {
                    int methodLine = MethodContext.getLine(method.getDeclaringClass().getName(), method.getName(), currentStackFrame.getDescriptor());
                    // 当能获取到本地类路径的方法行号则记录， 或者deepshow参数为true表示指定包下对象的父类方法也记录
                    if (methodLine != -1 || Context.DEEP_SHOW) {
                        tracer.addSpan(Span.builder()
                                .rootInComming(rootInComming)
                                .success(success)
                                .targetClass(target.getClass())
                                .declaringClass(method.getDeclaringClass())
                                .fileName(currentStackFrame.getFileName())
                                .descriptor(currentStackFrame.getDescriptor())
                                .start(start)
                                .end(end)
                                .threadName(Thread.currentThread().getName())
                                .method(method)
                                .methodLine(methodLine)
                                .currentClassName(currentStackFrame.getClassName())
                                .currentMethodName(currentStackFrame.getMethodName())
                                .currentMethodDescriptor(currentStackFrame.getDescriptor())
                                .inComingClassName(inComingStackFrame.getClassName())
                                .inComingMethodName(inComingStackFrame.getMethodName())
                                .inComingMethodDescriptor(inComingStackFrame.getDescriptor())
                                .build());
                    }
                    if (rootInComming) {
                        tracer.setEnd(end);
                        if (Context.DEBUGGER) {
                            System.out.println("=================================================");
                            List<Span> spans = tracer.getSpans();
                            for (Span span : spans) {
                                System.out.println(span.getComingKey());
                                System.out.println(span.getParentComingKey());
                                System.out.println();
                            }
                            System.out.println("=================================================");
                            tracer.print(tracer.getSpans());
                        }
                        tracer.print(tracer.getTreeSpans());
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
