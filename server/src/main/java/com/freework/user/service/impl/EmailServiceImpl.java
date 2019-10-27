package com.freework.user.service.impl;

import com.freework.common.loadon.cache.JedisUtil;
import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.common.loadon.result.enums.ResultStatusEnum;
import com.freework.common.loadon.result.util.ResultUtil;
import com.freework.common.loadon.util.DateUtil;
import com.freework.common.loadon.util.JsonUtil;
import com.freework.notify.client.vo.EmailVo;
import com.freework.user.entity.MessageLog;
import com.freework.user.entity.User;
import com.freework.user.enums.MessageLogStateEnum;
import com.freework.user.producer.EmailSender;
import com.freework.user.service.EmailService;
import com.freework.user.service.MessageLogService;
import com.freework.user.service.UserService;
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
public class EmailServiceImpl implements EmailService {
    private static Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Autowired(required = false)
    private JedisUtil.Keys jedisKeys;
    @Autowired(required = false)
    private JedisUtil.Strings jedisStrings;
    @Autowired
    private EmailSender emailSender;

    @Override
    public ResultVo checkVerificationCode(String email, String code) {
        String key = VERIFICATION_EMAIL_CODE_KEY + "_" + email;
        if (jedisKeys.exists(key)) {
            if (code.equals(jedisStrings.get(key))) {
                jedisKeys.del(key);
                String evidence = UUID.randomUUID().toString();
                jedisStrings.setEx(CHECK_EVIDENCE + evidence, 60 * 3, email);
                return ResultUtil.success(evidence);
            }
        }
        return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
    }

    @Override
    @Async
    public void sendVerificationEmail(String address) {
        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        String key = VERIFICATION_EMAIL_CODE_KEY + "_" + address;
        jedisStrings.setEx(key, 60 * 3, code);
        String htmlText = "<html lang=\"zh-CN\">\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<img src=\"http://101.132.152.64/img/logo.png\">\n" +
                "<h1 style=\"color: #2e6da4\">FreeWork<br/>\n" +
                "    <hr style=\"width: 50%;margin-left: 0px;\">\n" +
                "    验证您的操作权限\n" +
                "</h1>\n" +
                "<p>尊敬的用户，您好！</p>\n" +
                "<p>您正在进行敏感操作，我们需要进行验证</p>\n" +
                "<p>本次操作验证编码如下（3分钟内有效）：</p>\n" +
                "<u style=\"font-size:30px;font-weight: bold; color: #2e6da4;\">" + code + "</u>\n" +
                "<p>如果这不是您的操作，那么您的账号已不再安全，请立即修改密码。</p>\n" +
                "<p>谢谢！</p>\n" +
                "<p>FreeWork团队</p>\n" +
                "</html>";
        EmailVo emailVo = new EmailVo();
        emailVo.setAddress(address);
        emailVo.setHtmlText(htmlText);
        emailVo.autoSetMessageId();
        MessageLog messageLog = new MessageLog();
        messageLog.setTag("UserEmail");
        messageLog.setMessageId(emailVo.getMessageId());
        messageLog.setMessage(JsonUtil.objectToJson(emailVo));
        messageLog.setTryCount(1);
        messageLog.setStatus(MessageLogStateEnum.SENDING.getState());
        messageLog.setNextRetryTime(DateUtil.getLaterTimeMinute(1));
        messageLog.setCreateTime(new Date());
        messageLog.setLastEditTime(new Date());
        String jsonString = JsonUtil.objectToJson(messageLog);
        String messageLogKey = MessageLogService.MESSAGELOG_EMAIL_KEY + "_" + messageLog.getMessageId();
        jedisStrings.set(messageLogKey, jsonString);
        emailSender.send(emailVo);
    }

    @Async
    @Override
    public void sendActivatedMail(User user) {
        Integer id = user.getUserId();
        String code = getCodeString(16).toString();
        String key = UserService.USER_EMAIL_ACTIVATION_CODE_KEY + "_Id_" + id;
        jedisStrings.setEx(key, 60 * 60 * 2, code);
        String htmlText = "<html lang=\"zh-CN\">\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<img src=\"http://101.132.152.64/img/logo.png\">\n" +
                "<h1 style=\"color: #2e6da4\">FreeWork<br/>\n" +
                "<hr style=\"width: 50%;margin-left: 0px;\">\n" +
                "激活您的电子邮箱地址\n" +
                "</h1>\n" +
                "<p>尊敬的用户，您好！</p>\n" +
                "<p>要完成此账户与电子邮箱的关联，我们需要确保这是您的电子邮件地址。</p>\n" +
                "<p>点击下方链接(或复制到浏览器打开)来验证并激活账户，有效时间为两小时。</p>\n" +
                "<a target=\"_blank\" href=\"http://101.132.152.64/html/user/activationemail.html?id=" + id + "&code=" + code + "\">" +
                "http://101.132.152.64/html/user/activationemail.html?id=" + id + "&code=" + code +
                "</a>\n" +
                "<p>如果这不是您的操作，请不要点击，并忽略此邮件。</p>\n" +
                "<p>谢谢！</p>\n" +
                "<p>FreeWork团队</p>\n" +
                "</html>";
        EmailVo emailVo = new EmailVo();
        emailVo.setAddress(user.getEmail());
        emailVo.setHtmlText(htmlText);
        emailVo.autoSetMessageId();
        MessageLog messageLog = new MessageLog();
        messageLog.setTag("UserEmail");
        messageLog.setMessageId(emailVo.getMessageId());
        messageLog.setMessage(JsonUtil.objectToJson(emailVo));
        messageLog.setTryCount(1);
        messageLog.setStatus(MessageLogStateEnum.SENDING.getState());
        messageLog.setNextRetryTime(DateUtil.getLaterTimeMinute(1));
        messageLog.setCreateTime(new Date());
        messageLog.setLastEditTime(new Date());
        String jsonString = JsonUtil.objectToJson(messageLog);
        String messageLogKey = MessageLogService.MESSAGELOG_EMAIL_KEY + "_" + messageLog.getMessageId();
        jedisStrings.set(messageLogKey, jsonString);
        emailSender.send(emailVo);
    }

    /**
     * 获取指定长度的验证码字符串
     *
     * @param length
     * @return code
     */
    private static StringBuffer getCodeString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer code = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            code.append(str.charAt(number));
        }
        return code;
    }
}
