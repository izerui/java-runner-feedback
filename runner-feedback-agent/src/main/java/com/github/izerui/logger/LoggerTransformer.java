package com.github.izerui.logger;

import com.github.izerui.Context;
import com.github.izerui.PremainAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class LoggerTransformer implements ClassFileTransformer, PremainAgent, AgentBuilder.Transformer {

    public LoggerTransformer() {
    }

    @Override
    public void premain(String args, Instrumentation instrumentation) {
        new AgentBuilder
                .Default()
//                .with(AgentBuilder.Listener.StreamWriting.toSystemOut())
                .type(Context.getTypeMatcher()) // 指定需要拦截的类
                .transform(this)
                .installOn(instrumentation);
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, ProtectionDomain protectionDomain) {
        return builder
                .method(ElementMatchers.any()) // 拦截任意方法
                .intercept(MethodDelegation.to(LoggerInterceptor.class)); // 委托
    }
}
