package com.github.izerui.structure;

import com.github.izerui.Context;
import com.github.izerui.PremainAgent;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author liuyuhua
 */
public class StructureTransformer implements ClassFileTransformer, PremainAgent {

    private Context context;

    public StructureTransformer(Context context) {
        this.context = context;
    }

    @Override
    public void premain(String args, Instrumentation instrumentation) {
        instrumentation.addTransformer(this);
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
            if (context.matchPackages(realClassName)) {
                // 只记录特定包名下的类，可根据需要修改过滤条件
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                ClassVisitor cv = new StructureClassVisitor(Opcodes.ASM9, cw, realClassName, context);
                cr.accept(cv, ClassReader.EXPAND_FRAMES);
                return cw.toByteArray();
            }
        } catch (Throwable ignored) {
            ignored.printStackTrace();
        }
        return classfileBuffer;
    }

}