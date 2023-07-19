# java-runner-feedback

java 类 调用过程记录 agent

使用方式：
java vm options添加:
```
-javaagent:/Users/liuyuhua/github/java-runner-feedback/runner-feedback-agent/target/runner-feedback-agent.jar
-Dfeedback.packages=com.github,com.yj2025,com.ecworking // 多个包名以逗号分隔
-Dfeedback.deepshow=true // 可选，当为true时可输出定义的包名以外的类的继承调用日志 默认为false
```