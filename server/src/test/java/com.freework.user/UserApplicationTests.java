package com.freework.user;

import com.freework.cvitae.client.vo.CvitaeVo;
import com.freework.user.dao.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserApplicationTests {
    @Autowired(required = false)
    private UserDao userDao;

    @Test
    public void contextLoads() {
        List<CvitaeVo> cvitaeVoList = new ArrayList<>();
        System.out.println(cvitaeVoList.size());
    }

}
