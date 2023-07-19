package com.github.izerui.logger;

import com.github.izerui.Context;
import com.github.izerui.PremainAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LoggerTransformer implements ClassFileTransformer, PremainAgent, AgentBuilder.Transformer {

    public LoggerTransformer() {
    }

    @Override
    public void premain(String args, Instrumentation instrumentation) {
        Supplier<ElementMatcher<? super TypeDescription>> typeMatcherConsumer = () -> {
            ElementMatcher.Junction<? super TypeDescription> matcher = ElementMatchers.any();
            for (String ignorePackage : Context.IGNORE_PACKAGES) {
                matcher = matcher.and(ElementMatchers.not(ElementMatchers.nameStartsWith(ignorePackage)));
            }
            matcher = matcher.and(ElementMatchers.not(ElementMatchers.isInterface()));
            ElementMatcher.Junction<? super TypeDescription> orMatcher = ElementMatchers.none();
            for (String aPackage : Context.PACKAGES) {
                orMatcher = orMatcher.or(ElementMatchers.nameStartsWith(aPackage));
            }
            matcher = matcher.and(orMatcher);
            return matcher;
        };
        new AgentBuilder
                .Default()
//                .with(AgentBuilder.Listener.StreamWriting.toSystemOut())
                .type(typeMatcherConsumer.get()) // 指定需要拦截的类
                .transform(this)
                .installOn(instrumentation);
    }


    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, ProtectionDomain protectionDomain) {
        return builder
                .method(
                        ElementMatchers.any()
                                .and(ElementMatchers.not(ElementMatchers.isHashCode()))
                                .and(ElementMatchers.not(ElementMatchers.isEquals()))
                                .and(ElementMatchers.not(ElementMatchers.isClone()))
                                .and(ElementMatchers.not(ElementMatchers.isToString()))
                                .and(ElementMatchers.not(ElementMatchers.isGetter()))
                                .and(ElementMatchers.not(ElementMatchers.isSetter()))
                )
                .intercept(MethodDelegation.to(LoggerInterceptor.class)); // 委托
    }
}
