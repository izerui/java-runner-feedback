# java-runner-feedback

[![](https://jitpack.io/v/izerui/java-runner-feedback.svg)](https://jitpack.io/#izerui/java-runner-feedback)

## 功能
java类调用过程记录Agent, 并以树状调用层次结构输出。
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
添加依赖
```
<dependency>
    <groupId>com.github.izerui</groupId>
    <artifactId>runner-feedback-core</artifactId>
    <version>1.1.3</version>
</dependency>
```
声明要切入的入口方法:
```
@Tracer("切入口")
public RespVO<PageVo<BusinessTaskVo>> findTaskList(@RequestBody BaseQueryVo vo) {
    // ...
}
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
