package com.github.izerui.support;

public class AssertionFailure extends RuntimeException{
    public AssertionFailure() {
    }

    public AssertionFailure(String message) {
        super(message);
    }

    public AssertionFailure(String message, Throwable cause) {
        super(message, cause);
    }
}
