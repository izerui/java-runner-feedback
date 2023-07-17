package com.github.izerui;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Context {

    private final static Logger LOGGER = LoggerFactory.getLogger(Context.class);

    private final String[] packages;

    private final String[] ignorePackages = {"com.github.izerui"};

    public Context() {
        String feedbackPackages = System.getProperties().getProperty("feedback.packages");
        if (feedbackPackages != null && !"".equals(feedbackPackages)) {
            packages = feedbackPackages.split(",");
        } else {
            LOGGER.warn("未获取到正确的可【feedback】的包匹配字符串, 请正确配置agent类似: -javaagent:runner-feedback-agent-jar-with-dependencies.jar -Dfeedback.packages=com.github.sample,com.yj2025");
            packages = new String[0];
        }
    }

    public boolean matchPackages(String className) {
        for (String ignorePackage : ignorePackages) {
            if (className.startsWith(ignorePackage)) {
                return false;
            }
        }
        for (String aPackage : packages) {
            if (className.startsWith(aPackage)) {
                return true;
            }
        }
        return false;
    }

    public ElementMatcher<? super TypeDescription> getTypeMatcher() {
        ElementMatcher.Junction<? super TypeDescription> matcher = ElementMatchers.any();
        for (String ignorePackage : this.ignorePackages) {
            matcher = matcher.and(ElementMatchers.not(ElementMatchers.nameStartsWith(ignorePackage)));
        }
        ElementMatcher.Junction<? super TypeDescription> orMatcher = ElementMatchers.none();
        for (String aPackage : this.packages) {
            orMatcher = orMatcher.or(ElementMatchers.nameStartsWith(aPackage));
        }
        return matcher.and(orMatcher);
    }
}
