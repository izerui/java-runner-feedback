package com.github.sample.service.impl;

import com.github.sample.dao.SampleDao;
import com.github.sample.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SampleServiceImpl implements SampleService {

    @Autowired
    private SampleDao sampleDao;

    @Override
    public String getUserName(String user) {
        return sampleDao.getName2(user);
    }

    @Override
    public void writeName(String user, boolean admin) {
        sampleDao.getVoid(user, admin);
    }
}
