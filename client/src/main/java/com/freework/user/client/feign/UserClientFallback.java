package com.freework.user.client.feign;

import com.freework.user.client.vo.UserVo;
import org.springframework.stereotype.Component;

/**
 * @author daihongru
 */
@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserVo getUserInfo(Integer userId) {
        UserVo userVo = new UserVo();
        userVo.setUserName("(系统繁忙)");
        userVo.setSex("(系统繁忙)");
        userVo.setEducation("(系统繁忙)");
        return userVo;
    }
}
