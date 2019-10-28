package com.freework.user.service;

import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.user.dto.ImageHolder;
import com.freework.user.entity.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

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
     * 获取当前登录用户的消息
     *
     * @param token
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultVo getCurrentUserNews(String token, Integer pageNum, Integer pageSize);

    /**
     * 修改消息为已读状态
     *
     * @param token
     * @param newsId
     */
    void updateNewsStatus(String token, Integer newsId);

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
     * 头像上传
     *
     * @param imageHolder
     * @param token
     * @return
     */
    ResultVo portraitUpload(ImageHolder imageHolder, String token);

    /**
     * 修改用户个人资料
     *
     * @param user
     * @param token
     * @return
     */
    ResultVo updateData(User user, String token);

    /**
     * 修改绑定手机
     *
     * @param newPhone
     * @param token
     * @return
     */
    ResultVo updatePhone(String newPhone, String token);

    /**
     * 修改绑定邮箱
     *
     * @param newEmail
     * @param token
     * @return
     */
    ResultVo updateEmail(String newEmail, String token);

    /**
     * 修改企业登录密码
     *
     * @param newPassword
     * @param token
     * @return
     */
    ResultVo updatePassword(String newPassword, String token);

    /**
     * 验证邮箱激活码正确性
     *
     * @param userId
     * @param code
     * @param request
     * @return
     */
    ResultVo checkActivationEmailAddress(Integer userId, String code, HttpServletRequest request);

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
