package com.github.izerui;

import com.github.izerui.ansi.AnsiOutput;
import com.github.izerui.structure.ClassMethodLine;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public final class Context {

    public final static int ASM_VERSION = Opcodes.ASM9;

    private final static String[] PACKAGES;

    private final static String[] IGNORE_PACKAGES = {
            "com.github.izerui",
            "org.springframework.cglib"
    };

    private final static Set<ClassMethodLine> CLASS_METHOD_LINES = new CopyOnWriteArraySet<>();

    private final static ThreadLocal<String> TRACE_ID = new InheritableThreadLocal<>();

    static {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        String feedbackPackages = System.getProperties().getProperty("feedback.packages");
        if (feedbackPackages != null && !"".equals(feedbackPackages)) {
            PACKAGES = feedbackPackages.split(",");
        } else {
            System.out.println("未获取到正确的可【feedback】的包匹配字符串, 请正确配置agent类似: -javaagent:~/runner-feedback-agent.jar -Dfeedback.packages=com.github.sample,com.yj2025");
            PACKAGES = new String[0];
        }
    }

    public static boolean matchPackages(String className) {
        for (String ignorePackage : IGNORE_PACKAGES) {
            if (className.startsWith(ignorePackage)) {
                return false;
            }
        }
        for (String aPackage : PACKAGES) {
            if (className.startsWith(aPackage)) {
                return true;
            }
        }
        return false;
    }

    public static ElementMatcher<? super TypeDescription> getTypeMatcher() {
        ElementMatcher.Junction<? super TypeDescription> matcher = ElementMatchers.any();
        for (String ignorePackage : IGNORE_PACKAGES) {
            matcher = matcher.and(ElementMatchers.not(ElementMatchers.nameStartsWith(ignorePackage)));
        }
        ElementMatcher.Junction<? super TypeDescription> orMatcher = ElementMatchers.none();
        for (String aPackage : PACKAGES) {
            orMatcher = orMatcher.or(ElementMatchers.nameStartsWith(aPackage));
        }
        return matcher.and(orMatcher);
    }

    public static void addMethodLine(String className, String methodName, String descriptor, int line) {
        CLASS_METHOD_LINES.add(new ClassMethodLine(className, methodName, descriptor, line));
    }

    public static int getClassMethodLine(Method method) {
        String declaringClassName = method.getDeclaringClass().getName();
        List<ClassMethodLine> methodLines = CLASS_METHOD_LINES.stream()
                .filter(classMethodLine -> declaringClassName.equals(classMethodLine.getClassName()))
                .filter(classMethodLine -> method.getName().equals(classMethodLine.getMethodName()))
                .collect(Collectors.toList());

        if (methodLines == null) {
            return -1;
        }
        if (methodLines.size() == 1) {
            return methodLines.get(0).getLine();
        } else if (methodLines.size() > 1) {
            // TODO 解析 参数类型进行匹配
            String methodDescriptor = getMethodDescriptor(method);
            for (ClassMethodLine methodLine : methodLines) {
                if (methodDescriptor.equals(methodLine.getDescriptor())) {
                    return methodLine.getLine();
                }
            }
        }
        return -1;
    }

    /**
     * 获取方法的入参和返回值的JNI字段描述符串
     *
     * @param method
     * @return
     */
    private static String getMethodDescriptor(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append("(")
                .append(getTypeDescriptors(method.getParameterTypes()))
                .append(")")
                .append(getTypeDescriptor(method.getReturnType()));
        return sb.toString();
    }

    /**
     * 获取多个类型的JNI字段描述符串
     *
     * @param types
     * @return
     */
    private static String getTypeDescriptors(Class<?>[] types) {
        StringBuilder sb = new StringBuilder();
        if (types != null) {
            for (Class<?> type : types) {
                sb.append(getTypeDescriptor(type));
            }
        }
        return sb.toString();
    }

    /**
     * 获取一个类型的 JNI字段描述符
     *
     * @param type
     * @return
     * @see `https://docs.oracle.com/javase/7/docs/technotes/guides/jni/spec/types.html#wp16432`
     */
    private static String getTypeDescriptor(Class<?> type) {
        if (type.isArray()) {
            return "[".concat(type.getName());
        }
        if (type.getName().equals("void")) {
            return "V";
        }
        if (type.isPrimitive()) {
            switch (type.getName()) {
                case "boolean":
                    return "Z";
                case "byte":
                    return "B";
                case "char":
                    return "C";
                case "short":
                    return "S";
                case "int":
                    return "I";
                case "long":
                    return "J";
                case "float":
                    return "F";
                case "double":
                    return "D";
            }
            System.out.println("未支持的类型: " + type.getName());
        } else {
            return "L".concat(type.getName().replace(".", "/")).concat(";");
        }
        return "";
    }

    public static void clearMethodLinesByClassName(String className) {
        Iterator<ClassMethodLine> iterator = CLASS_METHOD_LINES.iterator();
        while (iterator.hasNext()) {
            ClassMethodLine next = iterator.next();
            if (next.getClassName().equals(className)) {
                iterator.remove();
            }
        }
    }


    public static final String[] TRACE_CHARS = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    public static String getTraceId() {
        String traceId = TRACE_ID.get();
        if (traceId == null || "".equals(traceId)) {
            StringBuffer shortBuffer = new StringBuffer();
            String uuid = UUID.randomUUID().toString().replace("-", "");
            for (int i = 0; i < 8; i++) {
                String str = uuid.substring(i * 4, i * 4 + 4);
                int x = Integer.parseInt(str, 16);
                shortBuffer.append(TRACE_CHARS[x % 0x3E]);
            }
            traceId = shortBuffer.toString();
            TRACE_ID.set(traceId);
        }
        return traceId;
    }

}
