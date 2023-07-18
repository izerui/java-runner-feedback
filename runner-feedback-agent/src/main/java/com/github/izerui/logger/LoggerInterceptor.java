package com.github.izerui.logger;

import com.github.izerui.Context;
import com.github.izerui.ansi.AnsiColor;
import com.github.izerui.ansi.AnsiOutput;
import com.github.izerui.structure.ClassMethodLine;
import net.bytebuddy.implementation.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * 执行拦截器
 */
public class LoggerInterceptor {

    private final static Logger LOGGER = LoggerFactory.getLogger("【Feedback】");

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
            result = callable.call();
            return result;
        } catch (Exception e) {
            // 进行异常信息上报
            throw e;
        } finally {
            try {
                Class declaringClass = method.getDeclaringClass();
                String declaringPackageName = declaringClass.getPackageName();
                String declaringBaseClassName = declaringClass.getSimpleName();
                int methodLine = Context.getClassMethodLine(method);
                System.out.println(String.format("%s [%s]【%s】 %s(%s.java:%s)%s %s %s => %s",
                        LocalDateTime.now().format(DATE_TIME_FORMATTER).toString(),
                        Thread.currentThread().getName(),
                        AnsiOutput.toString(AnsiColor.MAGENTA, Context.getTraceId()),
                        declaringPackageName,
                        declaringBaseClassName,
                        methodLine,
                        AnsiOutput.toString(AnsiColor.YELLOW, "#".concat(method.getName())),
                        AnsiOutput.toString(AnsiColor.BRIGHT_MAGENTA, (System.currentTimeMillis() - start) + "ms"),
                        AnsiOutput.toString(AnsiColor.CYAN, argumengts),
                        AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, result)));
//                LOGGER.info("traceId:【{}】 {}({}.java:{}){} {} {} => {}",
//                        AnsiOutput.toString(AnsiColor.BRIGHT_YELLOW, Context.getTraceId()),
//                        declaringPackageName,
//                        declaringBaseClassName,
//                        methodLine,
//                        AnsiOutput.toString(AnsiColor.YELLOW, "#".concat(method.getName())),
//                        AnsiOutput.toString(AnsiColor.BRIGHT_MAGENTA, (System.currentTimeMillis() - start) + "ms"),
//                        AnsiOutput.toString(AnsiColor.CYAN, argumengts),
//                        AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, result));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
