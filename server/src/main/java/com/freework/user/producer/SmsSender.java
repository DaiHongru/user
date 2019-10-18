package com.freework.user.producer;

import com.freework.common.loadon.cache.JedisUtil;
import com.freework.notify.client.vo.SmsVo;
import com.freework.user.enums.MessageLogStateEnum;
import com.freework.user.service.MessageLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author daihongru
 */
@Component
public class SmsSender {
    private static Logger logger = LoggerFactory.getLogger(SmsSender.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MessageLogService messageLogService;
    @Autowired(required = false)
    private JedisUtil.Keys jedisKeys;

    private final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            String messageLogKey = MessageLogService.MESSAGELOG_SMS_KEY + "_" + correlationData.getId();
            if (ack) {
                jedisKeys.del(messageLogKey);
            } else {
                logger.error("RabbitMQ服务器异常：" + cause);
                messageLogService.persistence(messageLogKey, MessageLogStateEnum.SERVER_ERROR.getState());
            }
        }
    };

    public void send(SmsVo smsVo) {
        rabbitTemplate.setConfirmCallback(confirmCallback);
        CorrelationData correlationData = new CorrelationData(smsVo.getMessageId());
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.convertAndSend("sms-exchange", "sms.verificationcode", smsVo, correlationData);
    }
}
