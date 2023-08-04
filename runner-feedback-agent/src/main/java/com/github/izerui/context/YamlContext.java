package com.github.izerui.context;

import com.github.izerui.AgentProperties;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class YamlContext {

    public AgentProperties getProperties() {
        Yaml yaml = new Yaml();
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("feedback.yaml");
        if (resourceAsStream != null) {
            return yaml.loadAs(resourceAsStream, AgentProperties.class);
        }
        return new AgentProperties();
    }
}
