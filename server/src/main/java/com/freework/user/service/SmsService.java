package com.freework.user.service;

import com.freework.common.loadon.result.entity.ResultVo;
import org.springframework.stereotype.Service;

/**
 * @author daihongru
 */
@Service
public interface SmsService {

    String VERIFICATION_SMS_CODE_KEY = "verificationSmsCode_";

    String CHECK_EVIDENCE="checkEvidence_";

    /**
     * 异步发送验证短信
     *
     * @param phoneNumber
     */
    void sendVerificationSms(String phoneNumber);

    /**
     * 查询验证码是否正确
     *
     * @param phone
     * @param code
     * @return
     */
    ResultVo checkVerificationCode(String phone, String code);
}
