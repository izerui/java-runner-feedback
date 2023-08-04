package com.github.izerui;

import com.github.izerui.ansi.AnsiOutput;
import com.github.izerui.context.Context;
import com.github.izerui.logger.LoggerTransformer;
import com.github.izerui.structure.StructureTransformer;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
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
        Yaml yaml = new Yaml();
        InputStream resourceAsStream = yaml.getClass().getResourceAsStream("feedback.yaml");
        if (resourceAsStream != null) {
            Context.setProperties(yaml.loadAs(resourceAsStream, AgentProperties.class));
            if (Context.getProperties().isEnabled()) {
                AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
                Arrays.stream(PREMAIN_AGENTS).forEach(premainAgent -> premainAgent.premain(args, instrumentation));
                Context.printAfterAgent();
            }
        }
    }

}