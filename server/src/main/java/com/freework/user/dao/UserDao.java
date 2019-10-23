package com.freework.user.dao;


import com.freework.user.entity.User;

import java.util.List;

/**
 * @author daihongru
 */
public interface UserDao {
    /**
     * 验证账户名与密码，返回一个user对象，仅登陆使用
     *
     * @param user
     * @return user
     */
    User login(User user);

    /**
     * 根据传入的参数查询，返回一个user列表
     *
     * @param user
     * @return userList
     */
    List<User> query(User user);

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    int register(User user);

    /**
     * 查询邮箱或手机号码是否存在
     *
     * @param user
     * @return
     */
    User queryEmailOrPhoneExist(User user);

    /**
     * 修改用户密码
     *
     * @param user
     * @return
     */
    int updatePassword(User user);

    /**
     * 修改头像
     *
     * @param user
     * @return
     */
    int updateImg(User user);

    /**
     * 修改用户个人资料
     *
     * @param user
     * @return
     */
    int updateData(User user);
}
