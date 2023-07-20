package com.github.izerui.context;

import com.github.izerui.annotation.Tracer;

import java.util.UUID;

@Deprecated
public class TraceContext {

    private final static ThreadLocal<String> TRACE_ID = new InheritableThreadLocal<>();
    private final static ThreadLocal<String> TRACE_NAME = new InheritableThreadLocal<>();

    public static final String[] TRACE_CHARS = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    public static String getTraceNameId() {
        return TRACE_NAME.get() + "-" + getTraceId();
    }

    /**
     * 根据传入的注解来生成traceId， 如果传入的是空则不生成。
     *
     * @param feedback
     */
    public static void generateIfNone(Tracer feedback) {
        // 未声明@Feedback的方法不生成 name和id
        if (feedback == null) {
            return;
        }
        // 记录traceName
        String traceName = TRACE_NAME.get();
        if (traceName == null) {
            TRACE_NAME.set(feedback.value());
        }
        // 在记录traceName的前提下，生成traceId
        getTraceId();
    }

    public static String getTraceId() {
        String traceId = TRACE_ID.get();
        if (traceId == null) {
            StringBuffer shortBuffer = new StringBuffer();
            String uuid = UUID.randomUUID().toString().replace("-", "");
            for (int i = 0; i < 8; i++) {
                String str = uuid.substring(i * 4, i * 4 + 4);
                int x = Integer.parseInt(str, 16);
                shortBuffer.append(TRACE_CHARS[x % 0x3E]);
            }
            traceId = shortBuffer.toString();
            TRACE_ID.set(traceId);
        }
        return traceId;
    }

    public static void clear() {
        TRACE_ID.remove();
        TRACE_NAME.remove();
    }
}