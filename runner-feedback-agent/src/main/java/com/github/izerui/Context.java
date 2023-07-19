package com.github.izerui;

import com.github.izerui.annotation.Feedback;
import com.github.izerui.ansi.AnsiOutput;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public final class Context {

    public final static int ASM_VERSION = Opcodes.ASM9;

    /**
     * 是否输出非扫描路径的类的调用日志
     */
    public static boolean DEEP_SHOW = true;

    /**
     * 扫描记录包含的包名下的类
     */
    public final static String[] PACKAGES;

    /**
     * 忽略指定包名下的类
     */
    public final static String[] IGNORE_PACKAGES = {
            "com.github.izerui"
    };

    public final static String[] IGNORE_ANNOTATIONS = {
            "org.springframework.cloud.openfeign.FeignClient"
    };

    /**
     * k:className
     * v: methodName__descriptor: line
     * k: methodName__descriptor
     * v: line
     */
    private final static Map<String, Map<String, Integer>> CLASS_METHOD_LINES_MAP = new HashMap<>();

    static {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        Properties properties = System.getProperties();
        String feedbackPackages = properties.getProperty("feedback.packages");
        if (feedbackPackages != null && !"".equals(feedbackPackages)) {
            PACKAGES = feedbackPackages.split(",");
        } else {
            System.out.println("未获取到正确的可【feedback】的包匹配字符串, 请正确配置agent类似: -javaagent:~/runner-feedback-agent.jar -Dfeedback.packages=com.github.sample,com.yj2025");
            PACKAGES = new String[0];
        }
        Object deepshow = properties.get("feedback.deepshow");
        if (deepshow != null) {
            DEEP_SHOW = Boolean.valueOf(String.valueOf(properties.get("feedback.deepshow")));
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

    public synchronized static void clearMethodLinesByClassName(String className) {
        CLASS_METHOD_LINES_MAP.remove(className);
    }

    public synchronized static void addMethodLine(String className, String methodName, String descriptor, int line) {
        if (!CLASS_METHOD_LINES_MAP.containsKey(className)) {
            CLASS_METHOD_LINES_MAP.put(className, new HashMap<>());
        }
        Map<String, Integer> lineMap = CLASS_METHOD_LINES_MAP.get(className);
        String _key = methodName.concat(descriptor);
        if (!lineMap.containsKey(_key)) {
            lineMap.put(_key, line);
        }
    }

    public static int getClassMethodLine(Method method) {
        String declaringClassName = method.getDeclaringClass().getName();
        if (!CLASS_METHOD_LINES_MAP.containsKey(declaringClassName)) {
            CLASS_METHOD_LINES_MAP.put(declaringClassName, new HashMap<>());
        }
        Map<String, Integer> lineMap = CLASS_METHOD_LINES_MAP.get(declaringClassName);
        String methodDescriptor = getMethodDescriptor(method);
        String _key = method.getName().concat(methodDescriptor);
        Integer line = lineMap.get(_key);
        if (line == null) {
            return -1;
        }
        return line;
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


    public final static class Trace {

        private final static ThreadLocal<String> TRACE_ID = new InheritableThreadLocal<>();
        private final static ThreadLocal<String> TRACE_NAME = new InheritableThreadLocal<>();

        public static final String[] TRACE_CHARS = new String[]{"a", "b", "c", "d", "e", "f",
                "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};

        public static String getTraceNameId() {
            return TRACE_NAME.get() + "-" + getTraceId();
        }

        /**
         * 根据传入的注解来生成traceId， 如果传入的是空则不生成。
         *
         * @param feedback
         */
        public static void generateIfNone(Feedback feedback) {
            // 未声明@Feedback的方法不生成 name和id
            if (feedback == null) {
                return;
            }
            // 记录traceName
            String traceName = TRACE_NAME.get();
            if (traceName == null) {
                TRACE_NAME.set(feedback.value());
            }
            // 在记录traceName的前提下，生成traceId
            getTraceId();
        }

        public static String getTraceId() {
            String traceId = TRACE_ID.get();
            if (traceId == null) {
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

        public static void clear() {
            TRACE_ID.remove();
            TRACE_NAME.remove();
        }
    }

}
