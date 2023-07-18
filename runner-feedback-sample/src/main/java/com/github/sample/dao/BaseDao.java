package com.github.sample.dao;

public abstract class BaseDao {

    public String getName(String user, boolean isUser) {

        return "hello: " + isUser + " - " + user;
    }
}
