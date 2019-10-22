package com.freework.user.service.impl;

import com.freework.user.client.vo.UserVo;
import com.freework.user.dao.UserDao;
import com.freework.user.entity.User;
import com.freework.user.service.ClientService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author daihongru
 */
@Service
public class ClientServiceImpl implements ClientService {
    @Autowired(required = false)
    private UserDao userDao;

    @Override
    public UserVo getUserInfo(Integer userId) {
        User user = new User();
        user.setUserId(userId);
        List<User> userList = userDao.query(user);
        if (userList == null || userList.size() <= 0) {
            return new UserVo();
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userList.get(0), userVo);
        return userVo;
    }
}
