package com.github.sample.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

//用户
@Data
@Table("test_user")
public class TestUser {
    @Id
    private Long id;
    @Version
    private int version;
    private String entCode;
    @CreatedDate
    private Date createTime = new Date();
    private String code;
    private String name;
    private String email;
    private Integer age;
}
