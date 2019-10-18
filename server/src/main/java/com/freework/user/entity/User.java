package com.freework.user.entity;


import java.util.Date;

/**
 * @author daihongru
 */
public class User {
    /**
     * 用户编号，自增主键，唯一标识符，不可为空，新增时不传入
     */
    private Integer userId;

    /**
     * 用户状态，0为冻结，1为正常，不可为空，新增时可不传入，默认为0
     */
    private Integer status;

    /**
     * 用户姓名，不可为空
     */
    private String userName;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户密码，不可为空
     */
    private String password;

    /**
     * 用户手机号码，登录时作为账户名，不可为空
     */
    private String phone;

    /**
     * 用户头像，储存图片的相对路径
     */
    private String img;

    /**
     * 学历（高中及以下，专科、本科、研究生及以上）
     */
    private String education;

    /**
     * 毕业学校
     */
    private String school;

    /**
     * 求职状态（应届毕业生、在职、待业）
     */
    private String situation;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最近修改时间
     */
    private Date lastEditTime;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Date lastEditTime) {
        this.lastEditTime = lastEditTime;
    }
}
