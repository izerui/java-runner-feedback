package com.github.izerui.support;

import com.github.izerui.context.Context;

import java.util.ArrayList;
import java.util.List;

public abstract class Stack {

    public abstract List<StackWalker.StackFrame> getStackFrames();

    public abstract int getMethodLine();

    /**
     * 文件名
     */
    public String getFileName() {
        return getStackFrames().get(0).getFileName();
    }

    /**
     * 获取当前方法执行的行号
     *
     * @return
     */
    public int getLine() {
        int line = getMethodLine();
        if (line == -1) {
            return getStackFrames().get(0).getLineNumber();
        }
        return line;
    }

    public String getClassName() {
        return getStackFrames().get(0).getClassName();
    }

    public String getMethodName() {
        return getStackFrames().get(0).getMethodName();
    }

    /**
     * 当前调用的唯一ID
     *
     * @return
     */
    public String getId() {
        return getKey(getStackFrames().get(0));
    }

    /**
     * 拼接唯一key
     *
     * @param stackFrame
     * @return
     */
    protected String getKey(StackWalker.StackFrame stackFrame) {
        if (stackFrame == null) {
            return "NONE";
        }
        return Context.getOriginName(stackFrame.getClassName(), "$") + "#" + Context.getOriginName(stackFrame.getMethodName(), "$") + stackFrame.getDescriptor();
    }

    /**
     * 获取父级调用帧
     *
     * @return
     */
    public List<String> getParentIds() {
        List<String> parentIds = new ArrayList<>();
        List<StackWalker.StackFrame> stackFrames = getStackFrames();
        for (int i = 1; i < stackFrames.size(); i++) {
            StackWalker.StackFrame stackItem = stackFrames.get(i);
            String key = getKey(stackItem);
            if (!parentIds.contains(key)) {
                parentIds.add(key);
            }
        }
        return parentIds;
    }

    /**
     * 获取直接父级, 顶级无父级返回null
     *
     * @return
     */
    public String getParentId() {
        List<String> parentIds = getParentIds();
        if (parentIds != null && !parentIds.isEmpty()) {
            return getParentIds().get(0);
        }
        return null;
    }

    /**
     * 当前方法的JNI描述符 参看: https://docs.oracle.com/javase/7/docs/technotes/guides/jni/spec/types.html#wp16432
     */
    public String getDescriptor() {
        return getStackFrames().get(0).getDescriptor();
    }
}
