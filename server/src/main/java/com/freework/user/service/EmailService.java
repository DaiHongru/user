package com.freework.user.service;

import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.user.entity.User;
import org.springframework.stereotype.Service;

/**
 * @author daihongru
 */
@Service
public interface EmailService {

    String VERIFICATION_EMAIL_CODE_KEY = "verificationEmailCode";

    String CHECK_EVIDENCE = "checkEvidence_";

    /**
     * 异步发送验证邮件
     *
     * @param email
     */
    void sendVerificationEmail(String email);

    /**
     * 查询验证码是否正确
     *
     * @param email
     * @param code
     * @return
     */
    ResultVo checkVerificationCode(String email, String code);

    /**
     * 发送邮箱激活的邮件
     *
     * @param user
     */
    void sendActivatedMail(User user);
}
