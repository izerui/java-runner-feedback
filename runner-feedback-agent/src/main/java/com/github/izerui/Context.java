package com.github.izerui;

import com.github.izerui.structure.ClassMethodLine;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class Context {

    private final static Logger LOGGER = LoggerFactory.getLogger(Context.class);

    private final static String[] packages;

    private final static String[] ignorePackages = {"com.github.izerui"};

    private final static List<ClassMethodLine> classMethodLines = new ArrayList<>();

    static {
        String feedbackPackages = System.getProperties().getProperty("feedback.packages");
        if (feedbackPackages != null && !"".equals(feedbackPackages)) {
            packages = feedbackPackages.split(",");
        } else {
            LOGGER.warn("未获取到正确的可【feedback】的包匹配字符串, 请正确配置agent类似: -javaagent:runner-feedback-agent-jar-with-dependencies.jar -Dfeedback.packages=com.github.sample,com.yj2025");
            packages = new String[0];
        }
    }

    public static boolean matchPackages(String className) {
        for (String ignorePackage : ignorePackages) {
            if (className.startsWith(ignorePackage)) {
                return false;
            }
        }
        for (String aPackage : packages) {
            if (className.startsWith(aPackage)) {
                return true;
            }
        }
        return false;
    }

    public static ElementMatcher<? super TypeDescription> getTypeMatcher() {
        ElementMatcher.Junction<? super TypeDescription> matcher = ElementMatchers.any();
        for (String ignorePackage : ignorePackages) {
            matcher = matcher.and(ElementMatchers.not(ElementMatchers.nameStartsWith(ignorePackage)));
        }
        ElementMatcher.Junction<? super TypeDescription> orMatcher = ElementMatchers.none();
        for (String aPackage : packages) {
            orMatcher = orMatcher.or(ElementMatchers.nameStartsWith(aPackage));
        }
        return matcher.and(orMatcher);
    }

    public static void addMethodLine(String className, String methodName, String descriptor, int line) {
        classMethodLines.add(new ClassMethodLine(className, methodName, descriptor, line));
    }

    public static int getClassMethodLine(String className, Method method) {
        for (ClassMethodLine classMethodLine : classMethodLines) {
            if (className.equals(classMethodLine.getClassName())) {
                if (method.getName().equals(classMethodLine.getMethodName())) {
                    // TODO 解析 参数类型进行匹配
                    return classMethodLine.getLine();
                }
            }
        }
        return 0;
    }
}
