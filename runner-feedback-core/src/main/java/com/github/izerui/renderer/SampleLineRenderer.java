package com.github.izerui.renderer;

import com.github.izerui.AgentProperties;
import com.github.izerui.support.Span;

public class SampleLineRenderer implements LineRenderer {

    @Override
    public String render(Span span, AgentProperties properties) {
        String rendered = DEFAULT.render(span, properties);
        return rendered + " 当前对象:" + span.getTarget();
    }
}
