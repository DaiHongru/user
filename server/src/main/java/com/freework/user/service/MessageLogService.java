package com.freework.user.service;

import com.freework.user.entity.MessageLog;
import org.springframework.stereotype.Service;

/**
 * @author daihongru
 */
@Service
public interface MessageLogService {
    String MESSAGELOG_KEY = "userProducerMessageLog";
    String MESSAGELOG_SMS_KEY = "userProducerMessageLogSms";
    String MESSAGELOG_EMAIL_KEY = "userProducerMessageLogEmail";

    /**
     * 把指定的redis中的MessageLog信息持久化到数据库，并删除该key
     *
     * @param messageLogKey
     * @param status
     */
    void persistence(String messageLogKey, Integer status);

    /**
     * 重新投递关于短信的消息
     *
     * @param messageLogKey
     */
    void resendSms(String messageLogKey, MessageLog messageLog);

    /**
     * 重新投递关于邮件的消息
     *
     * @param messageLogKey
     */
    void resendEmail(String messageLogKey, MessageLog messageLog);
}
