package com.github.izerui.structure;


public class ClassMethodLine {
    private String className;
    private String methodName;
    private String descriptor;
    private int line;

    public ClassMethodLine(String className, String methodName, String descriptor, int line) {
        this.className = className;
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.line = line;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
