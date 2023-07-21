package com.github.izerui;

import com.github.izerui.ansi.AnsiColor;
import com.github.izerui.ansi.AnsiOutput;
import com.github.izerui.context.Context;
import com.github.izerui.logger.LoggerTransformer;
import com.github.izerui.structure.StructureTransformer;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;

public class Agent {

    private final static PremainAgent[] PREMAIN_AGENTS = {
            new StructureTransformer(),
            new LoggerTransformer()
    };

    /**
     * 用于JVM刚启动时调用，其执行时应用类文件还未加载到JVM 加载时刻增强（JVM 启动时加载）
     *
     * @param args
     * @param instrumentation
     */
    public static void premain(String args, Instrumentation instrumentation) {
        Arrays.stream(PREMAIN_AGENTS).forEach(premainAgent -> premainAgent.premain(args, instrumentation));
        System.out.println("☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟☟");
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_MAGENTA, "插桩: runner-feedback-agent 成功!"));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_MAGENTA, "feedback.packages: " + Arrays.toString(Context.PACKAGES)));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_MAGENTA, "feedback.debugger: " + Context.DEBUGGER));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_MAGENTA, "feedback.getter: " + Context.GETTER));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_MAGENTA, "feedback.setter: " + Context.SETTER));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_MAGENTA, "使用@Tracer('标记方法')就可以拦截调用链并输出树状结构!"));
        System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_MAGENTA, "开始愉快的玩耍吧!!!"));
        System.out.println("☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝☝");
    }

}