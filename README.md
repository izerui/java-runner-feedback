# java-runner-feedback
https://www.baeldung.com/byte-buddy

java 类 调用过程记录 agent, 并以树状调用层次结构输出

使用方式：
java vm options添加:
```
// 解决线程池调用的threadlocal传递问题
-javaagent:/Users/serv/github/java-runner-feedback/transmittable-thread-local-2.14.3.jar
-javaagent:/Users/liuyuhua/github/java-runner-feedback/runner-feedback-agent/target/runner-feedback-agent.jar
// 多个包名以逗号分隔(尽量按顺序优先匹配)
-Dfeedback.packages=com.yj2025,com.ecworking
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

<img src="https://github.com/izerui/java-runner-feedback/blob/main/demo.jpg?raw=true">