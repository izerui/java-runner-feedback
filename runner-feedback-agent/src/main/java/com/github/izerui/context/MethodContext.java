package com.github.izerui.context;

import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 类和方法及行号的映射缓存
 */
public class MethodContext {
    /**
     * k:className
     * v: methodName__descriptor: line
     * k: methodName__descriptor
     * v: line
     */
    private final static Map<String, Map<String, Integer>> CLASS_METHOD_LINES_MAP = new HashMap<>();

    /**
     * 根据类名清除缓存
     *
     * @param className
     */
    public synchronized static void removeByClassName(String className) {
        CLASS_METHOD_LINES_MAP.remove(className);
    }

    /**
     * 添加类、方法、行号到缓存中
     *
     * @param className
     * @param methodName
     * @param descriptor
     * @param line
     */
    public synchronized static void addLine(String className, String methodName, String descriptor, int line) {
        if (!CLASS_METHOD_LINES_MAP.containsKey(className)) {
            CLASS_METHOD_LINES_MAP.put(className, new HashMap<>());
        }
        Map<String, Integer> lineMap = CLASS_METHOD_LINES_MAP.get(className);
        String _key = methodName.concat(descriptor);
        if (!lineMap.containsKey(_key)) {
            lineMap.put(_key, line);
        }
    }

    /**
     * 获取方法所属类的行号
     */
    public static int getLine(String className, String methodName, String descriptor) {
        Map<String, Integer> lineMap = CLASS_METHOD_LINES_MAP.get(className);
        if (lineMap == null) {
            return -1;
        }
        String _key = methodName.concat(descriptor);
        Integer line = lineMap.get(_key);
        if (line == null) {
            return -1;
        }
        return line;
    }

    /**
     * 获取方法所属类的行号
     *
     * @param method
     * @return
     */
    public static int getLine(Method method) {
        return getLine(method.getDeclaringClass().getName(), method.getName(), getMethodDescriptor(method));
    }

    /**
     * 获取方法的入参和返回值的JNI字段描述符串
     *
     * @param method
     * @return
     */
    public static String getMethodDescriptor(Method method) {
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


    public static void main(String[] args) throws NoSuchMethodException {
        Method method = Statement.class.getDeclaredMethod("executeQuery", String.class);
        System.out.println(method.getName() + getMethodDescriptor(method));
    }

}