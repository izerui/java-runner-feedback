package com.github.izerui.logger;

import com.github.izerui.PremainAgent;
import com.github.izerui.context.Context;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.annotation.Annotation;
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
                .type(getTypeMatcher()) // 指定需要拦截的类
                .transform(this)
                .installOn(instrumentation);
    }


    private ElementMatcher<? super TypeDescription> getTypeMatcher() {
        ElementMatcher.Junction<? super TypeDescription> matcher = ElementMatchers.any();
        // 排除忽略的包
        for (String ignorePackage : Context.IGNORE_PACKAGES) {
            matcher = matcher.and(ElementMatchers.not(ElementMatchers.nameStartsWith(ignorePackage)));
        }
        // 排除接口
        matcher = matcher.and(ElementMatchers.not(ElementMatchers.isInterface()));
        // 排除包含指定注解的类
        matcher = withOutAnnotation(matcher, Context.IGNORE_ANNOTATIONS);

        ElementMatcher.Junction<? super TypeDescription> orMatcher = ElementMatchers.none();
        // 或包含包名
        for (String aPackage : Context.PACKAGES) {
            orMatcher = orMatcher.or(ElementMatchers.nameStartsWith(aPackage));
        }

        // 指定接口的子类
        orMatcher = withSubTypeOf(orMatcher, Context.INTERFACIES);

        matcher = matcher.and(orMatcher);
        return matcher;
    }

    private ElementMatcher.Junction<? super TypeDescription> withOutAnnotation(ElementMatcher.Junction<? super TypeDescription> matcher, String... annotationClassNames) {
        if (annotationClassNames != null) {
            for (String annotationClassName : annotationClassNames) {
                try {
                    Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) Class.forName(annotationClassName);
                    matcher = matcher.and(ElementMatchers.not(ElementMatchers.hasAnnotation(ElementMatchers.annotationType(annotationClass))));
                } catch (Exception ex) {
                    ;
                }
            }
        }
        return matcher;
    }

    private ElementMatcher.Junction<? super TypeDescription> withSubTypeOf(ElementMatcher.Junction<? super TypeDescription> matcher, String... interfacies) {
        if (interfacies != null) {
            for (String itf : interfacies) {
                try {
                    Class<?> aClass = Class.forName(itf);
                    matcher = matcher.or(ElementMatchers.isSubTypeOf(aClass));
                } catch (Exception ex) {
                    ;
                }
            }
        }
        return matcher;
    }


    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, ProtectionDomain protectionDomain) {
        ElementMatcher.Junction<MethodDescription> matcher = ElementMatchers.any()
                .and(ElementMatchers.not(ElementMatchers.isHashCode()))
                .and(ElementMatchers.not(ElementMatchers.isEquals()))
                .and(ElementMatchers.not(ElementMatchers.isClone()))
                .and(ElementMatchers.not(ElementMatchers.isToString()));
        if (!Context.SHOW_GETTER) {
            matcher = matcher.and(ElementMatchers.not(ElementMatchers.isGetter()));
        }
        if (!Context.SHOW_SETTER) {
            matcher = matcher.and(ElementMatchers.not(ElementMatchers.isSetter()));
        }
        return builder
                .method(matcher)
                .intercept(MethodDelegation.to(LoggerInterceptor.class)); // 委托
    }
}
