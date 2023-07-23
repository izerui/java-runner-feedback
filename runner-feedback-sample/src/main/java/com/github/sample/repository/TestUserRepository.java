package com.github.sample.repository;

import com.github.sample.entity.TestUser;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestUserRepository extends CrudRepository<TestUser, Long>, PagingAndSortingRepository<TestUser, Long> {

    @Query("select * from test_user where code like CONCAT('%', :code,'%') ")
    List<TestUser> findList(@Param("entCode") String entCode, @Param("code") String code);


    List<TestUser> findByCode(String entCode, String code);

    @Query("select * from test_user ")
    List<TestUser> findList1(String entCode);
}
