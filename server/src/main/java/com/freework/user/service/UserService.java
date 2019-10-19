package com.freework.user.service;

import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.user.entity.User;
import org.springframework.stereotype.Service;

/**
 * @author daihongru
 */
@Service
public interface UserService {
    /**
     * 用户登录后在redis中超时时间单位，一天
     */
    int USER_REDIS_TIMEOUT_UNIT = 60 * 60 * 24;

    /**
     * 用户激活邮件验证码的redis key
     */
    String USER_EMAIL_ACTIVATION_CODE_KEY = "userEmailActivationCode";

    /**
     * 获取当前登录的用户信息
     *
     * @param token
     * @return
     */
    ResultVo getCurrentUserInfo(String token);

    /**
     * utoken自动登录
     *
     * @param token
     * @return
     */
    ResultVo utokenLogin(String token);

    /**
     * 用户登录
     *
     * @param user
     * @param timeout
     * @param oldToken
     * @return
     */
    ResultVo loginCheck(User user, int timeout, String oldToken);

    /**
     * 退出登录时清除redis缓存的信息
     *
     * @param token
     * @return
     */
    ResultVo logout(String token);

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    ResultVo register(User user);

    /**
     * 找回登录密码
     *
     * @param user
     * @param evidence
     * @return
     */
    ResultVo retrievePassword(User user, String evidence);

    /**
     * 查询邮箱或手机号码是否存在
     *
     * @param email
     * @param phone
     * @return
     */
    ResultVo queryEmailOrPhoneExist(String email, String phone);

    /**
     * 向当前登录的用户发送验证短信
     *
     * @param token
     * @return
     */
    ResultVo sendVerificationSms(String token);

    /**
     * 向当前登录的用户发送验证邮件
     *
     * @param token
     * @return
     */
    ResultVo sendVerificationEmail(String token);
}
