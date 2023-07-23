package com.github.sample.service.impl;

import com.github.sample.dao.SampleDao;
import com.github.sample.entity.TestUser;
import com.github.sample.repository.TestUserRepository;
import com.github.sample.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SampleServiceImpl implements SampleService {

    @Autowired
    private SampleDao sampleDao;
    @Autowired
    private TestUserRepository userRepository;

    @Override
    public String getUserName_0_2(String user) {
        return sampleDao.getName_0_2_3(user);
    }

    @Override
    public void writeName_0_1(String user, boolean admin) {
        for (int i = 0; i < 5; i++) {
            sampleDao.getVoid_0_1_2(user, admin);
        }
        List<TestUser> users = userRepository.findByCode("ent001", "code10");
        System.out.println(users.size());
    }
}
