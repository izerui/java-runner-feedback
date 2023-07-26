package com.github.izerui.logger;

import com.github.izerui.AgentProperties;
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
        for (String ignorePackage : Context.getProperties().getIgnorePackages()) {
            matcher = matcher.and(ElementMatchers.not(ElementMatchers.nameStartsWith(ignorePackage)));
        }
        // 排除接口
        matcher = matcher.and(ElementMatchers.not(ElementMatchers.isInterface()));

        ElementMatcher.Junction<? super TypeDescription> orMatcher = ElementMatchers.none();
        // 或包含包名
        for (String aPackage : Context.getProperties().getPackages()) {
            orMatcher = orMatcher.or(ElementMatchers.nameStartsWith(aPackage));
        }

        // 指定接口的子类
        orMatcher = matchTypeWithSubTypeOf(orMatcher);

        matcher = matcher.and(orMatcher);
        System.out.println("bytebuddy matcher: " + matcher.toString());
        return matcher;
    }


    /**
     * 匹配指定是指定接口或者子类匹配
     *
     * @param matcher
     * @return
     */
    private ElementMatcher.Junction<? super TypeDescription> matchTypeWithSubTypeOf(ElementMatcher.Junction<? super TypeDescription> matcher) {
        AgentProperties properties = Context.getProperties();
        for (AgentProperties.Customizer customizer : properties.getCustomizers()) {
            String cls = customizer.getClassName();
            try {
                Class<?> aClass = properties.getCachedClass(cls);
                matcher = matcher.or(ElementMatchers.isSubTypeOf(aClass));
            } catch (Exception ex) {
                ;
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
        if (!Context.getProperties().isShowGetter()) {
            matcher = matcher.and(ElementMatchers.not(ElementMatchers.isGetter()));
        }
        if (!Context.getProperties().isShowSetter()) {
            matcher = matcher.and(ElementMatchers.not(ElementMatchers.isSetter()));
        }
        return builder
                .method(matcher)
                .intercept(MethodDelegation.to(LoggerInterceptor.class)); // 委托
    }
}
