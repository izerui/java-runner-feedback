package com.github.izerui.logger;

import com.github.izerui.annotation.Tracer;
import com.github.izerui.context.Context;
import com.github.izerui.context.MethodContext;
import com.github.izerui.context.TracerContext;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * 执行拦截器
 * @author liuyuhua
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
        Tracer tracerAnnotation = method.getAnnotation(Tracer.class);
        // true: 表示是入口方法
        boolean inComming = false;
        if (tracerAnnotation != null && TracerContext.getTracer() == null) {
            inComming = TracerContext.addTracerAndReturnTrue(
                    TracerContext.Tracer.builder()
                            .id(generateTraceId())
                            .name(tracerAnnotation.value())
                            .start(start)
                            .spans(new ArrayList<>())
                            .build()
            );
        }
        try {
            return callable.call();
        } catch (Exception e) {
            // 进行异常信息上报
            throw e;
        } finally {
            try {
                long end = System.currentTimeMillis();
                List<StackWalker.StackFrame> stackFrames = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(stackFrameStream -> stackFrameStream.collect(Collectors.toList()));
                StackWalker.StackFrame currentStackFrame = stackFrames.get(1);

                // 当tracer存在，则后续跨度及子线程都需要拦截记录
                TracerContext.Tracer tracer = TracerContext.getTracer();
                if (tracer != null) {
                    int methodLine = MethodContext.getLine(method.getDeclaringClass().getName(), method.getName(), currentStackFrame.getDescriptor());
                    // 当能获取到本地类路径的方法行号则记录， 或者deepshow参数为true表示指定包下对象的父类方法也记录
                    if (methodLine != -1 || Context.DEEP_SHOW) {
                        tracer.addSpan(TracerContext.Span.builder()
                                .targetClass(target.getClass())
                                .declaringClass(method.getDeclaringClass())
                                .fileName(currentStackFrame.getFileName())
                                .descriptor(currentStackFrame.getDescriptor())
                                .start(start)
                                .end(end)
                                .threadName(Thread.currentThread().getName())
                                .method(method)
                                .methodLine(methodLine)
                                .build());
                    }
                    if (inComming) {
                        tracer.setEnd(end);
                        tracer.print();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @IgnoreForBinding
    public static StackWalker.StackFrame getCurrentStackFrame() {
        Optional<StackWalker.StackFrame> first = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(stackFrameStream -> {
                    return stackFrameStream.skip(1).limit(1);
                }).findFirst();
        return first.orElse(null);
    }

    @IgnoreForBinding
    private static String getOriginClassName(String proxyClassName) {
        int proxySplitIndex = proxyClassName.indexOf("$$");
        if (proxySplitIndex > -1) {
            return proxyClassName.substring(0, proxyClassName.indexOf("$$"));
        }
        return proxyClassName;
    }


    public static final String[] TRACE_CHARS = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    /**
     * 生成一个8位的随机串
     *
     * @return
     */
    @IgnoreForBinding
    private static String generateTraceId() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(TRACE_CHARS[x % 0x3E]);
        }
        return shortBuffer.toString();
    }

}
