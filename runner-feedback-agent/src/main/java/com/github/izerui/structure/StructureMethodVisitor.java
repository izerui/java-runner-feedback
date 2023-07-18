package com.github.izerui.structure;

import com.github.izerui.Context;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 修改类字节码,读取类结构缓存等
 * @author liuyuhua
 */
public class StructureMethodVisitor extends MethodVisitor {

    private final Logger logger;
    private final String className;
    private final String methodName;
    private final String descriptor;
    private final ClassWriter classWriter;

    public StructureMethodVisitor(MethodVisitor methodVisitor, ClassWriter classWriter, String className, String methodName, String descriptor) {
        super(Opcodes.ASM9, methodVisitor);
        this.classWriter = classWriter;
        this.className = className;
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.logger = LoggerFactory.getLogger("【Asm】");
    }

    private String getBaseClassName() {
        return className.substring(className.lastIndexOf(".") + 1, className.length());
    }

    private String getPackageName() {
        return className.substring(0, className.lastIndexOf("."));
    }

    @Override
    public void visitLineNumber(int line, org.objectweb.asm.Label start) {
        logger.info("{}({}.java:{})#{}:{}", getPackageName(), getBaseClassName(), line, methodName, descriptor);
        Context.addMethodLine(className, methodName, descriptor, line);
        super.visitLineNumber(line, start);
    }

}