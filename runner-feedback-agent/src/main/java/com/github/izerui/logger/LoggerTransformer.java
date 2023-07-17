package com.github.izerui.logger;

import com.github.izerui.Context;
import com.github.izerui.PremainAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class LoggerTransformer implements ClassFileTransformer, PremainAgent, AgentBuilder.Transformer {
    private final Context context;

    public LoggerTransformer(Context context) {
        this.context = context;
    }

    @Override
    public void premain(String args, Instrumentation instrumentation) {
        new AgentBuilder
                .Default()
                .type(context.getTypeMatcher()) // 指定需要拦截的类
                .transform(this)
                .installOn(instrumentation);
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, ProtectionDomain protectionDomain) {
        return builder
                .method(ElementMatchers.<MethodDescription>any()) // 拦截任意方法
                .intercept(MethodDelegation.to(LoggerInterceptor.class)); // 委托
    }
}
