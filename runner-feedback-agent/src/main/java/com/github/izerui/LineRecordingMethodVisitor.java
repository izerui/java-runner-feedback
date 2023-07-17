//package com.github.izerui;
//
//import org.objectweb.asm.ClassWriter;
//import org.objectweb.asm.MethodVisitor;
//import org.objectweb.asm.Opcodes;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class LineRecordingMethodVisitor extends MethodVisitor {
//
//    private final Logger logger;
//    private final String realClassName;
//    private final String methodName;
//    private final String descriptor;
//    private final ClassWriter classWriter;
//
//    public LineRecordingMethodVisitor(MethodVisitor methodVisitor, ClassWriter classWriter, String realClassName, String methodName, String descriptor) {
//        super(Opcodes.ASM9, methodVisitor);
//        this.classWriter = classWriter;
//        this.realClassName = realClassName;
//        this.methodName = methodName;
//        this.descriptor = descriptor;
//        this.logger = LoggerFactory.getLogger("【Feedback】");
//    }
//
//    private String getBaseClassName() {
//        return realClassName.substring(realClassName.lastIndexOf(".") + 1, realClassName.length());
//    }
//
//    private String getPackageName() {
//        return realClassName.substring(0, realClassName.lastIndexOf("."));
//    }
//
//    @Override
//    public void visitLineNumber(int line, org.objectweb.asm.Label start) {
//        logger.info("{}({}.java:{})#{}:{}",getPackageName(),  getBaseClassName(), line, methodName, descriptor);
//        super.visitLineNumber(line, start);
//    }
//
//}