package com.github.izerui.logger;

import net.bytebuddy.implementation.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * 执行拦截器
 */
public class LoggerInterceptor {

    private final static Logger LOGGER = LoggerFactory.getLogger("【Bytebuddy】");

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
        try {
            return callable.call();
        } catch (Exception e) {
            // 进行异常信息上报
            throw e;
        } finally {
            try {
                String className = target.getClass().getName();
                String packageName = target.getClass().getPackageName();
                String baseClassName = target.getClass().getSimpleName();
                int lineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
                LOGGER.info("{}({}.java:{})#{}: ({}ms) {}", packageName, baseClassName, lineNumber, method.getName(), (System.currentTimeMillis() - start), argumengts);
            } catch (Exception ex) {
                ;
            }
        }
    }

    private static Optional<StackTraceElement> getStackTrace0(String className) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return Arrays.stream(stackTrace).filter(stackTraceElement -> stackTraceElement.getClassName().equals(className)).findFirst();
    }
}
