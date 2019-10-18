package com.freework.user.service.impl;

import com.freework.common.loadon.cache.JedisUtil;
import com.freework.common.loadon.util.DateUtil;
import com.freework.common.loadon.util.JsonUtil;
import com.freework.notify.client.vo.EmailVo;
import com.freework.notify.client.vo.SmsVo;
import com.freework.user.dao.MessageLogDao;
import com.freework.user.entity.MessageLog;
import com.freework.user.producer.EmailSender;
import com.freework.user.producer.SmsSender;
import com.freework.user.service.MessageLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author daihongru
 */
@Service
public class MessageLogServiceImpl implements MessageLogService {
    private static Logger logger = LoggerFactory.getLogger(MessageLogServiceImpl.class);
    @Autowired(required = false)
    private MessageLogDao messageLogDao;
    @Autowired(required = false)
    private JedisUtil.Keys jedisKeys;
    @Autowired(required = false)
    private JedisUtil.Strings jedisStrings;
    @Autowired
    private SmsSender smsSender;
    @Autowired
    private EmailSender emailSender;

    @Override
    public void persistence(String messageLogKey, Integer status) {
        String jsonString = jedisStrings.get(messageLogKey);
        MessageLog messageLog = new MessageLog();
        try {
            messageLog = JsonUtil.jsonToObject(jsonString, MessageLog.class);
        } catch (Exception e) {
            logger.error("将JSON转为MessageLog对象时异常：" + e.getMessage());
        }
        messageLog.setStatus(status);
        messageLogDao.insertMessageLog(messageLog);
        jedisKeys.del(messageLogKey);
    }

    @Override
    public void resendSms(String messageLogKey, MessageLog messageLog) {
        messageLog.setTryCount(messageLog.getTryCount() + 1);
        messageLog.setLastEditTime(new Date());
        messageLog.setNextRetryTime(DateUtil.getLaterTimeMinute(1));
        String newJsonString = null;
        try {
            newJsonString = JsonUtil.objectToJson(messageLog);
        } catch (Exception e) {
            logger.error("将MessageLog对象转为JSON时异常：" + e.getMessage());
        }
        jedisStrings.set(messageLogKey, newJsonString);
        SmsVo smsVo = new SmsVo();
        try {
            smsVo = JsonUtil.jsonToObject(messageLog.getMessage(), SmsVo.class);
        } catch (Exception e) {
            logger.error("将JSON转为Sms对象时异常：" + e.getMessage());
        }
        smsSender.send(smsVo);
    }

    @Override
    public void resendEmail(String messageLogKey, MessageLog messageLog) {
        messageLog.setTryCount(messageLog.getTryCount() + 1);
        messageLog.setLastEditTime(new Date());
        messageLog.setNextRetryTime(DateUtil.getLaterTimeMinute(1));
        String newJsonString = null;
        try {
            newJsonString = JsonUtil.objectToJson(messageLog);
        } catch (Exception e) {
            logger.error("将MessageLog对象转为JSON时异常：" + e.getMessage());
        }
        jedisStrings.set(messageLogKey, newJsonString);
        EmailVo emailVo = new EmailVo();
        try {
            emailVo = JsonUtil.jsonToObject(messageLog.getMessage(), EmailVo.class);
        } catch (Exception e) {
            logger.error("将JSON转为Email对象时异常：" + e.getMessage());
        }
        emailSender.send(emailVo);
    }
}
