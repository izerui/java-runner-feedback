package com.github.izerui;

import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author liuyuhua
 */
public class ClassLoggerTransformer implements ClassFileTransformer {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClassLoggerTransformer.class);

    private final static String[] FEEDBACK_PACKAGES;


    static {
        String feedbackPackages = System.getProperties().getProperty("runner-feedback.packages");
        if (feedbackPackages != null && !"".equals(feedbackPackages)) {
            FEEDBACK_PACKAGES = feedbackPackages.split(",");
        } else {
            LOGGER.warn("未获取到正确的可【feedback】的包匹配字符串, 请正确配置agent类似: -javaagent:runner-feedback-agent-jar-with-dependencies.jar -Drunner-feedback.packages=com.github.sample,com.yj2025");
            FEEDBACK_PACKAGES = new String[0];
        }
    }

    /**
     * 返回值是替换的字节码数据
     */
    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            String realClassName = className.replaceAll("/", ".");
            for (String feedbackPackage : FEEDBACK_PACKAGES) {
                // 只记录特定包名下的类，可根据需要修改过滤条件
                if (realClassName.startsWith(feedbackPackage)) {
                    ClassReader cr = new ClassReader(classfileBuffer);
                    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                    ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
                        @Override
                        public MethodVisitor visitMethod(
                                int access,
                                String methodName,
                                String descriptor,
                                String signature,
                                String[] exceptions
                        ) {
                            MethodVisitor mv = super.visitMethod(access, methodName, descriptor, signature, exceptions);
                            return new LineRecordingMethodVisitor(mv, realClassName, methodName);
                        }
                    };
                    cr.accept(cv, ClassReader.EXPAND_FRAMES);
                    return cw.toByteArray();
                }
            }
        } catch (Throwable ignored) {
            ignored.printStackTrace();
        }
        return classfileBuffer;
    }
}