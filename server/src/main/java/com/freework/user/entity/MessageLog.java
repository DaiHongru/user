package com.freework.user.entity;


import java.io.Serializable;
import java.util.Date;

/**
 * @author daihongru
 */
public class MessageLog implements Serializable {

    /**
     * 消息唯一id
     */
    private String messageId;

    /**
     * 消息类型标示
     */
    private String tag;

    /**
     * 消息内容
     */
    private String message;
    /**
     * 重试次数，默认1
     */
    private Integer tryCount;
    /**
     * 投递状态，0投递中，1Rabbitmq服务器异常，2投递失败
     */
    private Integer status;
    /**
     * 下一次投递时间
     */
    private Date nextRetryTime;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后更新时间
     */
    private Date lastEditTime;

    public MessageLog() {
    }

    public MessageLog(String messageId, Integer status, Date lastEditTime) {
        this.messageId = messageId;
        this.status = status;
        this.lastEditTime = lastEditTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTryCount() {
        return tryCount;
    }

    public void setTryCount(Integer tryCount) {
        this.tryCount = tryCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getNextRetryTime() {
        return nextRetryTime;
    }

    public void setNextRetryTime(Date nextRetryTime) {
        this.nextRetryTime = nextRetryTime;
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
