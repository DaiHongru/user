package com.freework.user.enums;

/**
 * @author daihongru
 */

public enum MessageLogStateEnum {
    /**
     * 枚举字段
     */
    SENDING(0, "投递中"),
    SERVER_ERROR(1, "RabbitMQ服务器异常"),
    SEND_FAIL(2, "投递失败"),
    SUCCESS(1001, "操作成功"),
    ERROR(2001, "操作失败");

    /**
     * 状态表示
     */
    private int state;

    /**
     * 状态说明
     */
    private String stateInfo;

    /**
     * 构造函数，默认private
     *
     * @param state
     * @param stateInfo
     */
    MessageLogStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    /**
     * 依据传入的state返回相应的enum值
     */
    public static MessageLogStateEnum stateOf(int state) {
        for (MessageLogStateEnum stateEnum : values()) {
            if (stateEnum.getState() == state) {
                return stateEnum;
            }
        }
        return null;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }
}
