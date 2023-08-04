package com.github.izerui;

import com.github.izerui.renderer.LineRenderer;
import com.github.izerui.support.Span;
import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Data
public class AgentProperties {

    /**
     * 类名与类的缓存
     */
    private final static Map<String, Class> classCacheMap = new HashMap<>();

    /**
     * 是否启用
     */
    private boolean enabled;
    /**
     * 每行输出格式
     */
    private String outputFormat;
    /**
     * 是否拦截并显示set方法
     */
    private boolean showSetter;
    /**
     * 是否拦截并显示get方法
     */
    private boolean showGetter;
    /**
     * 是否调试状态，输出拦截的方法信息
     */
    private boolean debugger;
    /**
     * 扫描记录包含的包名下的类
     */
    private String[] packages;
    /**
     * 忽略指定包名下的类
     */
    private String[] ignorePackages;
    /**
     * 自定义扩展, 除了扫描指定的包下的类外，也同样扫描自定义的类，并按自定义输出到行
     */
    private Customizer[] customizers;


    public String[] getPackages() {
        if (packages == null) {
            packages = new String[0];
        }
        return packages;
    }

    public String[] getIgnorePackages() {
        if (ignorePackages == null) {
            ignorePackages = new String[0];
        }
        return ignorePackages;
    }

    public Customizer[] getCustomizers() {
        if (customizers == null) {
            customizers = new Customizer[0];
        }
        return customizers;
    }

    /**
     * 自定义规则
     */
    @Data
    public static class Customizer {
        // 类名
        private String className;
        // 方法名
        private String methodName;
        // 方法描述符
        private String descriptor;
        // 行输出渲染器
        private String rendererClass;
    }


    /**
     * 通过类名匹配是否属于设置的包下
     *
     * @param className
     * @return
     */
    public boolean matchPackages(String className) {
        for (String ignorePackage : this.getIgnorePackages()) {
            if (className.startsWith(ignorePackage)) {
                return false;
            }
        }
        for (String aPackage : this.getPackages()) {
            if (className.startsWith(aPackage)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取自定义规则
     *
     * @return
     */
    public AgentProperties.Customizer getMatchCustomizer(String className, String methodName, String descriptor) {
        for (AgentProperties.Customizer customizer : this.getCustomizers()) {
            if (customizer.getClassName().equals(getOriginName(className, "$"))
                    || getCachedClass(customizer.getClassName()).isAssignableFrom(getCachedClass(getOriginName(className, "$")))) {
                if (customizer.getMethodName().equals("*")) {
                    return customizer;
                }
                if (customizer.getMethodName().equals(getOriginName(methodName, "$"))) {
                    if (customizer.getDescriptor() == null || "".equals(customizer.getDescriptor()) || "*".equals(customizer.getDescriptor())) {
                        return customizer;
                    }
                    if (customizer.getDescriptor().equals(descriptor)) {
                        return customizer;
                    }
                }
            }
        }
        return null;
    }


    /**
     * 通过自定规则判断使用默认或者自定义的渲染器渲染
     *
     * @param span
     * @return
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @SneakyThrows
    public String render(Span span) {
        LineRenderer lineRenderer = LineRenderer.DEFAULT;
        AgentProperties.Customizer customizer = this.getMatchCustomizer(span.getClassName(), span.getMethodName(), span.getDescriptor());
        if (customizer != null && customizer.getRendererClass() != null) {
            lineRenderer = (LineRenderer) getCachedClass(customizer.getRendererClass()).getConstructors()[0].newInstance();
        }
        return lineRenderer.render(span, this);
    }


    /**
     * 从缓存中获取class
     *
     * @param className
     * @return
     */
    @SneakyThrows
    public Class getCachedClass(String className) {
        Class aClass = classCacheMap.get(className);
        if (aClass == null) {
            aClass = Class.forName(className);
            classCacheMap.put(className, aClass);
        }
        return aClass;
    }


    /**
     * 获取原始名
     *
     * @param proxyName       代理名称
     * @param proxyIdentifier 代理标识符, class类型: $$  method类型: $
     * @return
     */
    public String getOriginName(String proxyName, String proxyIdentifier) {
        String originMethodName = proxyName;
        int proxySplitIndex = proxyName.indexOf(proxyIdentifier);
        if (proxySplitIndex > -1) {
            originMethodName = proxyName.substring(0, proxySplitIndex);
        }
        if (originMethodName == null || "".equals(originMethodName)) {
            originMethodName = proxyName;
        }
        return originMethodName;
    }

}
