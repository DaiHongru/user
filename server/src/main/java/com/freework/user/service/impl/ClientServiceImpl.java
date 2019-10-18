package com.freework.user.service.impl;

import com.freework.user.dao.UserDao;
import com.freework.user.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService {
    @Autowired(required = false)
    private UserDao userDao;
}
