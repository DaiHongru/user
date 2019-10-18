package com.freework.user.enums;

/**
 * @author daihongru
 */

public enum UserStateEnum {
    /**
     * 枚举字段
     */
    STOP(-1, "账号冻结"),
    CHECKING(0, "审核中"),
    PASS(1, "通过认证");

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
    UserStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    /**
     * 依据传入的state返回相应的enum值
     */
    public static UserStateEnum stateOf(int state) {
        for (UserStateEnum stateEnum : values()) {
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
