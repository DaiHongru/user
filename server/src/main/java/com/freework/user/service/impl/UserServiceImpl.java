package com.freework.user.service.impl;

import com.freeowork.user.client.key.UserRedisKey;
import com.freeowork.user.client.vo.UserVo;
import com.freework.common.loadon.cache.JedisUtil;
import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.common.loadon.result.enums.ResultStatusEnum;
import com.freework.common.loadon.result.util.ResultUtil;
import com.freework.common.loadon.util.DesUtil;
import com.freework.common.loadon.util.FileUtil;
import com.freework.common.loadon.util.JsonUtil;
import com.freework.common.loadon.util.PathUtil;
import com.freework.cvitae.client.feign.CvitaeClient;
import com.freework.cvitae.client.vo.CvitaeVo;
import com.freework.cvitae.client.vo.EnterpriseCvVo;
import com.freework.user.dao.UserDao;
import com.freework.user.dto.ImageHolder;
import com.freework.user.entity.User;
import com.freework.user.enums.UserStateEnum;
import com.freework.user.exceptions.UserOperationException;
import com.freework.user.service.EmailService;
import com.freework.user.service.SmsService;
import com.freework.user.service.UserService;
import com.freework.user.util.ImageUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author daihongru
 */
@Service
public class UserServiceImpl implements UserService {
    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired(required = false)
    private UserDao userDao;
    @Autowired(required = false)
    private JedisUtil.Keys jedisKeys;
    @Autowired(required = false)
    private JedisUtil.Strings jedisStrings;
    @Autowired
    private SmsService smsService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CvitaeClient cvitaeClient;

    @Override
    public ResultVo getCurrentUserInfo(String token) {
        String userKey = UserRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(userKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        return ResultUtil.success(getCurrentUserVo(userKey));
    }

    @Override
    public ResultVo utokenLogin(String token) {
        String key = UserRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(key)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        UserVo userVo = getCurrentUserVo(key);
        return ResultUtil.success(userVo);
    }

    @Override
    public ResultVo loginCheck(User user, int timeout, String oldToken) {
        if (user == null || user.getPassword() == null || user.getPhone() == null) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        if (timeout < 1) {
            timeout = 1;
        }
        if (StringUtils.isNotEmpty(oldToken)) {
            String oldKey = UserRedisKey.LOGIN_KEY + oldToken;
            if (jedisKeys.exists(oldKey)) {
                jedisKeys.del(oldKey);
            }
        }
        user.setPassword(DesUtil.getEncryptString(user.getPassword()));
        User u = userDao.login(user);
        if (u == null) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        if (u.getStatus() == UserStateEnum.STOP.getState()) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(u, userVo);
        Map<String, Object> map = cvitaeClient.getUserCvitaeInfo(userVo.getUserId());
        userVo.setCvitaeVoList((List<CvitaeVo>) map.get(CvitaeClient.CVITAE_VO_LIST_KEY));
        userVo.setEnterpriseCvVoList((List<EnterpriseCvVo>) map.get(CvitaeClient.ENTERPRISE_CV_VO_LIST_KEY));
        userVo.setPassCvitaeCount((Integer) map.get(CvitaeClient.PASS_CVITAE_COUNT_KEY));
        String token = UUID.randomUUID().toString();
        String userKey = UserRedisKey.LOGIN_KEY + token;
        String userStr = JsonUtil.objectToJson(userVo);
        jedisStrings.setEx(userKey, timeout * USER_REDIS_TIMEOUT_UNIT, userStr);
        return ResultUtil.success(token);
    }

    @Override
    public ResultVo logout(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = UserRedisKey.LOGIN_KEY + token;
            if (jedisKeys.exists(userKey)) {
                jedisKeys.del(userKey);
            }
        }
        return ResultUtil.success();
    }

