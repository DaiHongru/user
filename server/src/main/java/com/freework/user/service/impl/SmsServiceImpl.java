package com.freework.user.service.impl;

import com.freework.common.loadon.cache.JedisUtil;
import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.common.loadon.result.enums.ResultStatusEnum;
import com.freework.common.loadon.result.util.ResultUtil;
import com.freework.common.loadon.util.DateUtil;
import com.freework.common.loadon.util.JsonUtil;
import com.freework.notify.client.vo.SmsVo;
import com.freework.user.entity.MessageLog;
import com.freework.user.enums.MessageLogStateEnum;
import com.freework.user.producer.SmsSender;
import com.freework.user.service.MessageLogService;
import com.freework.user.service.SmsService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @author daihongru
 */
@Service
public class SmsServiceImpl implements SmsService {
    private static Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
    @Autowired(required = false)
    private JedisUtil.Keys jedisKeys;
    @Autowired(required = false)
    private JedisUtil.Strings jedisStrings;
    @Autowired
    private SmsSender smsSender;

    @Override
    public ResultVo checkVerificationCode(String phoneNumber, String code) {
        String key = VERIFICATION_SMS_CODE_KEY + phoneNumber;
        if (jedisKeys.exists(key)) {
            if (code.equals(jedisStrings.get(key))) {
                jedisKeys.del(key);
                String evidence = UUID.randomUUID().toString();
                jedisStrings.setEx(CHECK_EVIDENCE + evidence, 60 * 3, phoneNumber);
                return ResultUtil.success(evidence);
            }
        }
        return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
    }

    @Override
    @Async
    public void sendVerificationSms(String phoneNumber) {
        if (StringUtils.isEmpty(phoneNumber)) {
            logger.error("发送短信时sendVerificationSms()获取phoneNumber失败");
            return;
        }
        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        String key = VERIFICATION_SMS_CODE_KEY + phoneNumber;
        jedisStrings.setEx(key, 60 * 3, code);
        SmsVo smsVo = new SmsVo();
        smsVo.setPhone(phoneNumber);
        smsVo.setCode(code);
        smsVo.autoSetMessageId();
        MessageLog messageLog = new MessageLog();
        messageLog.setTag("UserSMS");
        messageLog.setMessageId(smsVo.getMessageId());
        try {
            messageLog.setMessage(JsonUtil.objectToJson(smsVo));
        } catch (Exception e) {
            logger.error("将Sms对象转为JSON时异常：" + e.getMessage());
        }
        messageLog.setTryCount(1);
        messageLog.setStatus(MessageLogStateEnum.SENDING.getState());
        messageLog.setNextRetryTime(DateUtil.getLaterTimeMinute(1));
        messageLog.setCreateTime(new Date());
        messageLog.setLastEditTime(new Date());
        String jsonString = null;
        try {
            jsonString = JsonUtil.objectToJson(messageLog);
        } catch (Exception e) {
            logger.error("messageLog：" + e.getMessage());
        }
        String messageLogKey = MessageLogService.MESSAGELOG_SMS_KEY + "_" + messageLog.getMessageId();
        jedisStrings.set(messageLogKey, jsonString);
        smsSender.send(smsVo);
    }
}
