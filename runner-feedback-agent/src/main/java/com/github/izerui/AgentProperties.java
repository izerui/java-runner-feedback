package com.github.izerui;

import lombok.Data;

@Data
public class AgentProperties {

    /**
     * 是否启用
     */
    private boolean enabled;
    /**
     * 每行输出格式
     */
    private String output_format;
    /**
     * 是否拦截并显示set方法
     */
    private boolean show_setter;
    /**
     * 是否拦截并显示get方法
     */
    private boolean show_getter;
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
    private String[] ignore_packages;
    /**
     * 忽略带指定注解的
     */
    private String[] ignore_annotations;
    /**
     * 扩展的扫描入口,由系统定义，固定扫描比如:
     * 1. sql 执行
     * 2. http请求
     * 3. 消息发送等
     * 扫描记录继承至如下接口的方法 格式: [class]#[method][descriptor]
     */
    private String[] class_methods;

}
