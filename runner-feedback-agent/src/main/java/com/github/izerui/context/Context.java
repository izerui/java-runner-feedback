package com.github.izerui.context;

import com.github.izerui.ansi.AnsiOutput;
import org.objectweb.asm.Opcodes;

import java.util.Properties;

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


}
