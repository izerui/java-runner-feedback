package com.github.izerui.context;

import com.github.izerui.AgentProperties;
import com.github.izerui.ansi.AnsiColor;
import com.github.izerui.ansi.AnsiOutput;
import org.objectweb.asm.Opcodes;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.UUID;

public final class Context {

    /**
     * agnet 配置文件
     */
    private static AgentProperties properties = new AgentProperties();

    public final static int ASM_VERSION = Opcodes.ASM9;


    static {
        Yaml yaml = new Yaml();
        InputStream resourceAsStream = ClassLoader.getSystemResourceAsStream("feedback.yaml");
        if (resourceAsStream != null) {
            properties = yaml.loadAs(resourceAsStream, AgentProperties.class);
            AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        }
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
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "配置文件: feedback.yaml"));
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
