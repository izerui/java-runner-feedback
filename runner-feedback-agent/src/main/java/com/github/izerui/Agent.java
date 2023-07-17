package com.github.izerui;

import com.github.izerui.logger.LoggerTransformer;
import com.github.izerui.structure.StructureTransformer;

import java.lang.instrument.Instrumentation;

public class Agent {
    /**
     * 用于JVM刚启动时调用，其执行时应用类文件还未加载到JVM 加载时刻增强（JVM 启动时加载）
     *
     * @param args
     * @param instrumentation
     */
    public static void premain(String args, Instrumentation instrumentation) {
        Context context = new Context();

        StructureTransformer structureTransformer = new StructureTransformer(context);
        structureTransformer.premain(args, instrumentation);

        LoggerTransformer feedbackTransformer = new LoggerTransformer(context);
        feedbackTransformer.premain(args, instrumentation);

    }

}