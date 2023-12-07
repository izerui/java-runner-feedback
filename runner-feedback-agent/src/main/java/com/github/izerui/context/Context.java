package com.github.izerui.context;

import com.github.izerui.AgentProperties;
import com.github.izerui.ansi.AnsiColor;
import com.github.izerui.ansi.AnsiOutput;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

public final class Context {

    /**
     * agnet 配置文件
     */
    private static final AgentProperties properties = new AgentProperties();

    public final static int ASM_VERSION = Opcodes.ASM9;

    static {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        properties.setEnabled(getBoolProperty("feedback.enabled", true));
        properties.setDebugger(getBoolProperty("feedback.debugger", false));
        properties.setPackages(getArrayProperty("feedback.packages", "com.yj2025", "com.ecworking"));
        properties.setIgnorePackages(getArrayProperty("feedback.ignore-packages", "com.github.izerui"));
        properties.setIgnoreMethods(getArrayProperty("feedback.ignore-methods", "afterExecuted"));
        properties.setOutputFormat(getStringProperty("feedback.output-format", "${success} ${time}${count} ${package}(${file}:${line})${method}${descriptor} ${thread}"));
        properties.setShowGetter(getBoolProperty("feedback.show-getter", false));
        properties.setShowSetter(getBoolProperty("feedback.show-setter", false));
    }

    private static String[] getArrayProperty(String key, String... defaultValue) {
        Properties properties = System.getProperties();
        Object value = properties.get(key);
        if (value != null) {
            return String.valueOf(value).split(",");
        }
        return defaultValue;
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
     * 获取配置
     *
     * @return
     */
    public static AgentProperties getProperties() {
        return properties;
    }

    public static void printAfterAgent() {
        System.out.println("☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟");
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "插桩: runner-feedback-agent 成功!"));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "[feedback.enabled](是否启用): " + Boolean.valueOf(properties.isEnabled())));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "[feedback.debugger](是否输出调试信息): " + properties.isDebugger()));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "[feedback.packages](拦截的起始包名,多个逗号分隔): " + Arrays.toString(properties.getPackages())));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "[feedback.ignore-packages](忽略的起始包名,多个逗号分隔): " + Arrays.toString(properties.getPackages())));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "[feedback.ignore-methods](忽略的方法名,多个逗号分隔): " + Arrays.toString(properties.getPackages())));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "[feedback.output-format](输出格式): " + properties.getOutputFormat()));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "[feedback.show-getter](是否拦截并显示get方法): " + properties.isShowGetter()));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "[feedback.show-setter](是否拦截并显示set方法): " + properties.isShowSetter()));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "使用@Tracer('标记方法')就可以拦截调用链并输出树状结构!"));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "开始愉快的玩耍吧!!!"));
        System.out.println("☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝");
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
