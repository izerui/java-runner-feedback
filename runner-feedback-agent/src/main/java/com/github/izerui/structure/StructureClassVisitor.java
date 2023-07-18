package com.github.izerui.structure;

import com.github.izerui.Context;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class StructureClassVisitor extends ClassVisitor {

    private final String className;
    private final ClassWriter classWriter;

    public StructureClassVisitor(int api, ClassWriter classWriter, String className) {
        super(api, classWriter);
        this.className = className;
        this.classWriter = classWriter;
        Context.clearMethodLinesByClassName(className);
    }

    @Override
    public MethodVisitor visitMethod(int access, String methodName, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, methodName, descriptor, signature, exceptions);
        return new StructureMethodVisitor(mv, classWriter, className, methodName, descriptor);
    }

}
