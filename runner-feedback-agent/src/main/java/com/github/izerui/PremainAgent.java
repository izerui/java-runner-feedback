package com.github.izerui;

import java.lang.instrument.Instrumentation;

public interface PremainAgent {
    void premain(String args, Instrumentation instrumentation);
}
