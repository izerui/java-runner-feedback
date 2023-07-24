package com.github.izerui.context;

import com.github.izerui.ansi.AnsiColor;
import com.github.izerui.ansi.AnsiOutput;
import lombok.SneakyThrows;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.objectweb.asm.Opcodes;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Context {

    /**
     * 每行输出格式
     */
    public static String OUTPUT_FORMAT = "${success} ${time}${count} ${thread} ${package}(${file}:${line})${method}${descriptor} ${args}";

    public final static int ASM_VERSION = Opcodes.ASM9;

    /**
     * 扫描记录包含的包名下的类
     */
    public final static String[] PACKAGES;

    /**
     * 类名与类的缓存
     */
    private final static Map<String, Class> classCacheMap = new HashMap<>();

    /**
     * 扫描记录继承至如下接口的方法
     */
    public final static Map<String, String> INTERFACE_METHODS_MAP = new HashMap<>() {{
        put("feign.Client", "execute(Lfeign/Request;Lfeign/Request$Options;)Lfeign/Response;");
        put("java.sql.PreparedStatement", "*");
        put("java.sql.Statement", "startQueryTimer(Lcom/mysql/cj/Query;I)Lcom/mysql/cj/CancelQueryTask;");
    }};

    /**
     * 是否调试状态，输出拦截的方法信息
     */
    public static boolean DEBUGGER = false;

    /**
     * 是否拦截并显示get方法
     */
    public static boolean SHOW_GETTER = false;

    /**
     * 是否拦截并显示set方法
     */
    public static boolean SHOW_SETTER = false;

    /**
     * 忽略指定包名下的类
     */
    public final static String[] IGNORE_PACKAGES = {
            "com.github.izerui"
    };

    /**
     * 忽略带指定注解的
     */
    public final static String[] IGNORE_ANNOTATIONS = {
            "org.springframework.cloud.openfeign.FeignClient"
    };


    public final static SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        String feedbackPackages = getStringProperty("feedback.packages", null);
        if (feedbackPackages != null && !"".equals(feedbackPackages)) {
            PACKAGES = feedbackPackages.split(",");
        } else {
            System.out.println("未获取到正确的可【feedback】的包匹配字符串, 请正确配置agent类似: -javaagent:~/runner-feedback-agent.jar -Dfeedback.packages=com.github.sample,com.yj2025");
            PACKAGES = new String[0];
        }
        OUTPUT_FORMAT = getStringProperty("feedback.output-format", OUTPUT_FORMAT);
        DEBUGGER = getBoolProperty("feedback.debugger", DEBUGGER);
        SHOW_GETTER = getBoolProperty("feedback.show-getter", SHOW_GETTER);
        SHOW_SETTER = getBoolProperty("feedback.show-setter", SHOW_SETTER);
    }

    public static void printAfterAgent() {
        System.out.println("☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟");
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "插桩: runner-feedback-agent 成功!"));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "[feedback.packages](拦截包名,多个逗号分隔): " + Arrays.toString(Context.PACKAGES)));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "[feedback.debugger](是否输出调试信息): " + Context.DEBUGGER));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "[feedback.show-getter](是否拦截并显示get方法): " + Context.SHOW_GETTER));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "[feedback.show-setter](是否拦截并显示set方法): " + Context.SHOW_SETTER));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "[feedback.output-format](每行输出格式): " + Context.OUTPUT_FORMAT));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "使用@Tracer('标记方法')就可以拦截调用链并输出树状结构!"));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "开始愉快的玩耍吧!!!"));
        System.out.println("☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝");
    }

    private static String getStringProperty(String key, String defaultValue) {
        Properties properties = System.getProperties();
        Object value = properties.get(key);
        if (value != null) {
            return String.valueOf(value);
        }
        return defaultValue;
    }

    private static boolean getBoolProperty(String key, boolean defaultBool) {
        Properties properties = System.getProperties();
        Object bool = properties.get(key);
        if (bool != null) {
            return Boolean.valueOf(String.valueOf(bool));
        }
        return defaultBool;
    }

    /**
     * 通过类名匹配是否属于设置的包下
     *
     * @param className
     * @return
     */
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


    /**
     * 通过类名匹配是否属于设置的包下
     *
     * @param currentStackFrame
     * @return
     */
    public static boolean matchInterfaceMethods(StackWalker.StackFrame currentStackFrame) {
        AtomicBoolean matched = new AtomicBoolean(false);
        INTERFACE_METHODS_MAP.forEach((cls, mdp) -> {
            try {
                if (getCachedClass(cls).isAssignableFrom(getCachedClass(getOriginName(currentStackFrame.getClassName(), "$")))
                        && (mdp.equals("*") || mdp.equals(getOriginName(currentStackFrame.getMethodName(), "$") + currentStackFrame.getDescriptor()))) {
                    matched.set(true);
                }
            } catch (Exception ex) {
                ;
            }
        });
        return matched.get();
    }


    /**
     * 匹配类型忽略指定的注解
     *
     * @param matcher
     * @return
     */
    public static ElementMatcher.Junction<? super TypeDescription> matchTypeWithOutAnnotation(ElementMatcher.Junction<? super TypeDescription> matcher) {
        for (String annotationClassName : Context.IGNORE_ANNOTATIONS) {
            try {
                Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) getCachedClass(annotationClassName);
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
        for (String className : INTERFACE_METHODS_MAP.keySet()) {
            try {
                Class<?> aClass = getCachedClass(className);
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
    public static Class getCachedClass(String className) {
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
