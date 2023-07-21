package com.github.sample.service.impl;

import com.github.sample.dao.SampleDao;
import com.github.sample.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SampleServiceImpl implements SampleService {

    @Autowired
    private SampleDao sampleDao;

    @Override
    public String getUserName_0_2(String user) {
        return sampleDao.getName_0_2_3(user);
    }

    @Override
    public void writeName_0_1(String user, boolean admin) {
        for (int i = 0; i < 5; i++) {
            sampleDao.getVoid_0_1_2(user, admin);
        }
    }
}
