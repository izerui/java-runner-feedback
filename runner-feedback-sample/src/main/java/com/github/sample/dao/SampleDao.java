package com.github.sample.dao;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SampleDao extends BaseDao{

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



    public String getName2(String user) {
        return "hello: " + user;
//        throw new RuntimeException("dss");
    }
}
