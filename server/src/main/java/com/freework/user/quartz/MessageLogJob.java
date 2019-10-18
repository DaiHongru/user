package com.freework.user.quartz;

import com.freework.common.loadon.cache.JedisUtil;
import com.freework.common.loadon.util.JsonUtil;
import com.freework.user.dao.MessageLogDao;
import com.freework.user.entity.MessageLog;
import com.freework.user.enums.MessageLogStateEnum;
import com.freework.user.service.MessageLogService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Set;

/**
 * 检查redis中未成功投递的消息，并重新发送
 * 重试次数大于3时持久化到数据库中，删除redis中的key
 *
 * @author daihongru
 */
public class MessageLogJob extends QuartzJobBean {
    private static Logger logger = LoggerFactory.getLogger(MessageLogJob.class);
    @Autowired
    private MessageLogDao messageLogDao;
    @Autowired
    private MessageLogService messageLogService;
    @Autowired(required = false)
    private JedisUtil.Keys jedisKeys;
    @Autowired(required = false)
    private JedisUtil.Strings jedisStrings;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String key = MessageLogService.MESSAGELOG_KEY;
        Set<String> setList = jedisKeys.keys(key + "*");
        if (setList == null || setList.size() <= 0) {
            logger.info("当前无未投递的消息");
        } else {
            int tryTotal = 0;
            int delTotal = 0;
            for (String messageLogKey : setList) {
                String jsonString = jedisStrings.get(messageLogKey);
                MessageLog messageLog = JsonUtil.jsonToObject(jsonString, MessageLog.class);
                if (messageLog.getNextRetryTime().getTime() <= System.currentTimeMillis()) {
                    if (messageLog.getTryCount() < 3) {
                        String tag = messageLog.getTag();
                        if (tag.equals("UserSMS")) {
                            messageLogService.resendSms(messageLogKey, messageLog);
                        } else if (tag.equals("UserEmail")) {
                            messageLogService.resendEmail(messageLogKey, messageLog);
                        }
                        tryTotal++;
                    } else {
                        messageLog.setStatus(MessageLogStateEnum.SEND_FAIL.getState());
                        messageLogDao.insertMessageLog(messageLog);
                        jedisKeys.del(messageLogKey);
                        delTotal++;
                    }
                }
            }
            logger.info("当前共" + setList.size() + "条消息未完成投递");
            logger.info("重试[" + tryTotal + "]，失败[" + delTotal);
        }
    }
}
