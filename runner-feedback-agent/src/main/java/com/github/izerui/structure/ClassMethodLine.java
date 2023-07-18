package com.github.izerui.structure;


import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ClassMethodLine)) {
            return false;
        }
        ClassMethodLine ml = (ClassMethodLine) obj;
        return Objects.equals(className, ml.className) &&
                Objects.equals(methodName, ml.methodName) &&
                Objects.equals(descriptor, ml.descriptor);
    }

    @Override
    public int hashCode() {
        if (className != null && methodName != null && descriptor != null) {
            return className.hashCode() + methodName.hashCode() + descriptor.hashCode();
        }
        return super.hashCode();
    }
}
