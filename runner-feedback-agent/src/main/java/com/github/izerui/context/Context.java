package com.github.izerui.context;

import com.github.izerui.AgentProperties;
import com.github.izerui.ansi.AnsiColor;
import com.github.izerui.ansi.AnsiOutput;
import lombok.SneakyThrows;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.objectweb.asm.Opcodes;
import org.yaml.snakeyaml.Yaml;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Context {

    /**
     * agnet 配置文件
     */
    private static final AgentProperties properties;

    public final static int ASM_VERSION = Opcodes.ASM9;


    /**
     * 类名与类的缓存
     */
    private final static Map<String, Class> classCacheMap = new HashMap<>();


    public final static SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        Yaml yaml = new Yaml();
        properties = yaml.loadAs(ClassLoader.getSystemResourceAsStream("feedback.yaml"), AgentProperties.class);
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
    }

    /**
     * 获取配置
     * @return
     */
    public static AgentProperties getProperties() {
        return properties;
    }

    public static void printAfterAgent() {
        System.out.println("☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟");
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "插桩: runner-feedback-agent 成功!"));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "配置文件: feedback.yaml"));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "使用@Tracer('标记方法')就可以拦截调用链并输出树状结构!"));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "开始愉快的玩耍吧!!!"));
        System.out.println("☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝");
    }


    /**
     * 通过类名匹配是否属于设置的包下
     *
     * @param className
     * @return
     */
    public static boolean matchPackages(String className) {
        for (String ignorePackage : properties.getIgnore_packages()) {
            if (className.startsWith(ignorePackage)) {
                return false;
            }
        }
        for (String aPackage : properties.getPackages()) {
            if (className.startsWith(aPackage)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 通过类名匹配是否属于设置的包下
     *
     * @param currentStackFrame
     * @return
     */
    public static boolean matchInterfaceMethods(StackWalker.StackFrame currentStackFrame) {
        for (String classMethod : properties.getClass_methods()) {
            String[] split = classMethod.split("#");
            String cls = split[0];
            String mdp = split[1];
            if (cachedClass(cls).isAssignableFrom(cachedClass(getOriginName(currentStackFrame.getClassName(), "$")))
                    && (mdp.equals("*") || mdp.startsWith(getOriginName(currentStackFrame.getMethodName(), "$") + currentStackFrame.getDescriptor()))) {
                return true;
            }
        }
        return false;
    }


    /**
     * 匹配类型忽略指定的注解
     *
     * @param matcher
     * @return
     */
    public static ElementMatcher.Junction<? super TypeDescription> matchTypeWithOutAnnotation(ElementMatcher.Junction<? super TypeDescription> matcher) {
        for (String annotationClassName : properties.getIgnore_annotations()) {
            try {
                Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) cachedClass(annotationClassName);
                matcher = matcher.and(ElementMatchers.not(ElementMatchers.hasAnnotation(ElementMatchers.annotationType(annotationClass))));
            } catch (Exception ex) {
                ;
            }
        }
        return matcher;
    }


    /**
     * 匹配指定是指定接口或者父类的子类匹配
     *
     * @param matcher
     * @return
     */
    public static ElementMatcher.Junction<? super TypeDescription> matchTypeWithSubTypeOf(ElementMatcher.Junction<? super TypeDescription> matcher) {
        for (String classMethod : properties.getClass_methods()) {
            String[] split = classMethod.split("#");
            String cls = split[0];
            try {
                Class<?> aClass = cachedClass(cls);
                matcher = matcher.or(ElementMatchers.isSubTypeOf(aClass));
            } catch (Exception ex) {
                ;
            }
        }
        return matcher;
    }

    /**
     * 从缓存中获取class
     *
     * @param className
     * @return
     */
    @SneakyThrows
    public static Class cachedClass(String className) {
        Class aClass = classCacheMap.get(className);
        if (aClass == null) {
            aClass = Class.forName(className);
            classCacheMap.put(className, aClass);
        }
        return aClass;
    }


    /**
     * 获取原始名
     *
     * @param proxyName       代理名称
     * @param proxyIdentifier 代理标识符, class类型: $$  method类型: $
     * @return
     */
    public static String getOriginName(String proxyName, String proxyIdentifier) {
        String originMethodName = proxyName;
        int proxySplitIndex = proxyName.indexOf(proxyIdentifier);
        if (proxySplitIndex > -1) {
            originMethodName = proxyName.substring(0, proxySplitIndex);
        }
        if (originMethodName == null || "".equals(originMethodName)) {
            originMethodName = proxyName;
        }
        return originMethodName;
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
    public static String generateTraceId() {
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
