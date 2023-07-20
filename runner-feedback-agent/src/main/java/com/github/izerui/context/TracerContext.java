package com.github.izerui.context;

import com.github.izerui.support.Tracer;

public class TracerContext {

    private final static ThreadLocal<Tracer> TRACER_THREAD_LOCAL = new InheritableThreadLocal<>();


    /**
     * 添加一个tracer, 第一个添加的tracer返回ture
     *
     * @param tracer
     */
    public static boolean addTracerAndReturnTrue(Tracer tracer) {
        if (TRACER_THREAD_LOCAL.get() == null) {
            TRACER_THREAD_LOCAL.set(tracer);
            return true;
        }
        return false;
    }

    /**
     * 返回当前跟踪
     *
     * @return
     */
    public static Tracer getTracer() {
        return TRACER_THREAD_LOCAL.get();
    }

}