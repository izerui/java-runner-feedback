# java-runner-feedback

## 功能
java类调用过程记录Agent, 并以树状调用层次结构输出。
[![](https://jitpack.io/v/izerui/wx-java-sdk.svg)](https://jitpack.io/#izerui/wx-java-sdk)
效果图:
<img src="https://github.com/izerui/java-runner-feedback/blob/main/demo.jpg?raw=true">

## 使用
1. 添加vm-options配置:
```
-javaagent:/Users/serv/runner-feedback-agent.jar
```

如果需要监看线程池的调用关系链路请在feedback-agent之前加入
```
-javaagent:/Users/serv/transmittable-thread-local-2.14.3.jar
```

2. 指定切入口:
添加仓库
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
添加依赖
```
<dependency>
    <groupId>com.github.izerui</groupId>
    <artifactId>runner-feedback-core</artifactId>
    <version>1.1.0</version>
</dependency>
```
声明要切入的入口方法:
```
@Tracer("切入口")
public RespVO<PageVo<BusinessTaskVo>> findTaskList(@RequestBody BaseQueryVo vo) {
    // ...
}
```

3. 添加配置文件 `feedback.yaml` 到类路径中
```
# 是否启用
enabled: true
# 要扫描的包路径前缀
packages:
  - com.yj2025
  - com.ecworking
# 忽略的包路径前缀
ignore_packages:
  - com.github.izerui
# 忽略的包含指定注解的类
ignore_annotations:
  - org.springframework.cloud.openfeign.FeignClient
# 是否在树状输出中包含set方法
show_setter: true
# 是否在树状输出中包含get方法
show_getter: true
# 是否调试状态
debugger: true
# 树状每行的输出格式
output_format: "${success} ${time}${count} ${thread} ${package}(${file}:${line})${method}${descriptor} ${args}"
# 除了扫描packages以外的，另外包含的接口或者类，或者接口或者类的指定方法
interface_methods:
  - feign.Client#execute(Lfeign/Request;Lfeign/Request$Options;)Lfeign/Response;
  - java.sql.PreparedStatement#*
  - java.sql.Statement#*
```

以下非本工程开发可忽略
====================
本工程调试:
在sample模块的application运行配置中
1. 添加 vm options
2. 添加 agent模块的 前置build任务`clean package`

参考资料:
bytebuddy:
    https://www.jianshu.com/p/672a9bdd12e1
StackFrame:
    https://www.baeldung.com/java-name-of-executing-method
