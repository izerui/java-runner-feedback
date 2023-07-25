package com.github.izerui.renderer;

import com.github.izerui.AgentProperties;
import com.github.izerui.ansi.AnsiColor;
import com.github.izerui.ansi.AnsiOutput;
import com.github.izerui.support.Span;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

public interface LineRenderer {

    LineRenderer DEFAULT = (span, properties) -> {
        Map<String, Object> variables = new HashMap<>();
        // 是否成功
        variables.put("success", span.isSuccess() ? AnsiOutput.toString(AnsiColor.GREEN, "[T]") : AnsiOutput.toString(AnsiColor.RED, "[F]"));
        // 耗时
        variables.put("time", span.getTime() + "ms");
        // 调用次数
        variables.put("count", AnsiOutput.toString(AnsiColor.RED, span.getCount() > 1 ? "[" + span.getCount() + "]" : ""));
        // 包名
        variables.put("package", AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, span.getDeclaringPackage()));
        // 文件名
        variables.put("file", span.getFileName());
        // 行号
        variables.put("line", span.getLine());
        // 方法
        variables.put("method", AnsiOutput.toString(AnsiColor.YELLOW, span.getMethodName()));
        // 方法描述符
        variables.put("descriptor", AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, span.getDescriptor()));
        // 线程名
        variables.put("thread", AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, "[" + span.getThreadName() + "]"));
        // 参数
        variables.put("args", AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, toString(span.getArgumengts())));
        StringSubstitutor substitutor = new StringSubstitutor(variables);
        return substitutor.replace(properties.getOutputFormat());
    };

    String render(Span span, AgentProperties properties);

    static String toString(Object[] a) {
        if (a == null) {
            return "null";
        }

        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            String text = String.valueOf(a[i]);
            if (text != null) {
                text = text.replaceAll("[\\t\\n\\r]", "");
            }
            b.append(text);
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }
}