    @Override
    public ResultVo register(User user) {
        if (user == null) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        user.setStatus(UserStateEnum.PASS.getState());
        user.setPassword(DesUtil.getEncryptString(user.getPassword()));
        user.setCreateTime(new Date());
        user.setLastEditTime(new Date());
        try {
            int judgeNum = userDao.register(user);
            if (judgeNum <= 0) {
                throw new UserOperationException("用户注册失败");
            }
        } catch (Exception e) {
            throw new UserOperationException("用户注册时发生异常:" + e.getMessage());
        }
        emailService.sendActivatedMail(user);
        return ResultUtil.success();
    }

    @Override
    public ResultVo retrievePassword(User user, String evidence) {
        if (StringUtils.isEmpty(evidence)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        if (!jedisKeys.exists(SmsService.CHECK_EVIDENCE + evidence)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        if (user.getPhone() == null || user.getPassword() == null) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        if (!user.getPhone().equals(jedisStrings.get(SmsService.CHECK_EVIDENCE + evidence))) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        user.setPassword(DesUtil.getEncryptString(user.getPassword()));
        user.setLastEditTime(new Date());
        try {
            int judgeNum = userDao.updatePassword(user);
            if (judgeNum <= 0) {
                throw new UserOperationException("修改用户密码失败");
            }
        } catch (Exception e) {
            throw new UserOperationException("用户找回密码时发生异常:" + e.getMessage());
        }
        return ResultUtil.success();
    }

    @Override
    public ResultVo portraitUpload(ImageHolder imageHolder, String token) {
        if (imageHolder == null) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String userKey = UserRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(userKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        UserVo userVo = getCurrentUserVo(userKey);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = sdf.format(new Date());
        String fileName = "userPortrait_" + timeStr + "_" + userVo.getUserId();
        String extension = FileUtil.getFileExtension(imageHolder.getImageName());
        String targetAddr = PathUtil.getUserPortraitPath(userVo.getUserId());
        FileUtil.mkdirPath(targetAddr);
        String path = targetAddr + fileName + extension;
        ImageUtil.storageImage(path, imageHolder);
        if (userVo.getImg() != null) {
            StringBuffer buffer = new StringBuffer(userVo.getImg());
            buffer.delete(0, 15);
            FileUtil.deleteFileOrPath(buffer.toString());
        }
        userVo.setImg("/localresources" + path);
        userVo.setLastEditTime(new Date());
        User user = new User();
        BeanUtils.copyProperties(userVo, user);
        try {
            int judgeNum = userDao.updateImg(user);
            if (judgeNum <= 0) {
                logger.error("上传头像时储存文件路径失败");
                throw new UserOperationException("上传头像时储存文件路径失败");
            }
        } catch (Exception e) {
            logger.error("上传头像时储存文件路径异常:" + e.getMessage());
            throw new UserOperationException("上传头像时储存文件路径异常:" + e.getMessage());
        }
        setCurrentUserVo(userVo, userKey);
        return ResultUtil.success();
    }

    @Override
    public ResultVo queryEmailOrPhoneExist(String email, String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setEmail(email);
        User u = userDao.queryEmailOrPhoneExist(user);
        if (u == null) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        } else {
            return ResultUtil.success();
        }
    }

    @Override
    public ResultVo sendVerificationSms(String token) {
        String userKey = UserRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(userKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        UserVo userVo = getCurrentUserVo(userKey);
        smsService.sendVerificationSms(userVo.getPhone());
        return ResultUtil.success();
    }

    @Override
    public ResultVo sendVerificationEmail(String token) {
        String userKey = UserRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(userKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        UserVo userVo = getCurrentUserVo(userKey);
        emailService.sendVerificationEmail(userVo.getEmail());
        return ResultUtil.success();
    }

    /**
     * 获取当前登录用户
     *
     * @param key
     * @return
     */
    private UserVo getCurrentUserVo(String key) {
        String userStr = jedisStrings.get(key);
        UserVo userVo = JsonUtil.jsonToObject(userStr, UserVo.class);
        return userVo;
    }

    /**
     * 设置当前登录用户的信息
     *
     * @param userVo
     * @param key
     */
    private void setCurrentUserVo(UserVo userVo, String key) {
        String userStr = JsonUtil.objectToJson(userVo);
        jedisStrings.set(key, userStr);
    }
}
