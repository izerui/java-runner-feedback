package com.github.sample.dao;

import java.util.List;

public class SampleDao {

    public boolean getBoolean() {
        return true;
    }

    public boolean getVoid(String user, boolean isTrue) {
        return true;
    }

    public boolean getVoid(String user, boolean isTrue, int i) {
        return true;
    }

    public boolean isList(List<String> list) {
        return true;
    }

    public String getName(String user) {
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        return "hello: " + user;
//        throw new RuntimeException("dss");
    }

    public String getName(String user, boolean isUser) {

        return "hello: " + isUser + " - " + user;
    }

    public String getName2(String user) {
        return "hello: " + user;
//        throw new RuntimeException("dss");
    }
}
