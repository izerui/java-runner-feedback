package com.github.izerui;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LineRecordingMethodVisitor extends MethodVisitor {

    private final static ThreadLocal<Integer> LOCAL_REQUEST_NO = new InheritableThreadLocal<>();
    private static Integer INCREASE_REQUEST_NO = 0;

    private final Logger logger;
    private final String realClassName;
    private final String methodName;

    public LineRecordingMethodVisitor(MethodVisitor methodVisitor, String realClassName, String methodName) {
        super(Opcodes.ASM9, methodVisitor);
        this.realClassName = realClassName;
        this.methodName = methodName;
        this.logger = LoggerFactory.getLogger("【Feedback】");
    }

    private String getBaseClassName() {
        return realClassName.substring(realClassName.lastIndexOf(".") + 1, realClassName.length());
    }

    private String getPackageName() {
        return realClassName.substring(0, realClassName.lastIndexOf("."));
    }

    @Override
    public void visitLineNumber(int line, org.objectweb.asm.Label start) {
        logger.info("{}:{} ({}.java:{}) ({})", methodName, line, getBaseClassName(), line, getPackageName());
        super.visitLineNumber(line, start);
    }


    private Integer getRequestNo() {
        Integer no = LOCAL_REQUEST_NO.get();
        if (no == null) {
            no = LineRecordingMethodVisitor.getAndIncreaseRequestNo();
            LOCAL_REQUEST_NO.set(no);
        }
        return no;
    }

    private static synchronized Integer getAndIncreaseRequestNo() {
        INCREASE_REQUEST_NO++;
        return INCREASE_REQUEST_NO;
    }

}