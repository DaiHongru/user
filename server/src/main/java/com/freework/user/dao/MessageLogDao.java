package com.freework.user.dao;


import com.freework.user.entity.MessageLog;

import java.util.List;

/**
 * @author daihongru
 */
public interface MessageLogDao {
    /**
     * 修改投递状态
     * messageId，status，lastEditTime不可为空
     *
     * @param messageLog
     * @return
     */
    int updateStatus(MessageLog messageLog);

    /**
     * 添加新的MessageLog
     * 所有字段均不可为空
     *
     * @param messageLog
     * @return
     */
    int insertMessageLog(MessageLog messageLog);

    /**
     * 查询需要重新发送的消息
     *
     * @param tag
     * @return
     */
    List<MessageLog> queryMessageLogByNeedRetry(String tag);

    /**
     * 重新投递消息后更新状态
     * messageId，tryCount，nextRetryTime，lastEditTime不可为空
     *
     * @param messageLog
     * @return
     */
    int updateRetry(MessageLog messageLog);
}
