package com.freework.user.controller;

import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.common.loadon.result.enums.ResultStatusEnum;
import com.freework.common.loadon.result.util.ResultUtil;
import com.freework.common.loadon.util.HttpServletRequestUtil;
import com.freework.user.dto.ImageHolder;
import com.freework.user.entity.User;
import com.freework.user.service.EmailService;
import com.freework.user.service.SmsService;
import com.freework.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author daihongru
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private EmailService emailService;

    /**
     * 获取当前登录的用户信息
     *
     * @return
     */
    @GetMapping(value = "current/info")
    public ResultVo getCurrentUserInfo(HttpServletRequest request) {
        String token = request.getHeader("utoken");
        return userService.getCurrentUserInfo(token);
    }

    /**
     * 获取当前登录用户的消息
     *
     * @return
     */
    @GetMapping(value = "current/news")
    public ResultVo getCurrentUserNews(HttpServletRequest request) {
        String token = request.getHeader("utoken");
        return userService.getCurrentUserNews(token);
    }

    /**
     * 修改消息为已读状态
     *
     * @param newsId
     * @param request
     */
    @PutMapping(value = "current/news/{newsId}")
    public void updateNewsStatus(@PathVariable Integer newsId, HttpServletRequest request) {
        String token = request.getHeader("utoken");
        userService.updateNewsStatus(token, newsId);
    }

    /**
     * 通过token自动登录
     *
     * @param request
     * @return
     */
    @GetMapping(value = "current/login")
    public ResultVo utokenLogin(HttpServletRequest request) {
        String token = request.getHeader("utoken");
        return userService.utokenLogin(token);
    }

    /**
     * 用户登录验证
     *
     * @param user
     * @param request
     * @return
     */
    @PostMapping(value = "login")
    public ResultVo loginCheck(@RequestBody User user, HttpServletRequest request) {
        int timeout = request.getIntHeader("timeout");
        String oldToken = request.getHeader("utoken");
        return userService.loginCheck(user, timeout, oldToken);
    }

    /**
     * 注销登录
     *
     * @return
     */
    @GetMapping(value = "logout")
    public ResultVo logout(HttpServletRequest request) {
        String token = request.getHeader("utoken");
        return userService.logout(token);
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @PostMapping(value = "register")
    public ResultVo registerEnterprise(@RequestBody User user) {
        return userService.register(user);
    }

    /**
     * 找回密码
     *
     * @param user
     * @return
     */
    @PutMapping(value = "retrieve/password")
    public ResultVo retrievePassword(@RequestBody User user, HttpServletRequest request) {
        String evidence = request.getHeader("evidence");
        return userService.retrievePassword(user, evidence);
    }

    /**
     * 头像上传
     *
     * @return
     */
    @PostMapping(value = "current/portrait")
    public ResultVo logoUpload(MultipartHttpServletRequest request) throws IOException {
        MultipartFile portrait = request.getFile("portrait");
        ImageHolder imageHolder = new ImageHolder(portrait.getOriginalFilename(), portrait.getInputStream());
        String token = request.getHeader("utoken");
        return userService.portraitUpload(imageHolder, token);
    }

    /**
     * 修改用户个人资料
     *
     * @param user
     * @param request
     * @return
     */
    @PutMapping(value = "current/data")
    public ResultVo updateData(@RequestBody User user, HttpServletRequest request) {
        String token = request.getHeader("utoken");
        return userService.updateData(user, token);
    }

    /**
     * 修改绑定手机
     *
     * @return
     */
    @PutMapping(value = "current/phone")
    public ResultVo updatePhone(String newPhone, HttpServletRequest request) {
        String token = request.getHeader("utoken");
        return userService.updatePhone(newPhone, token);
    }

    /**
     * 修改绑定邮箱
     *
     * @return
     */
    @PutMapping(value = "current/email")
    public ResultVo updateEmail(String newEmail, HttpServletRequest request) {
        String token = request.getHeader("utoken");
        return userService.updateEmail(newEmail, token);
    }

    /**
     * 修改登陆密码
     *
     * @return
     */
    @PutMapping(value = "current/password")
    public ResultVo updatePassword(String newPassword, HttpServletRequest request) {
        String token = request.getHeader("utoken");
        return userService.updatePassword(newPassword, token);
    }


    /**
     * 查询邮箱或手机号码是否存在
     * 如需要存在为false，如注册时查询，则需要传入不为空的inversion参数，即可置反
     *
     * @param request
     * @return
     */
    @PostMapping(value = "contact/exist")
    public ResultVo queryEmailOrPhoneExist(HttpServletRequest request) {
        String email = HttpServletRequestUtil.getString(request, "email");
        String phone = HttpServletRequestUtil.getString(request, "phone");
        String inversion = HttpServletRequestUtil.getString(request, "inversion");
        ResultVo resultVo = userService.queryEmailOrPhoneExist(email, phone);
        if (inversion != null) {
            resultVo.setSuccess(!resultVo.isSuccess());
        }
        return resultVo;
    }

    /**
     * 向当前登陆的用户发送验证短信
     *
     * @param request
     */
    @GetMapping(value = "current/sms")
    public ResultVo sendCurrentVerificationSms(HttpServletRequest request) {
        String token = request.getHeader("utoken");
        return userService.sendVerificationSms(token);
    }

    /**
     * 发送验证短信
     *
     * @param phone
     * @return
     */
    @GetMapping(value = "sms/{phone}")
    public ResultVo sendVerificationSms(@PathVariable String phone) {
        smsService.sendVerificationSms(phone);
        return ResultUtil.success();
    }

    /**
     * 查询短信验证码是否正确
     *
     * @param phone
     * @param code
     * @return
     */
    @GetMapping(value = "sms/{phone}/{code}")
    public ResultVo checkSmsVerificationCode(@PathVariable String phone,
                                             @PathVariable String code) {
        int codeLength = 6;
        if (code.length() == codeLength && phone != null) {
            return smsService.checkVerificationCode(phone, code);
        } else {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
    }

    /**
     * 向当前登陆的用户发送验证码邮件
     *
     * @param request
     */
    @GetMapping(value = "current/email")
    public ResultVo sendCurrentVerificationEmail(HttpServletRequest request) {
        String token = request.getHeader("utoken");
        return userService.sendVerificationEmail(token);
    }

    /**
     * 发送验证邮件
     *
     * @param email
     */
    @GetMapping(value = "email/{email}")
    public ResultVo sendVerificationEmail(@PathVariable String email) {
        emailService.sendVerificationEmail(email);
        return ResultUtil.success();
    }

    /**
     * 查询邮箱验证码是否正确
     *
     * @param email
     * @param code
     * @return
     */
    @GetMapping(value = "email/code/{email}/{code}")
    public ResultVo checkVerificationCode(@PathVariable String email,
                                          @PathVariable String code) {
        int codeLength = 6;
        if (code.length() == codeLength && email != null) {
            return emailService.checkVerificationCode(email, code);
        } else {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
    }
}
