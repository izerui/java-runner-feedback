# java-runner-feedback
https://www.baeldung.com/byte-buddy

java 类 调用过程记录 agent

使用方式：
java vm options添加:
```
-javaagent:/Users/serv/github/java-runner-feedback/transmittable-thread-local-2.14.3.jar // 解决线程池调用的threadlocal传递问题
-javaagent:/Users/liuyuhua/github/java-runner-feedback/runner-feedback-agent/target/runner-feedback-agent.jar
-Dfeedback.packages=com.yj2025,com.ecworking // 多个包名以逗号分隔(尽量按顺序优先匹配)
-Dfeedback.deepshow=false // 可选(减少调用链输出)，当为true时可输出定义的包名以外的类的继承调用日志 默认为true
```


调试:
添加 vm options并且添加 agent模块的 前置build任务`clean package`

链接：https://www.jianshu.com/p/672a9bdd12e1
常用注解说明
```
@Argument	绑定单个参数
@AllArguments	绑定所有参数的数组
@This	当前被拦截的、动态生成的那个对象
@Super	当前被拦截的、动态生成的那个对象的父类对象
@Origin	可以绑定到以下类型的参数：Method 被调用的原始方法 Constructor 被调用的原始构造器 Class 当前动态创建的类 MethodHandle MethodType Field 拦截的字段
@DefaultCall	调用默认方法而非super的方法
@SuperCall	用于调用父类版本的方法
@Super	注入父类型对象，可以是接口，从而调用它的任何方法
@RuntimeType	可以用在返回值、参数上，提示ByteBuddy禁用严格的类型检查
@Empty	注入参数的类型的默认值
@StubValue	注入一个存根值。对于返回引用、void的方法，注入null；对于返回原始类型的方法，注入0
@FieldValue	注入被拦截对象的一个字段的值
@Morph	类似于@SuperCall，但是允许指定调用参数
```


todo:
1. 解析调用堆栈，将方法调用记录起来并且连接成树状结构
2. 将所有日志统一放到一个地方，一起以树状输出
https://www.google.com.hk/search?q=java+Thread+StackTrace+parent&newwindow=1&hl=zh-CN&biw=1440&bih=721&ei=QRW4ZPXIAsWlhwPw2ITwDQ&ved=0ahUKEwi17b6SnpuAAxXF0mEKHXAsAd44HhDh1QMIDw&uact=5&oq=java+Thread+StackTrace+parent&gs_lp=Egxnd3Mtd2l6LXNlcnAiHWphdmEgVGhyZWFkIFN0YWNrVHJhY2UgcGFyZW50MgUQIRigATIFECEYoAFI0OEBUKoKWKXgAXAEeAGQAQGYAdMHoAGvK6oBDDAuMTEuNS4xLjYtM7gBA8gBAPgBAfgBAsICChAAGEcY1gQYsAPCAgcQABiKBRhDwgIFEAAYgATCAgcQABgNGIAEwgIEEAAYHsICBhAAGAgYHuIDBBgAIEGIBgGQBgE&sclient=gws-wiz-serp
https://www.baeldung.com/java-name-of-executing-method
https://www.baeldung.com/java-9-stackwalking-api

https://www.freebuf.com/sectool/279742.html

apm:
https://newrelic.com/
